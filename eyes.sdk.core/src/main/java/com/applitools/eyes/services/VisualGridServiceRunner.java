package com.applitools.eyes.services;

import com.applitools.ICheckSettingsInternal;
import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.*;
import com.applitools.eyes.fluent.CheckSettings;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.visualgrid.model.*;
import com.applitools.eyes.visualgrid.services.CheckTask;
import com.applitools.eyes.visualgrid.services.IEyes;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class VisualGridServiceRunner extends Thread {
    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    private Throwable error = null;

    private Logger logger;
    private RenderingInfo renderingInfo;

    private final Set<IEyes> allEyes;

    // Contains all the waiting web resource collection tasks (including resource uploading)
    private final Map<String, Pair<FrameData, List<CheckTask>>> webResourceCollectionTasksMapping = new HashMap<>();

    // Contains all the waiting native resource collection tasks (including resource uploading)
    private final Map<String, Pair<MobileResourceMap, List<CheckTask>>> nativeResourceCollectionTasksMapping = new HashMap<>();

    // Contains web resource collection tasks which are part of a native resource collection tasks (webview for example)
    private final Map<String, String> webTasksToNativeTasks = new HashMap<>();
    private final List<RenderRequest> waitingRenderRequests = new ArrayList<>();
    private final Map<String, CheckTask> waitingCheckTasks = new HashMap<>();

    private final OpenService openService;
    private final CheckService checkService;
    private final CloseService closeService;
    private final ResourceCollectionService resourceCollectionService;
    private final PutResourceService putResourceService;
    private final RenderService renderService;

    public VisualGridServiceRunner(Logger logger, ServerConnector serverConnector, Set<IEyes> allEyes, int testConcurrency,
                                   IDebugResourceWriter debugResourceWriter, Map<String, RGridResource> resourcesCacheMap) {
        this.logger = logger;
        this.allEyes = allEyes;

        openService = new OpenService(logger, serverConnector, testConcurrency);
        checkService = new CheckService(logger, serverConnector);
        closeService = new CloseService(logger, serverConnector);
        resourceCollectionService = new ResourceCollectionService(logger, serverConnector, debugResourceWriter, resourcesCacheMap);
        putResourceService = new PutResourceService(logger, serverConnector);
        renderService = new RenderService(logger, serverConnector);
    }

    public void setRenderingInfo(RenderingInfo renderingInfo) {
        if (renderingInfo != null) {
            this.renderingInfo = renderingInfo;
        }
    }

    public void setDebugResourceWriter(IDebugResourceWriter debugResourceWriter) {
        resourceCollectionService.setDebugResourceWriter(debugResourceWriter);
    }

    public void setAutProxy(AbstractProxySettings proxy, String[] autProxyDomains, RunnerOptions.AutProxyMode mode) {
        boolean isWhiteList = mode == null || mode.equals(RunnerOptions.AutProxyMode.ALLOW);
        resourceCollectionService.setAutProxy(proxy, autProxyDomains, isWhiteList);
    }

    public AbstractProxySettings getDefaultResourcesProxy() {
        return resourceCollectionService.defaultResourcesConnector.getProxy();
    }

    public AbstractProxySettings getCustomResourcesProxy() {
        return resourceCollectionService.customResourcesConnector.getProxy();
    }

    public void setLogger(Logger logger) {
        openService.setLogger(logger);
        checkService.setLogger(logger);
        closeService.setLogger(logger);
        resourceCollectionService.setLogger(logger);
        putResourceService.setLogger(logger);
        renderService.setLogger(logger);
        this.logger = logger;
    }

    public void setServerConnector(ServerConnector serverConnector) {
        openService.setServerConnector(serverConnector);
        checkService.setServerConnector(serverConnector);
        closeService.setServerConnector(serverConnector);
        resourceCollectionService.setServerConnector(serverConnector);
        putResourceService.setServerConnector(serverConnector);
        renderService.setServerConnector(serverConnector);
    }

    public void openTests(Collection<RunningTest> runningTests) {
        for (RunningTest runningTest : runningTests) {
            openService.addInput(runningTest.getTestId(), runningTest.prepareForOpen());
        }
    }

    public void addResourceCollectionTask(FrameData domData, List<CheckTask> checkTasks) {
        String resourceCollectionTaskId = UUID.randomUUID().toString();
        Set<String> testIds = new HashSet<>();
        testIds.add(resourceCollectionTaskId);
        for (CheckTask checkTask : checkTasks) {
            testIds.add(checkTask.getTestId());
        }
        domData.setTestIds(testIds);
        resourceCollectionService.addInput(resourceCollectionTaskId, domData);
        webResourceCollectionTasksMapping.put(resourceCollectionTaskId, Pair.of(domData, checkTasks));
        logger.log(testIds, Stage.RESOURCE_COLLECTION, Pair.of("resourceCollectionTaskId", resourceCollectionTaskId));
    }

    public void addNativeMobileResources(byte[] vhs, Map<String, FrameData> resources, String contentType, List<CheckTask> checkTasks, VhsCompatibilityParams vhsCompatibilityParams) {
        String resourceCollectionTaskId = UUID.randomUUID().toString();
        Set<String> testIds = new HashSet<>();
        for (CheckTask checkTask : checkTasks) {
            testIds.add(checkTask.getTestId());
        }

        RGridResource vhsResource = new RGridResource("vhs", contentType, vhs);
        MobileResourceMap mobileResourceMap = new MobileResourceMap(vhsResource, checkTasks.get(0).getBrowserInfo(), resources.size());
        nativeResourceCollectionTasksMapping.put(resourceCollectionTaskId, Pair.of(mobileResourceMap, checkTasks));
        if (checkTasks.get(0).getBrowserInfo().getPlatform().equals("ios")) {
            // Eventually IOS case will be the same as android case
            RGridDom dom = new RGridDom(vhsResource);
            dom.setTestIds(testIds);
            dom.setVhsCompatibilityParams(vhsCompatibilityParams);
            putResourceService.addInput(resourceCollectionTaskId, dom);
        } else {
            if (resources.size() == 0) {
                putResourceService.addInput(resourceCollectionTaskId, new RGridDom(mobileResourceMap));
            }
            for (Map.Entry<String, FrameData> entry: resources.entrySet()) {
                // Add all web resource collection tasks belong to this native resource collection task
                entry.getValue().setTestIds(testIds);
                resourceCollectionService.addInput(entry.getKey(), entry.getValue());
                webResourceCollectionTasksMapping.put(entry.getKey(), Pair.of(entry.getValue(), checkTasks));
                webTasksToNativeTasks.put(entry.getKey(), resourceCollectionTaskId);
            }
        }
    }

    @Override
    public void run() {
        try {
            while (isRunning.get()) {
                openServiceIteration();
                resourceCollectionServiceIteration();
                putResourcesServiceIteration();
                renderServiceIteration();
                checkServiceIteration();
                closeServiceIteration();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {}
            }
        } catch (Throwable e) {
            isRunning.set(false);
            error = e;
            GeneralUtils.logExceptionStackTrace(logger, Stage.GENERAL, e);
        }
    }

    public Throwable getError() {
        return error;
    }

    public void stopServices() {
        isRunning.set(false);
    }

    private void openServiceIteration() {
        openService.run();
        for (Pair<String, RunningSession> pair : openService.getSucceededTasks()) {
            findTestById(pair.getLeft()).openCompleted(pair.getRight());
        }
        for (Pair<String, Throwable> pair : openService.getFailedTasks()) {
            findTestById(pair.getLeft()).openFailed(pair.getRight());
        }
    }

    private void checkServiceIteration() {
        for (CheckTask checkTask : waitingCheckTasks.values()) {
            if (checkTask.isTestActive() && checkTask.isReadyForMatch()) {
                MatchWindowData matchWindowData = checkTask.startMatch();
                checkService.addInput(checkTask.getStepId(), matchWindowData);
            }
        }

        checkService.run();
        for (Pair<String, MatchResult> pair : checkService.getSucceededTasks()) {
            CheckTask checkTask = waitingCheckTasks.remove(pair.getLeft());
            if (!checkTask.isTestActive()) {
                continue;
            }

            checkTask.onComplete(pair.getRight());
        }

        for (Pair<String, Throwable> pair : checkService.getFailedTasks()) {
            CheckTask checkTask = waitingCheckTasks.remove(pair.getLeft());
            checkTask.onFail(pair.getRight());
        }
    }

    private void closeServiceIteration() {
        // Check if tests are ready to be closed
        synchronized (allEyes) {
            for (IEyes eyes : allEyes) {
                synchronized (eyes.getAllRunningTests()) {
                    for (RunningTest runningTest : eyes.getAllRunningTests().values()) {
                        if (runningTest.isTestReadyToClose()) {
                            if (!runningTest.getIsOpen()) {
                                // If the test isn't open and is ready to close, it means the open failed
                                openService.decrementConcurrency();
                                runningTest.closeFailed(new EyesException("Eyes never opened"));
                                continue;
                            }

                            SessionStopInfo sessionStopInfo = runningTest.prepareStopSession(runningTest.isTestAborted());
                            closeService.addInput(runningTest.getTestId(), sessionStopInfo);
                        }
                    }
                }
            }
        }

        closeService.run();
        for (Pair<String, TestResults> pair : closeService.getSucceededTasks()) {
            RunningTest runningTest = findTestById(pair.getLeft());
            runningTest.closeCompleted(pair.getRight());
            openService.decrementConcurrency();
        }

        for (Pair<String, Throwable> pair : closeService.getFailedTasks()) {
            RunningTest runningTest = findTestById(pair.getLeft());
            runningTest.closeFailed(pair.getRight());
            openService.decrementConcurrency();
        }
    }

    private void resourceCollectionServiceIteration() {
        resourceCollectionService.run();
        if (resourceCollectionService.outputQueue.isEmpty() && resourceCollectionService.errorQueue.isEmpty()) {
            return;
        }

        for (Pair<String, RGridDom> pair : resourceCollectionService.getSucceededTasks()) {
            putResourceService.addInput(pair.getLeft(), pair.getRight());
        }

        for (Pair<String, Throwable> pair : resourceCollectionService.getFailedTasks()) {
            Pair<FrameData, List<CheckTask>> checkTasks = webResourceCollectionTasksMapping.get(pair.getLeft());
            for (CheckTask checkTask : checkTasks.getRight()) {
                checkTask.onFail(pair.getRight());
            }

            webResourceCollectionTasksMapping.remove(pair.getLeft());
            if (webTasksToNativeTasks.containsKey(pair.getLeft())) {
                // If this web task belongs to a native task, remove also the native task
                String resourceCollectionTaskId = webTasksToNativeTasks.remove(pair.getLeft());
                nativeResourceCollectionTasksMapping.remove(resourceCollectionTaskId);
            }
        }

        System.gc();
    }

    private void putResourcesServiceIteration() throws JsonProcessingException {
        putResourceService.run();
        if (putResourceService.outputQueue.isEmpty() && putResourceService.errorQueue.isEmpty()) {
            return;
        }

        for (Pair<String, RGridDom> pair : putResourceService.getSucceededTasks()) {
            if (webResourceCollectionTasksMapping.containsKey(pair.getLeft())) {
                Pair<FrameData, List<CheckTask>> checkTasks = webResourceCollectionTasksMapping.get(pair.getLeft());
                if (webTasksToNativeTasks.containsKey(pair.getLeft())) {
                    // If this web task belongs to a native task, add the dom as a resource to the mobile resource map
                    String resourceCollectionTaskId = webTasksToNativeTasks.remove(pair.getLeft());
                    if (nativeResourceCollectionTasksMapping.containsKey(resourceCollectionTaskId)) {
                        MobileResourceMap mobileResourceMap = nativeResourceCollectionTasksMapping.get(resourceCollectionTaskId).getLeft();
                        mobileResourceMap.addResource(pair.getLeft(), pair.getRight().asResource());
                        if (mobileResourceMap.getNumOfResources() == mobileResourceMap.getResources().size()) {
                            // If this native task has all the required web DOMs, we can upload the native snapshot and start a render request
                            RGridDom snapshot = new RGridDom(mobileResourceMap);
                            putResourceService.addInput(resourceCollectionTaskId, snapshot);
                        }
                    }
                } else {
                    queueRenderRequests(checkTasks.getLeft(), pair.getRight(), checkTasks.getRight());
                }
                webResourceCollectionTasksMapping.remove(pair.getLeft());
            } else {
                List<CheckTask> checkTasks = nativeResourceCollectionTasksMapping.get(pair.getLeft()).getRight();
                queueRenderRequests(pair.getRight(), checkTasks);
                nativeResourceCollectionTasksMapping.remove(pair.getLeft());
            }
        }

        for (Pair<String, Throwable> pair : putResourceService.getFailedTasks()) {
            List<CheckTask> tasksToFail;
            if (webResourceCollectionTasksMapping.containsKey(pair.getLeft())) {
                tasksToFail = webResourceCollectionTasksMapping.get(pair.getLeft()).getRight();
                webResourceCollectionTasksMapping.remove(pair.getLeft());
                if (webTasksToNativeTasks.containsKey(pair.getLeft())) {
                    // If this web task belongs to a native task, remove also the native task
                    String resourceCollectionTaskId = webTasksToNativeTasks.remove(pair.getLeft());
                    nativeResourceCollectionTasksMapping.remove(resourceCollectionTaskId);
                }
            } else {
                tasksToFail = nativeResourceCollectionTasksMapping.get(pair.getLeft()).getRight();
                nativeResourceCollectionTasksMapping.remove(pair.getLeft());
            }

            for (CheckTask checkTask : tasksToFail) {
                checkTask.onFail(pair.getRight());
            }
        }

        System.gc();
    }

    private void renderServiceIteration() {
        // Check if render requests are ready to start
        List<RenderRequest> renderRequestsToRemove = new ArrayList<>();
        for (RenderRequest renderRequest : waitingRenderRequests) {
            CheckTask checkTask = waitingCheckTasks.get(renderRequest.getStepId());
            if (!checkTask.isTestActive()) {
                waitingCheckTasks.remove(checkTask.getStepId());
                renderRequestsToRemove.add(renderRequest);
                continue;
            }

            if (checkTask.isReady()) {
                renderService.addInput(checkTask.getStepId(), renderRequest);
                renderRequestsToRemove.add(renderRequest);
            }
        }

        waitingRenderRequests.removeAll(renderRequestsToRemove);

        renderService.run();
        for (Pair<String, RenderStatusResults> pair : renderService.getSucceededTasks()) {
            CheckTask checkTask = waitingCheckTasks.get(pair.getLeft());
            if (!checkTask.isTestActive()) {
                waitingCheckTasks.remove(pair.getLeft());
                continue;
            }

            checkTask.setAppOutput(pair.getRight());
        }

        for (Pair<String, Throwable> pair : renderService.getFailedTasks()) {
            CheckTask checkTask = waitingCheckTasks.remove(pair.getLeft());
            checkTask.onFail(pair.getRight());
        }
    }

    private RunningTest findTestById(String testId) {
        synchronized (allEyes) {
            for (IEyes eyes : allEyes) {
                if (eyes.getAllRunningTests().containsKey(testId)) {
                    return eyes.getAllRunningTests().get(testId);
                }
            }
        }

        throw new IllegalStateException(String.format("Didn't find test id %s", testId));
    }

    private void queueRenderRequests(FrameData result, RGridDom dom, List<CheckTask> checkTasks) {
        ICheckSettingsInternal checkSettingsInternal = (ICheckSettingsInternal) checkTasks.get(0).getCheckSettings();
        List<VisualGridSelector> regionSelectorsList = new ArrayList<>();
        for (VisualGridSelector[] regionSelector : checkTasks.get(0).getRegionSelectors()) {
            regionSelectorsList.addAll(Arrays.asList(regionSelector));
        }

        for (CheckTask checkTask : checkTasks) {
            if (!checkTask.isTestActive()) {
                continue;
            }

            RenderBrowserInfo browserInfo = checkTask.getBrowserInfo();
            String sizeMode = checkSettingsInternal.getSizeMode();
            if (sizeMode.equalsIgnoreCase(CheckSettings.VIEWPORT) && checkSettingsInternal.isStitchContent()) {
                sizeMode = CheckSettings.FULL_PAGE;
            }

            RenderInfo renderInfo = new RenderInfo(browserInfo.getWidth(), browserInfo.getHeight(),
                    sizeMode, checkSettingsInternal.getTargetRegion(), checkSettingsInternal.getVGTargetSelector(),
                    browserInfo.getEmulationInfo(), browserInfo.getIosDeviceInfo(), browserInfo.getAndroidDeviceInfo(), dom.getVhsCompatibilityParams());

            RenderRequest request = new RenderRequest(checkTask.getTestId(), this.renderingInfo.getResultsUrl(), result.getUrl(), dom,
                    dom.getResources(), renderInfo, browserInfo.getPlatform(), "web", browserInfo.getBrowserType(),
                    checkSettingsInternal.getScriptHooks(), regionSelectorsList, checkSettingsInternal.isSendDom(),
                    checkTask.getRenderer(), checkTask.getStepId(), this.renderingInfo.getStitchingServiceUrl(), checkSettingsInternal.getVisualGridOptions(), checkTask.getAgentId());

            waitingCheckTasks.put(checkTask.getStepId(), checkTask);
            if (checkTask.isReady()) {
                renderService.addInput(checkTask.getStepId(), request);
            } else {
                waitingRenderRequests.add(request);
            }
        }
    }

    private void queueRenderRequests(RGridDom dom, List<CheckTask> checkTasks) {
        for (CheckTask checkTask : checkTasks) {
            if (!checkTask.isTestActive()) {
                continue;
            }

            ICheckSettingsInternal checkSettingsInternal = (ICheckSettingsInternal) checkTasks.get(0).getCheckSettings();
            RenderBrowserInfo deviceInfo = checkTask.getBrowserInfo();
            RenderInfo renderInfo = new RenderInfo(deviceInfo.getWidth(), deviceInfo.getHeight(), checkSettingsInternal.getSizeMode(),
                    null, null, null, deviceInfo.getIosDeviceInfo(), deviceInfo.getAndroidDeviceInfo(), dom.getVhsCompatibilityParams());

            RenderRequest request = new RenderRequest(checkTask.getTestId(), this.renderingInfo.getResultsUrl(), dom, dom.getResources(),
                    renderInfo, deviceInfo.getPlatform(), "native", checkSettingsInternal.getScriptHooks(), checkTask.getRenderer(),
                    checkTask.getStepId(), this.renderingInfo.getStitchingServiceUrl(), checkTask.getAgentId(), checkSettingsInternal.getVisualGridOptions());

            waitingCheckTasks.put(checkTask.getStepId(), checkTask);
            if (checkTask.isReady()) {
                renderService.addInput(checkTask.getStepId(), request);
            } else {
                waitingRenderRequests.add(request);
            }
        }
    }
}
