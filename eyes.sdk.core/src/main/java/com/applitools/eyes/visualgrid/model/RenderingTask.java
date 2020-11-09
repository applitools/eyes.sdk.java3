package com.applitools.eyes.visualgrid.model;

import com.applitools.ICheckSettings;
import com.applitools.ICheckSettingsInternal;
import com.applitools.eyes.EyesException;
import com.applitools.eyes.Logger;
import com.applitools.eyes.TaskListener;
import com.applitools.eyes.UserAgent;
import com.applitools.eyes.visualgrid.services.IEyesConnector;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.applitools.eyes.visualgrid.services.VisualGridTask;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("WeakerAccess")
public class RenderingTask implements Callable<RenderStatusResults>, CompletableTask {

    private static final int FETCH_TIMEOUT_MS = 60 * 60 * 1000;
    public static final String FULLPAGE = "full-page";
    public static final String VIEWPORT = "viewport";
    public static final int HOUR = 60 * 60 * 1000;

    private final List<RenderTaskListener> listeners = new ArrayList<>();
    private final IEyesConnector eyesConnector;
    private final ICheckSettings checkSettings;
    private final List<VisualGridTask> visualGridTaskList;
    private List<VisualGridTask> openVisualGridTaskList;
    private final RenderingInfo renderingInfo;
    private final UserAgent userAgent;
    final Map<String, RGridResource> fetchedCacheMap;
    final Map<String, RGridResource> putResourceCache;
    private final Logger logger;
    private final AtomicBoolean isTaskComplete = new AtomicBoolean(false);
    private AtomicBoolean isForcePutNeeded;
    private final List<VisualGridSelector[]> regionSelectors;
    private IDebugResourceWriter debugResourceWriter;
    private FrameData domData;
    private RGridDom dom = null;
    private final Timer timer = new Timer("VG_StopWatch", true);
    private final AtomicBoolean isTimeElapsed = new AtomicBoolean(false);

    // Phaser for syncing all futures downloading resources
    Phaser resourcesPhaser = new Phaser();

    // Listener for putResource tasks
    final TaskListener<Boolean> putListener = new TaskListener<Boolean>() {
        @Override
        public void onComplete(Boolean isSucceeded) {
            try {
                if (!isSucceeded) {
                    logger.log("Failed putting resource");
                }
            } finally {
                resourcesPhaser.arriveAndDeregister();
            }
        }

        @Override
        public void onFail() {
            resourcesPhaser.arriveAndDeregister();
            logger.log("Failed putting resource");
        }
    };

    public interface RenderTaskListener {
        void onRenderSuccess();

        void onRenderFailed(Exception e);
    }

    /**
     * For tests only
     */
    RenderingTask(IEyesConnector eyesConnector, List<VisualGridTask> visualGridTaskList, UserAgent userAgent) {
        this(eyesConnector, visualGridTaskList, userAgent, null);
    }

    /**
     * For tests only
     */
    RenderingTask(IEyesConnector eyesConnector, List<VisualGridTask> visualGridTaskList, UserAgent userAgent, ICheckSettings checkSettings) {
        this.eyesConnector = eyesConnector;
        this.visualGridTaskList = visualGridTaskList;
        this.userAgent = userAgent;
        fetchedCacheMap = new HashMap<>();
        logger = new Logger();
        regionSelectors = new ArrayList<>();
        putResourceCache = new HashMap<>();
        this.checkSettings = checkSettings;
        this.renderingInfo = new RenderingInfo();
    }

    public RenderingTask(IEyesConnector eyesConnector, FrameData domData, ICheckSettings checkSettings,
                         List<VisualGridTask> visualGridTaskList, List<VisualGridTask> openVisualGridTasks, VisualGridRunner renderingGridManager,
                         IDebugResourceWriter debugResourceWriter, RenderTaskListener listener, UserAgent userAgent, List<VisualGridSelector[]> regionSelectors) {

        this.eyesConnector = eyesConnector;
        this.domData = domData;
        this.checkSettings = checkSettings;
        this.visualGridTaskList = visualGridTaskList;
        this.openVisualGridTaskList = openVisualGridTasks;
        this.renderingInfo = renderingGridManager.getRenderingInfo();
        this.fetchedCacheMap = renderingGridManager.getCachedResources();
        this.putResourceCache = renderingGridManager.getPutResourceCache();
        this.logger = renderingGridManager.getLogger();
        this.debugResourceWriter = debugResourceWriter;
        this.userAgent = userAgent;
        this.regionSelectors = regionSelectors;
        this.listeners.add(listener);
        String renderingGridForcePut = GeneralUtils.getEnvString("APPLITOOLS_RENDERING_GRID_FORCE_PUT");
        this.isForcePutNeeded = new AtomicBoolean(renderingGridForcePut != null && renderingGridForcePut.equalsIgnoreCase("true"));
    }

