package com.applitools.eyes.services;

import com.applitools.ICheckSettingsInternal;
import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.*;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.visualgrid.model.*;
import com.applitools.eyes.visualgrid.services.CheckTask;
import com.applitools.eyes.visualgrid.services.IEyes;
import com.applitools.eyes.visualgrid.services.VisualGridRunningTest;
import com.applitools.utils.GeneralUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class EyesServiceRunner extends Thread {
    private static final String FULLPAGE = "full-page";
    private static final String VIEWPORT = "viewport";

    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    private Throwable error = null;

    private Logger logger;
    private RenderingInfo renderingInfo;

    private final Set<IEyes> allEyes;
    private final Map<String, Pair<FrameData, List<CheckTask>>> resourceCollectionTasksMapping = new HashMap<>();
    private final List<RenderRequest> waitingRenderRequests = new ArrayList<>();
    private final Map<String, CheckTask> waitingCheckTasks = new HashMap<>();

    private final OpenService openService;
    private final CheckService checkService;
    private final CloseService closeService;
    private final ResourceCollectionService resourceCollectionService;
    private final RenderService renderService;

    public EyesServiceRunner(Logger logger, ServerConnector serverConnector, Set<IEyes> allEyes, int testConcurrency,
                             IDebugResourceWriter debugResourceWriter, Map<String, RGridResource> resourcesCacheMap) {
        this.logger = logger;
        this.allEyes = allEyes;

        openService = new OpenService(logger, serverConnector, testConcurrency);
        checkService = new CheckService(logger, serverConnector);
        closeService = new CloseService(logger, serverConnector);
        resourceCollectionService = new ResourceCollectionService(logger, serverConnector, debugResourceWriter, resourcesCacheMap);
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

    public void setAutProxy(AbstractProxySettings proxy) {
        resourceCollectionService.setAutProxy(proxy);
    }

    public AbstractProxySettings getAutProxy() {
        return resourceCollectionService.getAutProxy();
    }

    public void setLogger(Logger logger) {
        openService.setLogger(logger);
        checkService.setLogger(logger);
        closeService.setLogger(logger);
        resourceCollectionService.setLogger(logger);
        renderService.setLogger(logger);
        this.logger = logger;
    }

    public void setServerConnector(ServerConnector serverConnector) {
        openService.setServerConnector(serverConnector);
        checkService.setServerConnector(serverConnector);
        closeService.setServerConnector(serverConnector);
        resourceCollectionService.setServerConnector(serverConnector);
        renderService.setServerConnector(serverConnector);
    }

    public void openTests(Collection<VisualGridRunningTest> runningTests) {
        for (VisualGridRunningTest runningTest : runningTests) {
            openService.addInput(runningTest.getTestId(), runningTest.prepareForOpen());
        }
    }

    public void addResourceCollectionTask(FrameData domData, List<CheckTask> checkTasks) {
        String resourceCollectionTaskId = UUID.randomUUID().toString();
        Set<String> testIds = new HashSet<>();
        for (CheckTask checkTask : checkTasks) {
            testIds.add(checkTask.getTestId());
        }
        domData.setTestIds(testIds);
        resourceCollectionService.addInput(resourceCollectionTaskId, domData);
        resourceCollectionTasksMapping.put(resourceCollectionTaskId, Pair.of(domData, checkTasks));
    }

    @Override
    public void run() {
        try {
            while (isRunning.get()) {
                openServiceIteration();
                resourceCollectionServiceIteration();
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
        for (Pair<String, Map<String, RGridResource>> pair : resourceCollectionService.getSucceededTasks()) {
            Pair<FrameData, List<CheckTask>> checkTasks = resourceCollectionTasksMapping.get(pair.getLeft());
            queueRenderRequests(checkTasks.getLeft(), pair.getRight(), checkTasks.getRight());
        }

        for (Pair<String, Throwable> pair : resourceCollectionService.getFailedTasks()) {
            Pair<FrameData, List<CheckTask>> checkTasks = resourceCollectionTasksMapping.get(pair.getLeft());
            for (CheckTask checkTask : checkTasks.getRight()) {
                checkTask.onFail(pair.getRight());
            }

            resourceCollectionTasksMapping.remove(pair.getLeft());
        }
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

            if (checkTask.isReadyForRender()) {
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

            checkTask.setRenderStatusResults(pair.getRight());
            MatchWindowData matchWindowData = findTestById(checkTask.getTestId()).prepareForMatch(checkTask);
            checkService.addInput(checkTask.getStepId(), matchWindowData);
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

    private void queueRenderRequests(FrameData result, Map<String, RGridResource> resourceMapping, List<CheckTask> checkTasks) {
        RGridDom dom = new RGridDom(result.getCdt(), resourceMapping, result.getUrl());
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
            if (sizeMode.equalsIgnoreCase(VIEWPORT) && checkSettingsInternal.isStitchContent()) {
                sizeMode = FULLPAGE;
            }

            RenderInfo renderInfo = new RenderInfo(browserInfo.getWidth(), browserInfo.getHeight(),
                    sizeMode, checkSettingsInternal.getTargetRegion(), checkSettingsInternal.getVGTargetSelector(),
                    browserInfo.getEmulationInfo(), browserInfo.getIosDeviceInfo());

            RenderRequest request = new RenderRequest(checkTask.getTestId(), this.renderingInfo.getResultsUrl(), result.getUrl(), dom,
                    resourceMapping, renderInfo, browserInfo.getPlatform(), browserInfo.getBrowserType(),
                    checkSettingsInternal.getScriptHooks(), regionSelectorsList, checkSettingsInternal.isSendDom(),
                    checkTask.getRenderer(), checkTask.getStepId(), this.renderingInfo.getStitchingServiceUrl(), checkSettingsInternal.getVisualGridOptions());

            waitingCheckTasks.put(checkTask.getStepId(), checkTask);
            if (checkTask.isReadyForRender()) {
                renderService.addInput(checkTask.getStepId(), request);
            } else {
                waitingRenderRequests.add(request);
            }
        }
    }
}
