package com.applitools.eyes.visualgrid.model;

import com.applitools.ICheckSettings;
import com.applitools.ICheckSettingsInternal;
import com.applitools.eyes.*;
import com.applitools.eyes.visualgrid.services.IEyesConnector;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.applitools.eyes.visualgrid.services.VisualGridTask;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.*;
import java.util.concurrent.Callable;

public class ResourceCollectionTask implements Callable<TestResultContainer> {

    public static final String FULLPAGE = "full-page";
    public static final String VIEWPORT = "viewport";

    private final Logger logger;
    private final IEyesConnector eyesConnector;
    private final FrameData domData;
    private final UserAgent userAgent;
    private final RenderingInfo renderingInfo;
    private final List<VisualGridSelector[]> regionSelectors;
    private final ICheckSettings checkSettings;
    private final List<VisualGridTask> checkTasks;
    final Map<String, RGridResource> resourcesCacheMap;
    final Map<String, SyncTaskListener<Void>> uploadedResourcesCache;
    private IDebugResourceWriter debugResourceWriter;
    private final TaskListener<List<RenderingTask>> listener;
    private RenderingTask.RenderTaskListener renderTaskListener;

    /**
     * For tests only
     */
    public ResourceCollectionTask(IEyesConnector eyesConnector, List<VisualGridTask> checkTasks, FrameData domData,
                                  UserAgent userAgent, ICheckSettings checkSettings, TaskListener<List<RenderingTask>> listTaskListener) {
        this.eyesConnector = eyesConnector;
        this.checkTasks = checkTasks;
        this.domData = domData;
        this.userAgent = userAgent;
        this.checkSettings = checkSettings;
        this.listener = listTaskListener;
        this.resourcesCacheMap = new HashMap<>();
        this.uploadedResourcesCache = new HashMap<>();
        this.logger = new Logger();
        this.regionSelectors = new ArrayList<>();
        this.renderingInfo = new RenderingInfo();
    }

    public ResourceCollectionTask(VisualGridRunner runner, IEyesConnector eyesConnector, FrameData domData,
                                  UserAgent userAgent, List<VisualGridSelector[]> regionSelectors,
                                  ICheckSettings checkSettings, List<VisualGridTask> checkTasks,
                                  IDebugResourceWriter debugResourceWriter, TaskListener<List<RenderingTask>> listener,
                                  RenderingTask.RenderTaskListener renderTaskListener) {
        ArgumentGuard.notNull(checkTasks, "checkTasks");
        ArgumentGuard.notEqual(checkTasks.size(), 0, "checkTasks");
        this.logger = runner.getLogger();
        this.eyesConnector = eyesConnector;
        this.resourcesCacheMap = runner.getResourcesCacheMap();
        this.uploadedResourcesCache = runner.getUploadedResourcesCache();
        this.domData = domData;
        this.userAgent = userAgent;
        this.checkSettings = checkSettings;
        this.checkTasks = checkTasks;
        this.renderingInfo = runner.getRenderingInfo();
        this.regionSelectors = regionSelectors;
        this.debugResourceWriter = debugResourceWriter;
        this.listener = listener;
        this.renderTaskListener = renderTaskListener;
    }

