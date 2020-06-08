package com.applitools.eyes.visualgrid.model;

import com.applitools.ICheckSettings;
import com.applitools.ICheckSettingsInternal;
import com.applitools.eyes.Logger;
import com.applitools.eyes.TaskListener;
import com.applitools.eyes.UserAgent;
import com.applitools.eyes.visualgrid.services.IEyesConnector;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.applitools.eyes.visualgrid.services.VisualGridTask;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.css.ECSSVersion;
import com.helger.css.decl.*;
import com.helger.css.reader.CSSReader;
import org.apache.commons.codec.binary.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("WeakerAccess")
public class RenderingTask implements Callable<RenderStatusResults>, CompletableTask {

    private static final int FETCH_TIMEOUT_SECONDS = 60;
    public static final String FULLPAGE = "full-page";
    public static final String VIEWPORT = "viewport";
    public static final int HOUR = 60 * 60 * 1000;
    public static final String TEXT_CSS = "text/css";
    public static final String IMAGE_SVG_XML = "image/svg+xml";

    private final List<RenderTaskListener> listeners = new ArrayList<>();
    private IEyesConnector eyesConnector;
    private ICheckSettings checkSettings;
    private List<VisualGridTask> visualGridTaskList;
    private List<VisualGridTask> openVisualGridTaskList;
    private RenderingInfo renderingInfo;
    private UserAgent userAgent;
    private final Map<String, RGridResource> fetchedCacheMap;
    private final Map<String, RGridResource> putResourceCache;
    private Logger logger;
    private AtomicBoolean isTaskComplete = new AtomicBoolean(false);
    private AtomicBoolean isForcePutNeeded;
    private final List<VisualGridSelector[]> regionSelectors;
    private IDebugResourceWriter debugResourceWriter;
    private FrameData domData;
    private AtomicInteger framesLevel = new AtomicInteger();
    private RGridDom dom = null;
    private Timer timer = new Timer("VG_StopWatch", true);
    private AtomicBoolean isTimeElapsed = new AtomicBoolean(false);

    // Phaser for syncing all futures downloading resources
    Phaser resourcesPhaser = new Phaser();