    @Override
    public RenderStatusResults call() {
        try {
            addRenderingTaskToOpenTasks();

            logger.verbose("enter");

            logger.verbose("step 1");

            RenderRequest[] requests = prepareDataForRG(domData);

            logger.verbose("step 2");
            boolean stillRunning;
            long elapsedTimeStart = System.currentTimeMillis();
            boolean isForcePutAlreadyDone = false;
            List<RunningRender> runningRenders;
            RenderStatus worstStatus;
            do {
                try {
                    runningRenders = this.eyesConnector.render(requests);
                } catch (Exception e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                    logger.verbose("/render throws exception... sleeping for 1.5s");
                    logger.verbose("ERROR " + e.getMessage());
                    Thread.sleep(1500);
                    try {
                        runningRenders = this.eyesConnector.render(requests);
                    } catch (Exception e1) {
                        setRenderErrorToTasks(requests);
                        throw new EyesException("Invalid response for render request", e1);
                    }
                }

                logger.verbose("step 3.1");
                if (runningRenders == null || runningRenders.size() == 0) {
                    setRenderErrorToTasks(requests);
                    throw new EyesException("Invalid response for render request");
                }

                for (int i = 0; i < requests.length; i++) {
                    RenderRequest request = requests[i];
                    request.setRenderId(runningRenders.get(i).getRenderId());
                    logger.verbose(String.format("RunningRender: %s", runningRenders.get(i)));
                }
                logger.verbose("step 3.2");

                RunningRender runningRender = runningRenders.get(0);
                worstStatus = runningRender.getRenderStatus();
                worstStatus = calcWorstStatus(runningRenders, worstStatus);

                boolean isNeedMoreDom = runningRender.isNeedMoreDom();

                if (isForcePutNeeded.get() && !isForcePutAlreadyDone) {
                    forcePutAllResources(requests[0].getResources(), requests[0].getDom(), runningRender);
                    isForcePutAlreadyDone = true;
                    try {
                        if (resourcesPhaser.getRegisteredParties() > 0) {
                            resourcesPhaser.awaitAdvanceInterruptibly(0, 10, TimeUnit.MINUTES);
                        }
                    } catch (InterruptedException | TimeoutException e) {
                        GeneralUtils.logExceptionStackTrace(logger, e);
                        resourcesPhaser.forceTermination();
                    }
                }

                logger.verbose("step 3.3");
                double elapsedTime = System.currentTimeMillis() - elapsedTimeStart;
                stillRunning = (worstStatus == RenderStatus.NEED_MORE_RESOURCE || isNeedMoreDom) && elapsedTime < FETCH_TIMEOUT_MS;
                if (stillRunning) {
                    sendMissingResources(runningRenders, requests[0].getDom(), requests[0].getResources(), isNeedMoreDom);
                    try {
                        if (resourcesPhaser.getRegisteredParties() > 0) {
                            resourcesPhaser.awaitAdvanceInterruptibly(0, 10, TimeUnit.MINUTES);
                        }
                    } catch (InterruptedException | TimeoutException e) {
                        GeneralUtils.logExceptionStackTrace(logger, e);
                        resourcesPhaser.forceTermination();
                    }
                }

                logger.verbose("step 3.4");

            } while (stillRunning);

            if (worstStatus != RenderStatus.RENDERED && worstStatus != RenderStatus.RENDERING) {
                setRenderErrorToTasks(requests);
                throw new EyesException(String.format("Bad status for render requests: %s", worstStatus));
            }

            Map<RunningRender, RenderRequest> mapping = mapRequestToRunningRender(runningRenders, requests);

            logger.verbose("step 4");
            pollRenderingStatus(mapping);
        } catch (Throwable e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
            for (VisualGridTask visualGridTask : this.visualGridTaskList) {
                visualGridTask.setExceptionAndAbort(e);
            }
        }
        logger.verbose("Finished rendering task - exit");

        return null;
    }

    private void addRenderingTaskToOpenTasks() {
        if (this.openVisualGridTaskList != null) {
            for (VisualGridTask visualGridTask : openVisualGridTaskList) {
                visualGridTask.setRenderingTask(this);
            }
        }
    }