    @Override
    public TestResultContainer call() {
        try {
            DomAnalyzer domAnalyzer = new DomAnalyzer(logger, eyesConnector.getServerConnector(),
                    debugResourceWriter, domData, resourcesCacheMap, userAgent);
            Map<String, RGridResource> resourceMap = domAnalyzer.analyze();
            List<RenderRequest> renderRequests = buildRenderRequests(domData, resourceMap);
            if (debugResourceWriter != null && !(debugResourceWriter instanceof NullDebugResourceWriter)) {
                for (RenderRequest renderRequest : renderRequests) {
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

            logger.verbose("Uploading missing resources");
            List<RGridResource> missingResources = checkResourcesStatus(renderRequests.get(0).getDom(), resourceMap);
            uploadResources(missingResources);

            List<RenderingTask> renderingTasks = new ArrayList<>();
            for (int i = 0; i < renderRequests.size(); i++) {
                VisualGridTask checkTask = checkTasks.get(i);
                checkTask.setRenderTaskCreated();
                renderingTasks.add(new RenderingTask(logger, eyesConnector, renderRequests.get(i), checkTask, renderTaskListener));
            }

            listener.onComplete(renderingTasks);
        } catch (Throwable t) {
            GeneralUtils.logExceptionStackTrace(logger, t);
            for (VisualGridTask checkTask : checkTasks) {
                checkTask.setExceptionAndAbort(t);
            }
            listener.onFail();
        }

        return null;
    }

    private List<RenderRequest> buildRenderRequests(FrameData result, Map<String, RGridResource> resourceMapping) {
        RGridDom dom = new RGridDom(result.getCdt(), resourceMapping, result.getUrl(), logger, "buildRenderRequests");

        //Create RG requests
        List<RenderRequest> allRequestsForRG = new ArrayList<>();
        ICheckSettingsInternal checkSettingsInternal = (ICheckSettingsInternal) this.checkSettings;


        List<VisualGridSelector> regionSelectorsList = new ArrayList<>();

        for (VisualGridSelector[] regionSelector : this.regionSelectors) {
            regionSelectorsList.addAll(Arrays.asList(regionSelector));
        }

        logger.verbose("region selectors count: " + regionSelectorsList.size());
        logger.verbose("check visual grid tasks count: " + this.checkTasks.size());

        for (VisualGridTask visualGridTask : this.checkTasks) {

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

    /**
     * Checks with the server what resources are missing.
     * @return All the missing resources to upload.
     */
    List<RGridResource> checkResourcesStatus(RGridDom dom, Map<String, RGridResource> resourceMap) throws JsonProcessingException {
        List<HashObject> hashesToCheck = new ArrayList<>();
        Map<String, String> hashToResourceUrl = new HashMap<>();
        for (RGridResource  resource : resourceMap.values()) {
            String url = resource.getUrl();
            String hash = resource.getSha256();
            String hashFormat = resource.getHashFormat();
            synchronized (uploadedResourcesCache) {
                if (!uploadedResourcesCache.containsKey(hash)) {
                    hashesToCheck.add(new HashObject(hashFormat, hash));
                    hashToResourceUrl.put(hash, url);
                }
            }
        }

        RGridResource domResource = dom.asResource();
        synchronized (uploadedResourcesCache) {
            if (!uploadedResourcesCache.containsKey(domResource.getSha256())) {
                hashesToCheck.add(new HashObject(domResource.getHashFormat(), domResource.getSha256()));
                hashToResourceUrl.put(domResource.getSha256(), domResource.getUrl());
            }
        }

        if (hashesToCheck.isEmpty()) {
            return new ArrayList<>();
        }

        SyncTaskListener<Boolean[]> listener = new SyncTaskListener<>(logger, "checkResourceStatus");
        HashObject[] hashesArray = hashesToCheck.toArray(new HashObject[0]);
        eyesConnector.checkResourceStatus(listener, null, hashesArray);
        Boolean[] result = listener.get();
        if (result == null) {
            throw new EyesException("Failed checking resources with the server");
        }

        // Analyzing the server response and find the missing resources
        List<RGridResource> missingResources = new ArrayList<>();
        for (int i = 0; i < result.length; i++) {
            String hash = hashesArray[i].getHash();
            String resourceUrl = hashToResourceUrl.get(hash);
            if (result[i] != null && result[i]) {
                synchronized (uploadedResourcesCache) {
                    RGridResource resource;
                    if (resourceUrl.equals(domResource.getUrl()) && hash.equals(domResource.getSha256())) {
                        resource = domResource;
                    } else {
                        resource = resourceMap.get(resourceUrl);
                    }

                    resource.resetContent();
                    uploadedResourcesCache.put(resource.getSha256(), null);
                }
                continue;
            }

            if (resourceUrl.equals(domResource.getUrl()) && hash.equals(domResource.getSha256())) {
                missingResources.add(domResource);
                continue;
            }

            missingResources.add(resourceMap.get(resourceUrl));
        }

        return missingResources;
    }

    void uploadResources(List<RGridResource> resources) {
        for (RGridResource resource : resources) {
            synchronized (uploadedResourcesCache) {
                if (uploadedResourcesCache.containsKey(resource.getSha256())) {
                    continue;
                }
                logger.verbose("resource(" + resource.getUrl() + ") hash : " + resource.getSha256());
                SyncTaskListener<Void> listener = new SyncTaskListener<>(logger, String.format("uploadResource %s %s", resource.getSha256(), resource.getUrl()));
                this.eyesConnector.renderPutResource("NONE", resource, listener);
                uploadedResourcesCache.put(resource.getSha256(), listener);
            }
        }

        // Wait for all resources to be uploaded
        for (RGridResource resource : resources) {
            // A blocking call
            SyncTaskListener<Void> listener = uploadedResourcesCache.get(resource.getSha256());
            if (listener != null) {
                listener.get();
            }
        }
    }
}
