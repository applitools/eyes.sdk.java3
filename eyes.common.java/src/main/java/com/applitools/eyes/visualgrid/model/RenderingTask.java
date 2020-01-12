package com.applitools.eyes.visualgrid.model;

import com.applitools.ICheckSettings;
import com.applitools.ICheckSettingsInternal;
import com.applitools.eyes.IPutFuture;
import com.applitools.eyes.Logger;
import com.applitools.eyes.UserAgent;
import com.applitools.eyes.visualgrid.services.IEyesConnector;
import com.applitools.eyes.visualgrid.services.IResourceFuture;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.applitools.eyes.visualgrid.services.VisualGridTask;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.net.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("WeakerAccess")
public class RenderingTask implements Callable<RenderStatusResults>, CompletableTask {

    private static final int MAX_FETCH_FAILS = 62;
    public static final String CDT = "x-applitools-html/cdt";
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
    private final Map<String, IResourceFuture> fetchedCacheMap;
    private final Map<String, IPutFuture> putResourceCache;
    private Logger logger;
    private AtomicBoolean isTaskComplete = new AtomicBoolean(false);
    private AtomicBoolean isForcePutNeeded;
    private final List<VisualGridSelector[]> regionSelectors;
    private IDebugResourceWriter debugResourceWriter;
    private FrameData result;
    private AtomicInteger framesLevel = new AtomicInteger();
    private RGridDom dom = null;
    private Timer timer = new Timer("VG_StopWatch", true);
    private AtomicBoolean isTimeElapsed = new AtomicBoolean(false);

    private boolean isTaskStarted = false;
    private boolean isTaskCompleted = false;
    private boolean isTaskInException = false;

    public interface RenderTaskListener {
        void onRenderSuccess();

        void onRenderFailed(Exception e);
    }