    private void forcePutAllResources(Map<String, RGridResource> resources, RGridDom dom, RunningRender runningRender) {
        resourcesPhaser = new Phaser();
        Set<String> strings = resources.keySet();
        try {
            resourcesPhaser.register();
            this.eyesConnector.renderPutResource(runningRender, dom.asResource(), userAgent.getOriginalUserAgentString(), putListener);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        for (String url : strings) {
            try {
                logger.verbose("trying to get url from map - " + url);
                RGridResource resource = null;
                if (!fetchedCacheMap.containsKey(url)) {
                    if (url.equals(this.dom.getUrl())) {
                        resource = this.dom.asResource();
                    }
                } else {
                    resource = fetchedCacheMap.get(url);
                }

                if (resource == null) {
                    logger.log(String.format("Illegal state: resource is null for url %s", url));
                    continue;
                }

                resourcesPhaser.register();
                this.eyesConnector.renderPutResource(runningRender, resource, userAgent.getOriginalUserAgentString(), putListener);
                logger.verbose("locking putResourceCache");
                synchronized (putResourceCache) {
                    String contentType = resource.getContentType();
                    if (contentType != null && !contentType.equalsIgnoreCase(RGridDom.CONTENT_TYPE)) {
                        putResourceCache.put(url, resource);
                    }
                }
            } catch (Exception e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
            }
        }
    }

    private void setRenderErrorToTasks(RenderRequest[] requests) {
        for (RenderRequest renderRequest : requests) {
            for (VisualGridTask openTask : openVisualGridTaskList) {
                if (openTask.getRunningTest() == renderRequest.getVisualGridTask().getRunningTest()) {
                    openTask.setRenderError(null, "Invalid response for render request", renderRequest);
                }
            }
            renderRequest.getVisualGridTask().setRenderError(null, "Invalid response for render request", renderRequest);
        }
    }

    private void notifySuccessAllListeners() {
        for (RenderTaskListener listener : listeners) {
            listener.onRenderSuccess();
        }
    }

    private Map<RunningRender, RenderRequest> mapRequestToRunningRender(List<RunningRender> runningRenders, RenderRequest[] requests) {
        Map<RunningRender, RenderRequest> mapping = new HashMap<>();
        for (int i = 0; i < requests.length; i++) {
            RenderRequest request = requests[i];
            RunningRender runningRender = runningRenders.get(i);
            mapping.put(runningRender, request);
        }
        return mapping;
    }

    private RenderStatus calcWorstStatus(List<RunningRender> runningRenders, RenderStatus worstStatus) {
        LOOP:
        for (RunningRender runningRender : runningRenders) {
            switch (runningRender.getRenderStatus()) {
                case NEED_MORE_RESOURCE:
                    if (worstStatus == RenderStatus.RENDERED || worstStatus == RenderStatus.RENDERING) {
                        worstStatus = RenderStatus.NEED_MORE_RESOURCE;
                    }
                    break;
                case ERROR:
                    worstStatus = RenderStatus.ERROR;
                    break LOOP;
            }
        }
        return worstStatus;
    }

    private List<String> getRenderIds(Collection<RunningRender> runningRenders) {
        List<String> ids = new ArrayList<>();
        for (RunningRender runningRender : runningRenders) {
            ids.add(runningRender.getRenderId());
        }
        return ids;
    }

    private void sendMissingResources(List<RunningRender> runningRenders, RGridDom dom, Map<String, RGridResource> resources, boolean isNeedMoreDom) {
        logger.verbose("enter");
        resourcesPhaser = new Phaser();
        if (isNeedMoreDom) {
            RunningRender runningRender = runningRenders.get(0);
            try {
                resourcesPhaser.register();
                this.eyesConnector.renderPutResource(runningRender, dom.asResource(), userAgent.getOriginalUserAgentString(), putListener);
            } catch (Throwable e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
            }
        }

        logger.verbose("creating PutFutures for " + runningRenders.size() + " runningRenders");

        for (RunningRender runningRender : runningRenders) {
            createPutFutures(runningRender, resources);
        }
        logger.verbose("exit");
    }

    void createPutFutures(RunningRender runningRender, Map<String, RGridResource> resources) {
        List<String> needMoreResources = runningRender.getNeedMoreResources();
        for (String url : needMoreResources) {
            if (putResourceCache.containsKey(url)) {
                continue;
            }

            RGridResource resource;
            if (!fetchedCacheMap.containsKey(url)) {
                logger.verbose(String.format("Resource %s requested but never downloaded (maybe a Frame)", url));
                resource = resources.get(url);
            } else {
                resource = fetchedCacheMap.get(url);
            }

            if (resource == null) {
                logger.log(String.format("Illegal state: resource is null for url %s", url));
                continue;
            }
            logger.verbose("resource(" + resource.getUrl() + ") hash : " + resource.getSha256());
            resourcesPhaser.register();
            this.eyesConnector.renderPutResource(runningRender, resource, userAgent.getOriginalUserAgentString(), putListener);
            String contentType = resource.getContentType();
            if (!putResourceCache.containsKey(url) && (contentType != null && !contentType.equalsIgnoreCase(RGridDom.CONTENT_TYPE))) {
                synchronized (putResourceCache) {
                    putResourceCache.put(url, resource);
                }
            }
        }
    }

    RenderRequest[] prepareDataForRG(FrameData domData) {
        DomAnalyzer domAnalyzer = new DomAnalyzer(logger, eyesConnector.getServerConnector(), debugResourceWriter, domData, fetchedCacheMap, userAgent);

        Map<String, RGridResource> resourceMap = domAnalyzer.analyze();

        List<RenderRequest> allRequestsForRG = buildRenderRequests(domData, resourceMap);

        RenderRequest[] asArray = allRequestsForRG.toArray(new RenderRequest[0]);

        if (debugResourceWriter != null && !(debugResourceWriter instanceof NullDebugResourceWriter)) {
            for (RenderRequest renderRequest : asArray) {
                try {
                    debugResourceWriter.write(renderRequest.getDom().asResource());
                } catch (JsonProcessingException e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                }
                for (RGridResource value : renderRequest.getResources().values()) {
                    this.debugResourceWriter.write(value);
                }
            }
        }

        logger.verbose("exit - returning renderRequest array of length: " + asArray.length);
        return asArray;
    }

    private List<RenderRequest> buildRenderRequests(FrameData result, Map<String, RGridResource> resourceMapping) {

        RGridDom dom = new RGridDom(result.getCdt(), resourceMapping, result.getUrl(), logger, "buildRenderRequests");

        this.dom = dom;

        //Create RG requests
        List<RenderRequest> allRequestsForRG = new ArrayList<>();
        ICheckSettingsInternal checkSettingsInternal = (ICheckSettingsInternal) this.checkSettings;


        List<VisualGridSelector> regionSelectorsList = new ArrayList<>();

        for (VisualGridSelector[] regionSelector : this.regionSelectors) {
            regionSelectorsList.addAll(Arrays.asList(regionSelector));
        }

        logger.verbose("region selectors count: " + regionSelectorsList.size());
        logger.verbose("this.visualGridTaskList count: " + this.visualGridTaskList.size());

        for (VisualGridTask visualGridTask : this.visualGridTaskList) {

            RenderBrowserInfo browserInfo = visualGridTask.getBrowserInfo();

            String sizeMode = checkSettingsInternal.getSizeMode();

            if (sizeMode.equalsIgnoreCase(VIEWPORT) && checkSettingsInternal.isStitchContent()) {
                sizeMode = FULLPAGE;
            }

            RenderInfo renderInfo = new RenderInfo(browserInfo.getWidth(), browserInfo.getHeight(),
                    sizeMode, checkSettingsInternal.getTargetRegion(), checkSettingsInternal.getVGTargetSelector(),
                    browserInfo.getEmulationInfo(), browserInfo.getIosDeviceInfo());

            RenderRequest request = new RenderRequest(this.renderingInfo.getResultsUrl(), result.getUrl(), dom,
                    resourceMapping, renderInfo, browserInfo.getPlatform(), browserInfo.getBrowserType(),
                    checkSettingsInternal.getScriptHooks(), regionSelectorsList, checkSettingsInternal.isSendDom(),
                    visualGridTask, this.renderingInfo.getStitchingServiceUrl(), checkSettingsInternal.getVisualGridOptions());

            allRequestsForRG.add(request);
        }

        logger.verbose("count of all requests for RG: " + allRequestsForRG.size());
        return allRequestsForRG;
    }

    private void pollRenderingStatus(Map<RunningRender, RenderRequest> runningRenders) {
        logger.verbose("enter");
        List<String> ids = getRenderIds(runningRenders.keySet());
        logger.verbose("render ids : " + ids);
        timer.schedule(new TimeoutTask(), HOUR);
        do {
            List<RenderStatusResults> renderStatusResultsList;
            try {
                renderStatusResultsList = this.eyesConnector.renderStatusById(ids.toArray(new String[0]));
            } catch (Exception e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
                continue;
            }
            if (renderStatusResultsList == null || renderStatusResultsList.isEmpty()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                }
                continue;
            }

            sampleRenderingStatus(runningRenders, ids, renderStatusResultsList);

            if (ids.size() > 0) {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                }
            }

        } while (!ids.isEmpty() && !isTimeElapsed.get());