    // Listener for putResource tasks
    private TaskListener<Boolean> putListener = new TaskListener<Boolean>() {
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

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private boolean isTaskStarted = false;

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private boolean isTaskCompleted = false;

    @SuppressWarnings("unused")
    private boolean isTaskInException = false;

    public interface RenderTaskListener {
        void onRenderSuccess();

        void onRenderFailed(Exception e);
    }

    RenderingTask(IEyesConnector eyesConnector, List<VisualGridTask> visualGridTaskList, UserAgent userAgent) {
        this.eyesConnector = eyesConnector;
        this.visualGridTaskList = visualGridTaskList;
        this.userAgent = userAgent;
        fetchedCacheMap = new HashMap<>();
        logger = new Logger();
        regionSelectors = new ArrayList<>();
        putResourceCache = new HashMap<>();
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
            this.isTaskStarted = true;

            addRenderingTaskToOpenTasks();

            logger.verbose("enter");

            boolean isSecondRequestAlreadyHappened = false;

            logger.verbose("step 1");

            //Build RenderRequests
            RenderRequest[] requests = prepareDataForRG(domData);

            logger.verbose("step 2");
            boolean stillRunning;
            long elapsedTimeStart = System.currentTimeMillis();
            boolean isForcePutAlreadyDone = false;
            List<RunningRender> runningRenders = null;
            do {
                try {
                    runningRenders = this.eyesConnector.render(requests);
                } catch (Exception e) {
                    Thread.sleep(1500);
                    logger.verbose("/render throws exception... sleeping for 1.5s");
                    if (isSecondRequestAlreadyHappened) {
                        logger.verbose("Second request already happened");
                        throw e;
                    }
                    isSecondRequestAlreadyHappened = true;;
                    GeneralUtils.logExceptionStackTrace(logger, e);
                    logger.verbose("ERROR " + e.getMessage());
                }
                logger.verbose("step 3.1");
                if (runningRenders == null || runningRenders.size() == 0) {
                    logger.verbose("ERROR - runningRenders is null or empty.");
                    break;
                }

                for (int i = 0; i < requests.length; i++) {
                    RenderRequest request = requests[i];
                    request.setRenderId(runningRenders.get(i).getRenderId());
                }
                logger.verbose("step 3.2");

                RunningRender runningRender = runningRenders.get(0);
                RenderStatus worstStatus = runningRender.getRenderStatus();

                worstStatus = calcWorstStatus(runningRenders, worstStatus);

                boolean isNeedMoreDom = runningRender.isNeedMoreDom();

                if (isForcePutNeeded.get() && !isForcePutAlreadyDone) {
                    forcePutAllResources(requests[0].getResources(), requests[0].getDom(), runningRender);
                    isForcePutAlreadyDone = true;
                    try {
                        if (resourcesPhaser.getRegisteredParties() > 0) {
                            resourcesPhaser.awaitAdvanceInterruptibly(0, 30, TimeUnit.SECONDS);
                        }
                    } catch (InterruptedException | TimeoutException e) {
                        GeneralUtils.logExceptionStackTrace(logger, e);
                        resourcesPhaser.forceTermination();
                    }
                }

                logger.verbose("step 3.3");
                double elapsedTime = (System.currentTimeMillis() - elapsedTimeStart) / 1000;
                stillRunning = (worstStatus == RenderStatus.NEED_MORE_RESOURCE || isNeedMoreDom) && elapsedTime < FETCH_TIMEOUT_SECONDS;
                if (stillRunning) {
                    sendMissingResources(runningRenders, requests[0].getDom(), requests[0].getResources(), isNeedMoreDom);
                    try {
                        if (resourcesPhaser.getRegisteredParties() > 0) {
                            resourcesPhaser.awaitAdvanceInterruptibly(0, 30, TimeUnit.SECONDS);
                        }
                    } catch (InterruptedException | TimeoutException e) {
                        GeneralUtils.logExceptionStackTrace(logger, e);
                        resourcesPhaser.forceTermination();
                    }
                }

                logger.verbose("step 3.4");

            } while (stillRunning);

            Map<RunningRender, RenderRequest> mapping = mapRequestToRunningRender(runningRenders, requests);

            logger.verbose("step 4");
            pollRenderingStatus(mapping);

            isTaskCompleted = true;
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

    private void createPutFutures(RunningRender runningRender, Map<String, RGridResource> resources) {
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

    private RenderRequest[] prepareDataForRG(FrameData domData) {
        final Map<String, RGridResource> allBlobs = Collections.synchronizedMap(new HashMap<String, RGridResource>());
        Set<URI> resourceUrls = new HashSet<>();

        writeFrameDataAsResource(domData);

        parseScriptResult(domData, allBlobs, resourceUrls);

        logger.verbose("fetching " + resourceUrls.size() + " resources...");

        //Fetch all resources
        resourcesPhaser = new Phaser();
        fetchAllResources(allBlobs, resourceUrls, domData);
        try {
            if (resourcesPhaser.getRegisteredParties() > 0) {
                resourcesPhaser.awaitAdvanceInterruptibly(0, 30, TimeUnit.SECONDS);
            }
        } catch (InterruptedException | TimeoutException e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
            resourcesPhaser.forceTermination();
        }

        logger.verbose("done fetching resources.");

        List<RGridResource> unparsedResources = addBlobsToCache(allBlobs);

        resourceUrls = new HashSet<>();
        parseAndCollectExternalResources(unparsedResources, domData.getUrl(), resourceUrls);
        //Parse allBlobs to mapping
        Map<String, RGridResource> resourceMapping = new HashMap<>();
        for (String url : allBlobs.keySet()) {
            try {
                logger.verbose("trying to fetch - " + url);
                RGridResource resource = this.fetchedCacheMap.get(url);
                if (resource == null) {
                    logger.log(String.format("Illegal state: resource is null for url %s", url));
                    continue;
                }
                if (resource.getContent() != null) {
                    logger.verbose("adding url to map: " + url);
                    resourceMapping.put(url, resource);
                }
            } catch (Exception e) {
                logger.verbose("Couldn't download url = " + url);
            }
        }

        buildAllRGDoms(resourceMapping, domData);

        List<RenderRequest> allRequestsForRG = buildRenderRequests(domData, resourceMapping);

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

    private void buildAllRGDoms(Map<String, RGridResource> resourceMapping, FrameData domData) {
        URL baseUrl = null;
        String domDataUrl = domData.getUrl();
        logger.verbose("url in DOM: " + domDataUrl);
        try {
            baseUrl = new URL(domDataUrl);
        } catch (MalformedURLException e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
        logger.verbose("baseUrl: " + baseUrl);
        List<FrameData> allFrame = domData.getFrames();
        logger.verbose("FrameData count: " + allFrame.size());
        Map<String, RGridResource> mapping = new HashMap<>();
        for (FrameData frameObj : allFrame) {
            List<BlobData> allFramesBlobs = frameObj.getBlobs();
            @SuppressWarnings("unchecked")
            List<String> allResourceUrls = frameObj.getResourceUrls();
            URL frameUrl;
            try {
                frameUrl = new URL(baseUrl, frameObj.getUrl());
            } catch (MalformedURLException e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
                continue;
            }
            for (BlobData blob : allFramesBlobs) {
                String blobUrl = blob.getUrl();
                RGridResource rGridResource = resourceMapping.get(blobUrl);
                mapping.put(blobUrl, rGridResource);

            }
            for (String resourceUrl : allResourceUrls) {
                RGridResource rGridResource = resourceMapping.get(resourceUrl);
                mapping.put(resourceUrl, rGridResource);
            }
            List<CdtData> cdt = frameObj.getCdt();
            RGridDom rGridDom = new RGridDom(cdt, mapping, frameUrl.toString(), logger, "buildAllRGDoms");
            try {
                resourceMapping.put(frameUrl.toString(), rGridDom.asResource());
                buildAllRGDoms(resourceMapping, frameObj);
            } catch (JsonProcessingException e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
            }
        }
    }

    private void writeFrameDataAsResource(FrameData domData) {
        if (debugResourceWriter == null || (debugResourceWriter instanceof NullDebugResourceWriter)) return;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            byte[] content = objectMapper.writeValueAsBytes(domData);
            RGridResource resource = new RGridResource(domData.getUrl(), RGridDom.CONTENT_TYPE, content);
            debugResourceWriter.write(resource);
        } catch (JsonProcessingException e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
    }

    private void parseScriptResult(FrameData domData, Map<String, RGridResource> allBlobs, Set<URI> resourceUrls) {
        Base64 codec = new Base64();

        String baseUrlStr = domData.getUrl();

        logger.verbose("baseUrl: " + baseUrlStr);

        URI baseUrl = null;
        try {
            baseUrl = new URI(baseUrlStr);
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }

        parseBlobs(allBlobs, codec, baseUrl, domData.getBlobs());

        parseResourceUrls(domData, resourceUrls, baseUrl);

        parseFrames(domData, allBlobs, resourceUrls);

        List<RGridResource> unparsedResources = addBlobsToCache(allBlobs);

        String baseUrlAsString = null;
        if (baseUrl != null) {
            baseUrlAsString = baseUrl.toString();
        }
        parseAndCollectExternalResources(unparsedResources, baseUrlAsString, resourceUrls);
    }


    private void parseFrames(FrameData frameData, Map<String, RGridResource> allBlobs, Set<URI> resourceUrls) {
        logger.verbose("handling 'frames' key (level: " + framesLevel.incrementAndGet() + ")");
        for (FrameData frameObj : frameData.getFrames()) {
            parseScriptResult(frameObj, allBlobs, resourceUrls);
        }
        logger.verbose("done handling 'frames' key (level: " + framesLevel.getAndDecrement() + ")");
    }

    private void parseResourceUrls(FrameData result, Set<URI> resourceUrls, URI baseUrl) {
        List<String> list = result.getResourceUrls();
        for (String url : list) {
            try {
                resourceUrls.add(baseUrl.resolve(url));
            } catch (Exception e) {
                logger.log("Error resolving url:" + url);
                GeneralUtils.logExceptionStackTrace(logger, e);
            }
        }

        logger.verbose("exit");
    }

    private void parseBlobs(Map<String, RGridResource> allBlobs, Base64 codec, URI baseUrl, List<BlobData> value) {
        //TODO check if empty
        for (BlobData blob : value) {
            RGridResource resource = parseBlobToGridResource(codec, baseUrl, blob);
            if (!allBlobs.containsKey(resource.getUrl())) {
                allBlobs.put(resource.getUrl(), resource);
            }
        }
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

            DesktopBrowserInfo browserInfo = visualGridTask.getBrowserInfo();

            String sizeMode = checkSettingsInternal.getSizeMode();

            if (sizeMode.equalsIgnoreCase(VIEWPORT) && checkSettingsInternal.isStitchContent()) {
                sizeMode = FULLPAGE;
            }

            RenderInfo renderInfo = new RenderInfo(browserInfo.getWidth(), browserInfo.getHeight(),
                    sizeMode, checkSettingsInternal.getTargetRegion(), checkSettingsInternal.getVGTargetSelector(),
                    browserInfo.getEmulationInfo(), browserInfo.getIosDeviceInfo());

            RenderRequest request = new RenderRequest(this.renderingInfo.getResultsUrl(), result.getUrl(), dom,
                    resourceMapping, renderInfo, browserInfo.getPlatform(), browserInfo.getBrowserType(),
                    checkSettingsInternal.getScriptHooks(), regionSelectorsList, checkSettingsInternal.isSendDom(), visualGridTask, this.renderingInfo.getStitchingServiceUrl());

            allRequestsForRG.add(request);
        }

        logger.verbose("count of all requests for RG: " + allRequestsForRG.size());
        return allRequestsForRG;
    }

    private RGridResource parseBlobToGridResource(Base64 codec, URI baseUrl, BlobData blobAsMap) {
        // TODO - handle non-String values (probably empty json objects)
        String contentAsString = blobAsMap.getValue();
        byte[] content = codec.decode(contentAsString);
        String urlAsString = blobAsMap.getUrl();
        try {
            URI url = baseUrl.resolve(urlAsString);
            urlAsString = url.toString();
        } catch (Exception e) {
            logger.log("Error resolving uri:" + urlAsString);
            GeneralUtils.logExceptionStackTrace(logger, e);
        }

        @SuppressWarnings("UnnecessaryLocalVariable")
        RGridResource resource = new RGridResource(urlAsString, blobAsMap.getType(), content);
        return resource;
    }

    private void parseAndCollectExternalResources(List<RGridResource> allBlobs, String baseUrl, Set<URI> resourceUrls) {
        for (RGridResource blob : allBlobs) {
            getAndParseResource(blob, baseUrl, resourceUrls);
        }
    }

    private void getAndParseResource(RGridResource blob, String baseUrl, Set<URI> resourceUrls) {
        URI baseUri = URI.create(baseUrl);
        TextualDataResource tdr = tryGetTextualData(blob, baseUri);
        if (tdr == null) {
            return;
        }
        switch (tdr.mimeType) {
            case TEXT_CSS:
                parseCSS(tdr, resourceUrls);
                break;
            case IMAGE_SVG_XML:
                parseSVG(tdr, resourceUrls);
                break;
        }
    }

    private void parseSVG(TextualDataResource tdr, Set<URI> allResourceUris) {

        try {
            Document doc = Jsoup.parse(new String(tdr.originalData), tdr.uri.toString(), Parser.xmlParser());
            Elements links = doc.select("[href]");
            links.addAll(doc.select("[xlink:href]"));

            for (Element element : links) {
                String href = element.attr("href");
                if (href.isEmpty()) {
                    href = element.attr("xlink:href");
                    if (href.startsWith("#")) {
                        continue;
                    }
                }
                createUriAndAddToList(allResourceUris, tdr.uri, href);
            }
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
    }


    static class TextualDataResource {
        String mimeType;
        URI uri;
        String data;
        byte[] originalData;
    }

    private TextualDataResource tryGetTextualData(RGridResource blob, URI baseUrl) {
        byte[] contentBytes = blob.getContent();
        String contentTypeStr = blob.getContentType();
        if (contentTypeStr == null) return null;
        if (contentBytes.length == 0) return null;
        String[] parts = contentTypeStr.split(";");

        TextualDataResource tdr = new TextualDataResource();
        if (parts.length > 0) {
            tdr.mimeType = parts[0].toLowerCase();
        }

        String charset = "UTF-8";
        if (parts.length > 1) {
            String[] keyVal = parts[1].split("=");
            String key = keyVal[0].trim();
            String val = keyVal[1].trim();
            if (key.equalsIgnoreCase("charset")) {
                charset = val.toUpperCase();
            }
        }

        //remove double quotes if surrounded
        charset = charset.replaceAll("\"", "");

        try {
            tdr.data = new String(contentBytes, charset);
        } catch (UnsupportedEncodingException e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }

        URI uri = null;
        try {
            uri = new URI(GeneralUtils.sanitizeURL(blob.getUrl(), logger));
            tdr.uri = baseUrl.resolve(uri);
        } catch (Exception e) {
            logger.log("Error resolving uri:" + uri);
            GeneralUtils.logExceptionStackTrace(logger, e);
        }

        tdr.originalData = blob.getContent();
        logger.verbose("exit");
        return tdr;
    }


    private void parseCSS(TextualDataResource css, Set<URI> resourceUrls) {
        try {
            String data = css.data;
            if (data == null) {
                return;
            }
            CascadingStyleSheet cascadingStyleSheet = CSSReader.readFromString(data, ECSSVersion.CSS30);
            if (cascadingStyleSheet == null) {
                logger.verbose("exit - failed to read CSS String");
                return;
            }
            collectAllImportUris(cascadingStyleSheet, resourceUrls, css.uri);
            collectAllFontFaceUris(cascadingStyleSheet, resourceUrls, css.uri);
            collectAllBackgroundImageUris(cascadingStyleSheet, resourceUrls, css.uri);
        } catch (Throwable e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
    }

    private void collectAllFontFaceUris(CascadingStyleSheet cascadingStyleSheet, Set<URI> allResourceUris, URI baseUrl) {
        logger.verbose("enter");
        ICommonsList<CSSFontFaceRule> allFontFaceRules = cascadingStyleSheet.getAllFontFaceRules();
        for (CSSFontFaceRule fontFaceRule : allFontFaceRules) {
            getAllResourcesUrisFromDeclarations(allResourceUris, fontFaceRule, "src", baseUrl);
        }
        logger.verbose("exit");
    }

    private void collectAllBackgroundImageUris(CascadingStyleSheet cascadingStyleSheet, Set<URI> allResourceUris, URI baseUrl) {
        logger.verbose("enter");
        ICommonsList<CSSStyleRule> allStyleRules = cascadingStyleSheet.getAllStyleRules();
        for (CSSStyleRule styleRule : allStyleRules) {
            getAllResourcesUrisFromDeclarations(allResourceUris, styleRule, "background", baseUrl);
            getAllResourcesUrisFromDeclarations(allResourceUris, styleRule, "background-image", baseUrl);
        }
        logger.verbose("exit");
    }

    private void collectAllImportUris(CascadingStyleSheet cascadingStyleSheet, Set<URI> allResourceUris, URI baseUrl) {
        logger.verbose("enter");
        ICommonsList<CSSImportRule> allImportRules = cascadingStyleSheet.getAllImportRules();
        for (CSSImportRule importRule : allImportRules) {
            String uri = importRule.getLocation().getURI();
            createUriAndAddToList(allResourceUris, baseUrl, uri);
        }
        logger.verbose("exit");
    }

    private void createUriAndAddToList(Set<URI> allResourceUris, URI baseUrl, String uri) {
        if (uri.toLowerCase().startsWith("data:") || uri.toLowerCase().startsWith("javascript:")) return;
        try {
            URI url = baseUrl.resolve(uri);
            allResourceUris.add(url);
        } catch (Exception e) {
            logger.log("Error resolving uri:" + uri);
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
    }

    private <T extends IHasCSSDeclarations<T>> void getAllResourcesUrisFromDeclarations(Set<URI> allResourceUris, IHasCSSDeclarations<T> rule, String propertyName, URI baseUrl) {
        ICommonsList<CSSDeclaration> sourcesList = rule.getAllDeclarationsOfPropertyName(propertyName);
        for (CSSDeclaration cssDeclaration : sourcesList) {
            CSSExpression cssDeclarationExpression = cssDeclaration.getExpression();
            ICommonsList<ICSSExpressionMember> allExpressionMembers = cssDeclarationExpression.getAllMembers();
            ICommonsList<CSSExpressionMemberTermURI> allUriExpressions = allExpressionMembers.getAllInstanceOf(CSSExpressionMemberTermURI.class);
            for (CSSExpressionMemberTermURI uriExpression : allUriExpressions) {
                String uri = uriExpression.getURIString();
                createUriAndAddToList(allResourceUris, baseUrl, uri);
            }
        }
    }

    private List<RGridResource> addBlobsToCache(Map<String, RGridResource> allBlobs) {
        logger.verbose(String.format("trying to add %d blobs to cache", allBlobs.size()));
        logger.verbose(String.format("current fetchedCacheMap size: %d", fetchedCacheMap.size()));
        List<RGridResource> unparsedResources = new ArrayList<>();
        String url;
        for (RGridResource blob : allBlobs.values()) {
            url = blob.getUrl();
            if (fetchedCacheMap.containsKey(url)) {
                allBlobs.put(url, fetchedCacheMap.get(url));
                continue;
            }
            String contentType = blob.getContentType();
            try {
                if (contentType == null || !contentType.equalsIgnoreCase(RGridDom.CONTENT_TYPE)) {
                    fetchedCacheMap.put(url, blob);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            unparsedResources.add(blob);
        }
        return unparsedResources;
    }

    void fetchAllResources(final Map<String, RGridResource> allBlobs, final Set<URI> resourceUrls, final FrameData result) {
        logger.verbose("enter");
        if (resourceUrls.isEmpty()) {
            return;
        }

        for (final URI uri : resourceUrls) {
            final String uriStr = GeneralUtils.sanitizeURL(uri.toString(), logger);

            synchronized (this.fetchedCacheMap) {
                // If resource is already being fetched, remove it from the list, and use the future.
                if (fetchedCacheMap.containsKey(uriStr)) {
                    logger.verbose("this.fetchedCacheMap.containsKey(" + uriStr + ")");
                    continue;
                }

                // If resource is not being fetched yet (limited guarantee)
                IEyesConnector eyesConnector = this.visualGridTaskList.get(0).getEyesConnector();
                try {
                    resourcesPhaser.register();
                    eyesConnector.getResource(uri, userAgent.getOriginalUserAgentString(), result.getUrl(),
                            new TaskListener<RGridResource>() {
                        @Override
                        public void onComplete(RGridResource taskResponse) {
                            try {
                                if (taskResponse == null) {
                                    logger.log(String.format("Resource is null for url %s", uriStr));
                                    return;
                                }

                                Set<URI> newResourceUrls = handleCollectedResource(uri, taskResponse, allBlobs, result);
                                if (newResourceUrls.isEmpty()) {
                                    return;
                                }

                                fetchAllResources(allBlobs, newResourceUrls, result);
                            } finally {
                                resourcesPhaser.arriveAndDeregister();
                            }
                        }

                        @Override
                        public void onFail() {
                            resourcesPhaser.arriveAndDeregister();
                            logger.log(String.format("Failed downloading from uri %s", uriStr));
                        }
                    });
                } catch (Exception e) {
                    logger.log("error converting " + uri + " to url");
                    GeneralUtils.logExceptionStackTrace(logger, e);
                }
            }
        }
        logger.verbose("exit");
    }

    /**
     * Handles collected resources
     * @return A set of new resources to keep collecting recursively
     */
    private Set<URI> handleCollectedResource(URI url, RGridResource resource, Map<String, RGridResource> allBlobs, FrameData result) {
        Set<URI> newResourceUrls = new HashSet<>();
        try {
            synchronized (fetchedCacheMap) {
                fetchedCacheMap.put(url.toString(), resource);
            }
            this.debugResourceWriter.write(resource);
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
        logger.verbose("done writing to debugWriter");

        allBlobs.put(resource.getUrl(), resource);
        String contentType = resource.getContentType();
        logger.verbose("handling " + contentType + " resource from URL: " + url);
        getAndParseResource(resource, result.getUrl(), newResourceUrls);
        resource.setIsResourceParsed(true);
        return newResourceUrls;
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
            if (renderStatusResultsList == null || renderStatusResultsList.isEmpty() || renderStatusResultsList.get(0) == null) {
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
            for (RunningRender renderedRender : runningRenders.keySet()) {
                String renderId = renderedRender.getRenderId();
                if (renderId.equalsIgnoreCase(id)) {
                    logger.verbose("removing failed render id: " + id);
                    VisualGridTask visualGridTask = runningRenders.get(renderedRender).getVisualGridTask();
                    visualGridTask.setRenderError(id, "too long rendering(rendering exceeded 150 sec)");
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
                continue;
            }

            RenderStatus renderStatus = renderStatusResults.getStatus();
            boolean isRenderedStatus = renderStatus == RenderStatus.RENDERED;
            boolean isErrorStatus = renderStatus == RenderStatus.ERROR;
            logger.verbose("renderStatusResults - " + renderStatusResults);
            if (isRenderedStatus || isErrorStatus) {

                String removedId = ids.remove(j);

                for (RunningRender renderedRender : runningRenders.keySet()) {
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
                                    openVisualGridTask.setRenderError(removedId, renderStatusResults.getError());
                                }
                                iterator.remove();
                            }
                        }
                        logger.verbose("setting visualGridTask " + visualGridTask + " render result: " + renderStatusResults + " to url " + this.domData.getUrl());
                        String error = renderStatusResults.getError();
                        if (error != null) {
                            GeneralUtils.logExceptionStackTrace(logger, new Exception(error));
                            visualGridTask.setRenderError(renderId, error);
                        }
                        visualGridTask.setRenderResult(renderStatusResults);
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