    public RenderingTask(IEyesConnector eyesConnector, FrameData scriptResult, ICheckSettings checkSettings,
                         List<VisualGridTask> visualGridTaskList, List<VisualGridTask> openVisualGridTasks, VisualGridRunner renderingGridManager,
                         IDebugResourceWriter debugResourceWriter, RenderTaskListener listener, UserAgent userAgent, List<VisualGridSelector[]> regionSelectors) {

        this.eyesConnector = eyesConnector;
        this.result = scriptResult;
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
            RenderRequest[] requests = prepareDataForRG(result);

            logger.verbose("step 2");
            boolean stillRunning = true;
            int fetchFails = 0;
            boolean isForcePutAlreadyDone = false;
            List<RunningRender> runningRenders = null;
            do {
                try {

                    runningRenders = this.eyesConnector.render(requests);

                } catch (Exception e) {

                    Thread.sleep(1500);
                    logger.verbose("/render throws exception... sleeping for 1.5s");
                    GeneralUtils.logExceptionStackTrace(logger, e);
                    if (e.getMessage().contains("Second request, yet still some resources were not PUT in renderId")) {
                        if (isSecondRequestAlreadyHappened) {
                            logger.verbose("Second request already happened");
                        }
                        isSecondRequestAlreadyHappened = true;
//                        this.isForcePutNeeded.set(true);
                    }
                    logger.verbose("ERROR " + e.getMessage());
                    fetchFails++;
                }
                logger.verbose("step 3.1");
                if (runningRenders == null) {
                    logger.verbose("ERROR - runningRenders is null.");
                    continue;
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
                }

                logger.verbose("step 3.3");
                stillRunning = worstStatus == RenderStatus.NEED_MORE_RESOURCE || isNeedMoreDom || fetchFails > MAX_FETCH_FAILS;
                if (stillRunning) {
                    sendMissingResources(runningRenders, requests[0].getDom(), requests[0].getResources(), isNeedMoreDom);
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
        RGridResource resource;
        List<IPutFuture> allPuts = new ArrayList<>();
        Set<String> strings = resources.keySet();
        try {
            allPuts.add(this.eyesConnector.renderPutResource(runningRender, dom.asResource(), userAgent.getOriginalUserAgentString()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        for (String url : strings) {
            try {
                logger.verbose("trying to get url from map - " + url);
                IResourceFuture resourceFuture = fetchedCacheMap.get(url);
                if (resourceFuture == null) {
                    logger.verbose("fetchedCacheMap.get(url) == null trying dom");
                    if (url.equals(this.dom.getUrl())) {
                        resource = this.dom.asResource();
                    } else {
                        logger.verbose("Resource not found Exiting...");
                        return;
                    }
                } else {
                    resource = resourceFuture.get();
                    IPutFuture future = this.eyesConnector.renderPutResource(runningRender, resource, userAgent.getOriginalUserAgentString());
                    logger.verbose("locking putResourceCache");
                    synchronized (putResourceCache) {
                        String contentType = resource.getContentType();
                        if (contentType != null && !contentType.equalsIgnoreCase(CDT)) {
                            putResourceCache.put(this.dom.getUrl(), future);
                        }
                        allPuts.add(future);
                    }
                }
            } catch (Exception e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
            }
        }
        for (IPutFuture put : allPuts) {
            try {
                put.get();
            } catch (InterruptedException | ExecutionException e) {
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
        List<IPutFuture> allPuts = new ArrayList<>();
        if (isNeedMoreDom) {
            RunningRender runningRender = runningRenders.get(0);
            IPutFuture future = null;
            try {
                future = this.eyesConnector.renderPutResource(runningRender, dom.asResource(), userAgent.getOriginalUserAgentString());
            } catch (Throwable e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
            }
            logger.verbose("locking putResourceCache");
            allPuts.add(future);
            logger.verbose("releasing putResourceCache");
        }

        logger.verbose("creating PutFutures for " + runningRenders.size() + " runningRenders");

        for (RunningRender runningRender : runningRenders) {
            createPutFutures(allPuts, runningRender, resources);
        }

        logger.verbose("calling future.get on " + allPuts.size() + " PutFutures");
        for (IPutFuture future : allPuts) {
            logger.verbose("calling future.get on " + future.toString());
            try {
                future.get(2, TimeUnit.MINUTES);
            } catch (Exception e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
            }
        }
        logger.verbose("exit");
    }

    private void createPutFutures(List<IPutFuture> allPuts, RunningRender runningRender, Map<String, RGridResource> resources) {
        List<String> needMoreResources = runningRender.getNeedMoreResources();
        RGridResource resource;
        for (String url : needMoreResources) {
            if (putResourceCache.containsKey(url)) {
                IPutFuture putFuture = putResourceCache.get(url);
                if (!allPuts.contains(putFuture)) {
                    allPuts.add(putFuture);
                }
                continue;
            }

            //                    logger.verbose("trying to get url from map - " + url);
            IResourceFuture resourceFuture = fetchedCacheMap.get(url);
            if (resourceFuture == null) {
                logger.verbose("fetchedCacheMap.get(url) == null - " + url);
                logger.verbose("Resource put requested but never downloaded(maybe a Frame)");
                resource = resources.get(url);
            } else {
                try {
                    resource = resourceFuture.get();
                } catch (InterruptedException | ExecutionException e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                    continue;
                }
//
            }
            logger.verbose("resource(" + resource.getUrl() + ") hash : " + resource.getSha256());
            IPutFuture future = this.eyesConnector.renderPutResource(runningRender, resource, userAgent.getOriginalUserAgentString());
            String contentType = resource.getContentType();
            if (!putResourceCache.containsKey(url) && (contentType != null && !contentType.equalsIgnoreCase(CDT))) {
                synchronized (putResourceCache) {
                    putResourceCache.put(url, future);
                }
            }
            allPuts.add(future);

        }

    }

    private RenderRequest[] prepareDataForRG(FrameData result) {

        final Map<String, RGridResource> allBlobs = Collections.synchronizedMap(new HashMap<String, RGridResource>());
        Set<URL> resourceUrls = new HashSet<>();

        parseScriptResult(result, allBlobs, resourceUrls);

        logger.verbose("fetching " + resourceUrls.size() + " resources...");

        //Fetch all resources
        fetchAllResources(allBlobs, resourceUrls, result);
        if (!resourceUrls.isEmpty()) {
            logger.verbose("ERROR resourceUrl is not empty!!!!!***************************");
        }

        logger.verbose("done fetching resources.");

        List<RGridResource> unparsedResources = addBlobsToCache(allBlobs);

        parseAndCollectExternalResources(unparsedResources, result.getUrl(), resourceUrls);
        //Parse allBlobs to mapping
        Map<String, RGridResource> resourceMapping = new HashMap<>();
        for (String url : allBlobs.keySet()) {
            try {
                logger.verbose("trying to fetch - " + url);
                IResourceFuture iResourceFuture = this.fetchedCacheMap.get(url);
                if (iResourceFuture != null) {
                    RGridResource value = iResourceFuture.get();
                    if (value.getContent() != null) resourceMapping.put(url, value);
                }
            } catch (Exception e) {
                logger.verbose("Couldn't download url = " + url);
            }
        }

        buildAllRGDoms(resourceMapping, result);

        List<RenderRequest> allRequestsForRG = buildRenderRequests(result, resourceMapping);

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

    private void buildAllRGDoms(Map<String, RGridResource> resourceMapping, FrameData result) {
        URL baseUrl = null;
        try {
            baseUrl = new URL(result.getUrl());
        } catch (MalformedURLException e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
        logger.verbose("baseUrl: " + baseUrl);
        List<FrameData> allFrame = result.getFrames();
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

    @SuppressWarnings("unchecked")
    private void parseScriptResult(FrameData result, Map<String, RGridResource> allBlobs, Set<URL> resourceUrls) {

        Base64 codec = new Base64();

        String baseUrlStr = result.getUrl();

        logger.verbose("baseUrl: " + baseUrlStr);

        URL baseUrl = null;
        try {
            baseUrl = new URL(baseUrlStr);
        } catch (MalformedURLException e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
        parseBlobs(allBlobs, codec, baseUrl, result.getBlobs());

        parseResourceUrls(result, resourceUrls, baseUrl);

        parseFrames(result, allBlobs, resourceUrls);

        List<RGridResource> unparsedResources = addBlobsToCache(allBlobs);

        String baseUrlAsString = null;
        if (baseUrl != null) {
            baseUrlAsString = baseUrl.toString();
        }
        parseAndCollectExternalResources(unparsedResources, baseUrlAsString, resourceUrls);
    }


    private void parseFrames(FrameData result, Map<String, RGridResource> allBlobs, Set<URL> resourceUrls) {
        logger.verbose("handling 'frames' key (level: " + framesLevel.incrementAndGet() + ")");
        for (FrameData frameObj : result.getFrames()) {
            parseScriptResult(frameObj, allBlobs, resourceUrls);
        }
        logger.verbose("done handling 'frames' key (level: " + framesLevel.getAndDecrement() + ")");
    }

    private void parseResourceUrls(FrameData result, Set<URL> resourceUrls, URL baseUrl) {
        List<String> list = result.getResourceUrls();
        for (String url : list) {
            try {
                resourceUrls.add(new URL(baseUrl, url));
            } catch (MalformedURLException e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
            }
        }

        logger.verbose("exit");
    }

    private void parseBlobs(Map<String, RGridResource> allBlobs, Base64 codec, URL baseUrl, List<BlobData> value) {
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

        for (VisualGridTask visualGridTask : this.visualGridTaskList) {

            RenderBrowserInfo browserInfo = visualGridTask.getBrowserInfo();

            String sizeMode = checkSettingsInternal.getSizeMode();

            if (sizeMode.equalsIgnoreCase(VIEWPORT) && checkSettingsInternal.isStitchContent()) {
                sizeMode = FULLPAGE;
            }

            RenderInfo renderInfo = new RenderInfo(browserInfo.getWidth(), browserInfo.getHeight(),
                    sizeMode, checkSettingsInternal.getRegion(), checkSettingsInternal.getVGTargetSelector(), browserInfo.getEmulationInfo());

            RenderRequest request = new RenderRequest(this.renderingInfo.getResultsUrl(), result.getUrl(), dom,
                    resourceMapping, renderInfo, browserInfo.getPlatform(), browserInfo.getBrowserType(),
                    checkSettingsInternal.getScriptHooks(), regionSelectorsList, checkSettingsInternal.isSendDom(), visualGridTask, this.renderingInfo.getStitchingServiceUrl());

            allRequestsForRG.add(request);
        }
        return allRequestsForRG;
    }

    private RGridResource parseBlobToGridResource(Base64 codec, URL baseUrl, BlobData blobAsMap) {
        // TODO - handle non-String values (probably empty json objects)
        String contentAsString = blobAsMap.getValue();
        byte[] content = codec.decode(contentAsString);
        String urlAsString = blobAsMap.getUrl();
        try {

            URL url = new URL(baseUrl, urlAsString);
            urlAsString = url.toString();
        } catch (MalformedURLException e) {
            GeneralUtils.logExceptionStackTrace(logger, e);

        }

        @SuppressWarnings("UnnecessaryLocalVariable")
        RGridResource resource = new RGridResource(urlAsString, blobAsMap.getType(), content, logger, "parseBlobToGridResource");
        return resource;
    }

    private void parseAndCollectExternalResources(List<RGridResource> allBlobs, String baseUrl, Set<URL> resourceUrls) {
        for (RGridResource blob : allBlobs) {
            getAndParseResource(blob, baseUrl, resourceUrls);
        }
    }

    private void getAndParseResource(RGridResource blob, String baseUrl, Set<URL> resourceUrls) {
        TextualDataResource tdr = tryGetTextualData(blob, baseUrl);
        if (tdr == null) return;
        switch (tdr.mimeType) {
            case TEXT_CSS:
                parseCSS(tdr, resourceUrls);
                break;
            case IMAGE_SVG_XML:
                parseSVG(tdr, resourceUrls);
                break;
        }
    }

    private void parseSVG(TextualDataResource tdr, Set<URL> allResourceUris) {

        try {
            Document doc = Jsoup.parse(new String(tdr.originalData), tdr.uri.toString(), Parser.xmlParser());
            Elements links = doc.select("[href]");
            links.addAll(doc.select("[xlink:href]"));

            for (Element element : links) {
                String href = element.attr("href");
                if (href.isEmpty()) {
                    href = element.attr("xlink:href");
                    if (href.startsWith("#")) continue;
                }
                CreateUriAndAddToList(allResourceUris, tdr.uri, href);
            }
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
    }


    class TextualDataResource {
        String mimeType;
        URL uri;
        String data;
        byte[] originalData;
    }

    private TextualDataResource tryGetTextualData(RGridResource blob, String baseUrl) {
        byte[] contentBytes = blob.getContent();
//        logger.verbose(String.format("enter - content length: %d ; content type: %s", contentBytes.length , contentTypeStr));
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

        try {
            URL uri = new URL(GeneralUtils.sanitizeURL(blob.getUrl(), logger));
            tdr.uri = new URL(new URL(baseUrl), uri.toString());
        } catch (MalformedURLException e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }

        tdr.originalData = blob.getContent();
        logger.verbose("exit");
        return tdr;
    }


    private void parseCSS(TextualDataResource css, Set<URL> resourceUrls) {
//        logger.verbose("enter");
        try {
            String data = css.data;
            if (data == null) return;
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
//        logger.verbose("exit");
    }

    private void collectAllFontFaceUris(CascadingStyleSheet cascadingStyleSheet, Set<URL> allResourceUris, URL baseUrl) {
        logger.verbose("enter");
        ICommonsList<CSSFontFaceRule> allFontFaceRules = cascadingStyleSheet.getAllFontFaceRules();
        for (CSSFontFaceRule fontFaceRule : allFontFaceRules) {
            getAllResourcesUrisFromDeclarations(allResourceUris, fontFaceRule, "src", baseUrl);
        }
        logger.verbose("exit");
    }

    private void collectAllBackgroundImageUris(CascadingStyleSheet cascadingStyleSheet, Set<URL> allResourceUris, URL baseUrl) {
        logger.verbose("enter");
        ICommonsList<CSSStyleRule> allStyleRules = cascadingStyleSheet.getAllStyleRules();
        for (CSSStyleRule styleRule : allStyleRules) {
            getAllResourcesUrisFromDeclarations(allResourceUris, styleRule, "background", baseUrl);
            getAllResourcesUrisFromDeclarations(allResourceUris, styleRule, "background-image", baseUrl);
        }
        logger.verbose("exit");
    }

    private void collectAllImportUris(CascadingStyleSheet cascadingStyleSheet, Set<URL> allResourceUris, URL baseUrl) {
        logger.verbose("enter");
        ICommonsList<CSSImportRule> allImportRules = cascadingStyleSheet.getAllImportRules();
        for (CSSImportRule importRule : allImportRules) {
            String uri = importRule.getLocation().getURI();
            CreateUriAndAddToList(allResourceUris, baseUrl, uri);
        }
        logger.verbose("exit");
    }

    private void CreateUriAndAddToList(Set<URL> allResourceUris, URL baseUrl, String uri) {
        if (uri.toLowerCase().startsWith("data:")) return;
        try {
            URL url = new URL(baseUrl, uri);
            allResourceUris.add(url);
        } catch (MalformedURLException e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
    }

    private <T extends IHasCSSDeclarations<T>> void getAllResourcesUrisFromDeclarations(Set<URL> allResourceUris, IHasCSSDeclarations<T> rule, String propertyName, URL baseUrl) {
        ICommonsList<CSSDeclaration> sourcesList = rule.getAllDeclarationsOfPropertyName(propertyName);
        for (CSSDeclaration cssDeclaration : sourcesList) {
            CSSExpression cssDeclarationExpression = cssDeclaration.getExpression();
            ICommonsList<ICSSExpressionMember> allExpressionMembers = cssDeclarationExpression.getAllMembers();
            ICommonsList<CSSExpressionMemberTermURI> allUriExpressions = allExpressionMembers.getAllInstanceOf(CSSExpressionMemberTermURI.class);
            for (CSSExpressionMemberTermURI uriExpression : allUriExpressions) {
                String uri = uriExpression.getURIString();
                CreateUriAndAddToList(allResourceUris, baseUrl, uri);
            }
        }
    }
//
//    private <T extends IHasCSSDeclarations<T>> void getAllFontResourcesUrisFromDeclarations(Set<URL> allResourceUris, IHasCSSDeclarations<CSSFontFaceRule> rule, String propertyName, URL baseUrl) {
//        ICommonsList<CSSDeclaration> sourcesList = rule.getAllDeclarationsOfPropertyName(propertyName);
//        for (CSSDeclaration cssDeclaration : sourcesList) {
//            CSSExpression cssDeclarationExpression = cssDeclaration.getExpression();
//            ICommonsList<ICSSExpressionMember> allExpressionMembers = cssDeclarationExpression.getAllMembers();
//            ICommonsList<CSSExpressionMemberTermURI> allUriExpressions = allExpressionMembers.getAllInstanceOf(CSSExpressionMemberTermURI.class);
//            for (CSSExpressionMemberTermURI uriExpression : allUriExpressions) {
//                try {
//                    String uri = uriExpression.getURIString();
//                    if (uri.toLowerCase().startsWith("data:")) continue;
//                    URL url = new URL(baseUrl, uri);
//                    allResourceUris.add(url);
//                } catch (MalformedURLException e) {
//                    GeneralUtils.logExceptionStackTrace(logger, e);
//                }
//            }
//        }
//    }
//

    private List<RGridResource> addBlobsToCache(Map<String, RGridResource> allBlobs) {

        logger.verbose(String.format("trying to add %d blobs to cache", allBlobs.size()));
        logger.verbose(String.format("current fetchedCacheMap size: %d", fetchedCacheMap.size()));
        List<RGridResource> unparsedResources = new ArrayList<>();
        String url;
        for (RGridResource blob : allBlobs.values()) {
            url = blob.getUrl();
            String contentType = blob.getContentType();
            try {
                if (contentType == null || !contentType.equalsIgnoreCase(CDT)) {
                    IResourceFuture resourceFuture = this.eyesConnector.createResourceFuture(blob);
                    fetchedCacheMap.put(url, resourceFuture);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            unparsedResources.add(blob);
        }
        return unparsedResources;
    }

    private void fetchAllResources(final Map<String, RGridResource> allBlobs, Set<URL> resourceUrls, FrameData result) {
        logger.verbose("enter");
        if (resourceUrls.isEmpty()) {
            return;
        }
        List<IResourceFuture> allFetches = new ArrayList<>();
        final Iterator<URL> iterator = resourceUrls.iterator();
        while (iterator.hasNext()) {
            URL link = iterator.next();
            String url = link.toString();
            url = GeneralUtils.sanitizeURL(url, logger);
            synchronized (this.fetchedCacheMap) {
                // If resource is already being fetched, remove it from the list, and use the future.
                IResourceFuture fetch = fetchedCacheMap.get(url);
                if (fetch != null) {
                    logger.verbose("cache hit for url " + url);
                    allFetches.add(fetch);
                    continue;
                }

                // If resource is not being fetched yet (limited guarantee)
                IEyesConnector eyesConnector = this.visualGridTaskList.get(0).getEyesConnector();
                IResourceFuture future;
                try {
                    future = eyesConnector.getResource(link, userAgent.getOriginalUserAgentString());
                } catch (Exception e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                    iterator.remove();
                    continue;
                }

                if (!this.fetchedCacheMap.containsKey(url)) {
                    this.fetchedCacheMap.put(url, future);
                    allFetches.add(future);
                    logger.verbose("this.fetchedCacheMap.put(" + url + ")");
                } else {
                    logger.verbose("this.fetchedCacheMap.containsKey(" + url + ")");
                }
            }
        }

        logger.verbose("starting to fetch( " + allFetches.size() + ") fetched resources");
        for (IResourceFuture future : allFetches) {
            try {
                RGridResource resource;
                String url = future.getUrl();
                logger.verbose("trying future.get() for resource " + url + " ...");
                resource = future.get();


                logger.verbose("finishing future.get() for resource " + url + " ...");
                logger.verbose("done getting resource " + url);
                try {
                    this.debugResourceWriter.write(resource);
                } catch (Exception e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                }
                logger.verbose("done writing to debugWriter");
                if (resource == null) {
                    logger.verbose("Resource is null (" + url + ") ");
                    continue;
                }
                if (resource.isResourceParsed()) continue;

                removeUrlFromList(url, resourceUrls);
                allBlobs.put(resource.getUrl(), resource);

                String contentType = resource.getContentType();
                logger.verbose("handling " + contentType + " resource from URL: " + url);
                Set<URL> newResourceUrls = new HashSet<>();
                getAndParseResource(resource, result.getUrl(), newResourceUrls);
                resource.setIsResourceParsed(true);
                fetchAllResources(allBlobs, newResourceUrls, result);
            } catch (Throwable e) {
                e.printStackTrace();
            }

        }
        logger.verbose("finished fetching(" + allFetches.size() + ")");
        logger.verbose("exit");
    }

    private void removeUrlFromList(String url, Set<URL> resourceUrls) {
        Iterator<URL> iterator = resourceUrls.iterator();
        while (iterator.hasNext()) {
            URL resourceUrl = iterator.next();
            if (resourceUrl.toString().equalsIgnoreCase(url)) {
                iterator.remove();
            }
        }
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
                                    logger.verbose("setting openVisualGridTask " + openVisualGridTask + " render result: " + renderStatusResults + " to url " + this.result.getUrl());
                                    openVisualGridTask.setRenderResult(renderStatusResults);
                                } else {
                                    logger.verbose("setting openVisualGridTask " + openVisualGridTask + " render error: " + removedId + " to url " + this.result.getUrl());
                                    openVisualGridTask.setRenderError(removedId, renderStatusResults.getError());
                                }
                                iterator.remove();
                            }
                        }
                        logger.verbose("setting visualGridTask " + visualGridTask + " render result: " + renderStatusResults + " to url " + this.result.getUrl());
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