        timer.cancel();
        if (!ids.isEmpty()) {
            logger.verbose("Render ids that didn't complete in time : ");
            logger.verbose(ids.toString());
        }

        for (String id : ids) {
            for (Map.Entry<RunningRender,RenderRequest> kvp : runningRenders.entrySet()) {
                RunningRender renderedRender = kvp.getKey();
                RenderRequest renderRequest = kvp.getValue();
                String renderId = renderedRender.getRenderId();
                if (renderId.equalsIgnoreCase(id)) {
                    logger.verbose("removing failed render id: " + id);
                    VisualGridTask visualGridTask = runningRenders.get(renderedRender).getVisualGridTask();
                    visualGridTask.setRenderError(id, "too long rendering(rendering exceeded 150 sec)", renderRequest);
                    break;
                }
            }
        }

        ICheckSettingsInternal rcInternal = (ICheckSettingsInternal) checkSettings;
        logger.verbose("marking task as complete: " + rcInternal.getName());
        this.isTaskComplete.set(true);
        this.notifySuccessAllListeners();
        logger.verbose("exit");
    }

    private void sampleRenderingStatus(Map<RunningRender, RenderRequest> runningRenders, List<String> ids, List<RenderStatusResults> renderStatusResultsList) {
        logger.verbose("enter - renderStatusResultsList.size: " + renderStatusResultsList.size());

        for (int i = 0, j = 0; i < renderStatusResultsList.size(); i++) {
            RenderStatusResults renderStatusResults = renderStatusResultsList.get(i);
            if (renderStatusResults == null) {
                renderStatusResults = new RenderStatusResults();
                renderStatusResults.setStatus(RenderStatus.ERROR);
                renderStatusResults.setError("Render status result was null");
                renderStatusResults.setRenderId(ids.get(j));
            }

            RenderStatus renderStatus = renderStatusResults.getStatus();
            boolean isRenderedStatus = renderStatus == RenderStatus.RENDERED;
            boolean isErrorStatus = renderStatus == RenderStatus.ERROR;
            logger.verbose("renderStatusResults - " + renderStatusResults);
            if (isRenderedStatus || isErrorStatus) {
                String removedId = ids.remove(j);
                for (Map.Entry<RunningRender, RenderRequest> kvp: runningRenders.entrySet()) {
                    RunningRender renderedRender = kvp.getKey();
                    RenderRequest renderRequest = kvp.getValue();
                    String renderId = renderedRender.getRenderId();
                    if (renderId.equalsIgnoreCase(removedId)) {
                        VisualGridTask visualGridTask = runningRenders.get(renderedRender).getVisualGridTask();
                        Iterator<VisualGridTask> iterator = openVisualGridTaskList.iterator();
                        while (iterator.hasNext()) {
                            VisualGridTask openVisualGridTask = iterator.next();
                            if (openVisualGridTask.getRunningTest() == visualGridTask.getRunningTest()) {
                                if (isRenderedStatus) {
                                    logger.verbose("setting openVisualGridTask " + openVisualGridTask + " render result: " + renderStatusResults + " to url " + this.domData.getUrl());
                                    openVisualGridTask.setRenderResult(renderStatusResults);
                                } else {
                                    logger.verbose("setting openVisualGridTask " + openVisualGridTask + " render error: " + removedId + " to url " + this.domData.getUrl());
                                    openVisualGridTask.setRenderError(removedId, renderStatusResults.getError(), renderRequest);
                                }
                                iterator.remove();
                            }
                        }
                        logger.verbose("setting visualGridTask " + visualGridTask + " render result: " + renderStatusResults + " to url " + this.domData.getUrl());
                        String error = renderStatusResults.getError();
                        if (error != null) {
                            GeneralUtils.logExceptionStackTrace(logger, new Exception(error));
                            visualGridTask.setRenderError(renderId, error, renderRequest);
                        } else {
                            visualGridTask.setRenderResult(renderStatusResults);
                        }
                        break;
                    }
                }
            } else {
                j++;
            }
        }
        logger.verbose("exit");
    }

    public boolean getIsTaskComplete() {
        return isTaskComplete.get();
    }

    public void addListener(RenderTaskListener listener) {
        this.listeners.add(listener);
    }


    private class TimeoutTask extends TimerTask {
        @Override
        public void run() {
            logger.verbose("VG is Timed out!");
            isTimeElapsed.set(true);
        }
    }
}
