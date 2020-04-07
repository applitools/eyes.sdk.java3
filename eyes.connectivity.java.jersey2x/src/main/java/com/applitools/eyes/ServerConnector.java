/*
 * Applitools SDK for Selenium integration.
 */
package com.applitools.eyes;

import com.applitools.IResourceUploadListener;
import com.applitools.eyes.visualgrid.PutFuture;
import com.applitools.eyes.visualgrid.ResourceFuture;
import com.applitools.eyes.visualgrid.model.*;
import com.applitools.eyes.visualgrid.services.IResourceFuture;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.io.IOUtils;
import org.brotli.dec.BrotliInputStream;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Provides an API for communication with the Applitools agent
 */
public class ServerConnector extends RestClient implements IServerConnector {

    private String apiKey = null;
    private RenderingInfo renderingInfo;

    /***
     * @param logger A logger instance.
     * @param serverUrl The URI of the Eyes server.
     */
    public ServerConnector(Logger logger, URI serverUrl) {
        super(logger, serverUrl, TIMEOUT);
        endPoint = endPoint.path(API_PATH);
    }

    /***
     * @param logger A logger instance.
     */
    public ServerConnector(Logger logger) {
        this(logger, GeneralUtils.geServerUrl());
    }

    /***
     * @param serverUrl The URI of the Eyes server.
     */
    public ServerConnector(URI serverUrl) {
        this(null, serverUrl);
    }

    public ServerConnector() {
        this((Logger) null);
    }

    /**
     * Sets the API key of your applitools Eyes account.
     * @param apiKey The api key to set.
     */
    public void setApiKey(String apiKey) {
        ArgumentGuard.notNull(apiKey, "apiKey");
        this.apiKey = apiKey;
    }

    /**
     * @return The currently set API key or {@code null} if no key is set.
     */
    public String getApiKey() {
        String apiKey = this.apiKey != null ? this.apiKey : GeneralUtils.getEnvString("APPLITOOLS_API_KEY");
        apiKey = apiKey == null ? GeneralUtils.getEnvString("bamboo_APPLITOOLS_API_KEY") : apiKey;
        return apiKey;
    }

    @Override
    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    @Override
    public String getAgentId() {
        return this.agentId;
    }

    /**
     * Sets the proxy settings to be used by the rest client.
     * @param proxySettings The proxy settings to be used by the rest client.
     *                      If {@code null} then no proxy is set.
     */
    @SuppressWarnings("UnusedDeclaration")
    public void setProxy(AbstractProxySettings proxySettings) {
        setProxyBase(proxySettings);
        // After the server is updated we must make sure the endpoint refers
        // to the correct path.
        endPoint = endPoint.path(API_PATH);
    }

    /**
     * @return The current proxy settings used by the rest client,
     * or {@code null} if no proxy is set.
     */
    @SuppressWarnings("UnusedDeclaration")
    public AbstractProxySettings getProxy() {
        return getProxyBase();
    }

    /**
     * Sets the current server URL used by the rest client.
     * @param serverUrl The URI of the rest server.
     */
    @SuppressWarnings("UnusedDeclaration")
    public void setServerUrl(URI serverUrl) {
        setServerUrlBase(serverUrl);
        // After the server is updated we must make sure the endpoint refers
        // to the correct path.
        endPoint = endPoint.path(API_PATH);
    }

    /**
     * @return The URI of the eyes server.
     */
    @SuppressWarnings("UnusedDeclaration")
    public URI getServerUrl() {
        return getServerUrlBase();
    }

    /**
     * Starts a new running session in the agent. Based on the given parameters,
     * this running session will either be linked to an existing session, or to
     * a completely new session.
     * @param sessionStartInfo The start parameters for the session.
     * @return RunningSession object which represents the current running
     * session
     * @throws EyesException For invalid status codes, or if response parsing
     *                       failed.
     */
    public RunningSession startSession(SessionStartInfo sessionStartInfo)
            throws EyesException {

        ArgumentGuard.notNull(sessionStartInfo, "sessionStartInfo");

        logger.verbose("Using Jersey2 for REST API calls.");

        configureRestClient();

        String postData;
        Response response;
        try {

            // since the web API requires a root property for this message
            jsonMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
            postData = jsonMapper.writeValueAsString(sessionStartInfo);

            // returning the root property addition back to false (default)
            jsonMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        } catch (IOException e) {
            throw new EyesException("Failed to convert " +
                    "sessionStartInfo into Json string!", e);
        }

        try {
            Map<String, Object> queryParams = new HashMap<String, Object>(){{
                put("apiKey", getApiKey());
            }};
            Invocation.Builder request = makeEyesRequest(endPoint, queryParams);
            response = sendLongRequest(request, HttpMethod.POST, Entity.entity(postData, MediaType.APPLICATION_JSON));
        } catch (RuntimeException e) {
            logger.log("Server request failed: " + e.getMessage());
            throw e;
        }

        // Ok, let's create the running session from the response
        List<Integer> validStatusCodes = new ArrayList<>();
        validStatusCodes.add(Response.Status.OK.getStatusCode());
        validStatusCodes.add(Response.Status.CREATED.getStatusCode());

        response.bufferEntity();
        String responseDataString = response.readEntity(String.class);
        Map<?,?> responseData;
        try {
            responseData = GeneralUtils.parseJsonToObject(responseDataString, Map.class);
        } catch (IOException e) {
            String errorMessage = getReadResponseError(
                    "Failed to de-serialize response body",
                    response.getStatus(),
                    response.getStatusInfo().getReasonPhrase(),
                    responseDataString);

            throw new EyesException(errorMessage, e);
        }

        // If this is a new session, we set this flag.
        boolean isNewSession = (response.getStatus() == Response.Status.CREATED.getStatusCode() || responseData.containsKey("isNew"));
        RunningSession runningSession = parseResponseWithJsonData(response, validStatusCodes, RunningSession.class);
        runningSession.setIsNewSession(isNewSession);

        return runningSession;
    }

    /**
     * Stops the running session.
     * @param runningSession The running session to be stopped.
     * @return TestResults object for the stopped running session
     * @throws EyesException For invalid status codes, or if response parsing
     *                       failed.
     */
    public TestResults stopSession(final RunningSession runningSession,
                                   final boolean isAborted, final boolean save)
            throws EyesException {

        ArgumentGuard.notNull(runningSession, "runningSession");

        final String sessionId = runningSession.getId();
        Response response;
        List<Integer> validStatusCodes;
        TestResults result;

        Map<String, Object> queryParams = new HashMap<String, Object>(){{
            put("apiKey", getApiKey());
            put("aborted", String.valueOf(isAborted));
            put("updateBaseline", String.valueOf(save));
        }};
        Invocation.Builder request = makeEyesRequest(endPoint.path(sessionId), queryParams);
        response = sendLongRequest(request, HttpMethod.DELETE, null);

        // Ok, let's create the running session from the response
        validStatusCodes = new ArrayList<>();
        validStatusCodes.add(Response.Status.OK.getStatusCode());

        result = parseResponseWithJsonData(response, validStatusCodes,
                TestResults.class);
        return result;
    }

    @Override
    public void deleteSession(final TestResults testResults) {
        ArgumentGuard.notNull(testResults, "testResults");

        configureRestClient();

        WebTarget target = restClient.target(serverUrl)
                .path("/api/sessions/batches/")
                .path(testResults.getBatchId())
                .path("/")
                .path(testResults.getId());

        Map<String, Object> queryParams = new HashMap<String, Object>(){{
            put("apiKey", getApiKey());
            put("AccessToken", testResults.getSecretToken());
        }};

        Invocation.Builder request = makeEyesRequest(target, queryParams);

        @SuppressWarnings("unused")
        Response response = request.delete();
    }

    /**
     * Matches the current window (held by the WebDriver) to the expected
     * window.
     * @param runningSession The current agent's running session.
     * @param matchData      Encapsulation of a capture taken from the application.
     * @return The results of the window matching.
     * @throws EyesException For invalid status codes, or response parsing
     *                       failed.
     */
    public MatchResult matchWindow(RunningSession runningSession,
                                   MatchWindowData matchData)
            throws EyesException {

        ArgumentGuard.notNull(runningSession, "runningSession");
        ArgumentGuard.notNull(matchData, "model");

        Response response;
        List<Integer> validStatusCodes;
        MatchResult result;
        final String jsonData;

        // Serializing model into JSON (we'll treat it as binary later).
        try {
            jsonData = jsonMapper.writeValueAsString(matchData);
        } catch (IOException e) {
            throw new EyesException("Failed to serialize model for matchWindow!", e);
        }

        // Sending the request
        Map<String, Object> queryParams = new HashMap<String, Object>(){{
            put("apiKey", getApiKey());
        }};

        Invocation.Builder request = makeEyesRequest(endPoint.path(runningSession.getId()), queryParams);
        response = sendLongRequest(request, HttpMethod.POST, Entity.entity(jsonData, MediaType.APPLICATION_JSON));

        // Ok, let's create the running session from the response
        validStatusCodes = new ArrayList<>(1);
        validStatusCodes.add(Response.Status.OK.getStatusCode());

        result = parseResponseWithJsonData(response, validStatusCodes, MatchResult.class);

        return result;
    }

    @Override
    public int uploadData(byte[] bytes, RenderingInfo renderingInfo, String targetUrl, String contentType, String mediaType) {
        Invocation.Builder request = makeEyesRequest(restClient.target(targetUrl), null, contentType, mediaType)
                .header("X-Auth-Token", renderingInfo.getAccessToken())
                .header("x-ms-blob-type", "BlockBlob");

        Response response = request.put(Entity.entity(bytes, mediaType));
        int statusCode = response.getStatus();
        response.close();
        logger.verbose("Upload Status Code: " + statusCode);
        return statusCode;
    }

    @Override
    public void downloadString(final URL uri, final boolean isSecondRetry, final IDownloadListener<String> listener) {
        WebTarget target = this.restClient.target(uri.toString());
        Invocation.Builder request = makeEyesRequest(target, null, MediaType.WILDCARD);

        logger.verbose("Firing async GET");

        request.async().get(new InvocationCallback<Response>() {
            @Override
            public void completed(Response response) {
                try {
                    byte[] resource = downloadFile(response);
                    listener.onDownloadComplete(new String(resource), null);
                    logger.verbose("response finished");
                } catch (Throwable e) {
                    logger.verbose(e.getMessage());
                } finally {
                    response.close();
                }
            }

            @Override
            public void failed(Throwable throwable) {
                // on fail
                if (!isSecondRetry) {
                    logger.verbose("Async GET failed - entering retry");
                    downloadString(uri, true, listener);
                } else {
                    listener.onDownloadFailed();
                }
            }
        });

    }

    @Override
    public IResourceFuture downloadResource(final URL url, String userAgent, ResourceFuture resourceFuture) {
        WebTarget target = restClient.target(url.toString());
        Invocation.Builder request = makeEyesRequest(target, null, MediaType.WILDCARD);
        request.header("User-Agent", userAgent);

        final IResourceFuture newFuture = new ResourceFuture(url.toString(), logger, this, userAgent);

        Future<Response> responseFuture = request.async().get(new InvocationCallback<Response>() {
            @Override
            public void completed(Response response) {
                logger.verbose("GET callback  success");
                int status = response.getStatus();
                List<String> contentLengthHeaders = response.getStringHeaders().get("Content-length");
                int contentLength = 0;
                if (contentLengthHeaders != null) {
                    contentLength = Integer.parseInt(contentLengthHeaders.get(0));
                    logger.verbose("Content Length: " + contentLength);
                }

                logger.verbose("downloading url - : " + url);

                if (status == 404) {
                    logger.verbose("Status 404 on url - " + url);
                }

                if ((status == 200 || status == 201)) {
                    logger.verbose("response: " + response);
                    byte[] content = downloadFile(response);

                    String contentType = Utils.getResponseContentType(response);
                    String contentEncoding = Utils.getResponseContentEncoding(response);
                    if (contentEncoding != null && contentEncoding.contains("gzip")) {
                        try {
                            content = GeneralUtils.getUnGzipByteArrayOutputStream(content);
                        } catch (IOException e) {
                            GeneralUtils.logExceptionStackTrace(logger, e);
                        }
                    }
                    RGridResource rgResource = new RGridResource(url.toString(), contentType, content, logger, "ResourceFuture");

                    newFuture.setResource(rgResource);

                }
                response.close();
                logger.verbose("Response closed");
            }

            @Override
            public void failed(Throwable throwable) {
                logger.verbose("PUT callback failed");
            }
        });

        newFuture.setResponseFuture(responseFuture);

        return newFuture;
    }

    private Response sendWithRetry(String method, Invocation.Builder request, Entity entity, AtomicInteger retiresCounter) {
        if (retiresCounter == null) {
            retiresCounter = new AtomicInteger(0);

        }

        Response response = null;
        try {
            switch (method) {

                case HttpMethod.POST:
                    response = request.post(entity);
                    break;
                case HttpMethod.PUT:
                    response = request.put(entity);
            }

            return response;
        } catch (Exception e) {

            GeneralUtils.logExceptionStackTrace(logger, e);
            try {

                Thread.sleep(THREAD_SLEEP_MILLIS);

            } catch (InterruptedException e1) {
                GeneralUtils.logExceptionStackTrace(logger, e1);
            }

            if (retiresCounter.incrementAndGet() < NUM_OF_RETRIES) {

                return sendWithRetry(method, request, entity, retiresCounter);
            } else {
                throw e;
            }
        }

    }


    @Override
    public RenderingInfo getRenderInfo() {
        if (renderingInfo == null) {
            WebTarget target = restClient.target(serverUrl).path((RENDER_INFO_PATH));
            Map<String, Object> queryParams = new HashMap<String, Object>(){{
                put("apiKey", getApiKey());
            }};
            Invocation.Builder request = makeEyesRequest(target, queryParams);

            // Ok, let's create the running session from the response
            List<Integer> validStatusCodes = new ArrayList<>();
            validStatusCodes.add(Response.Status.OK.getStatusCode());

            Response response = request.get();
            renderingInfo = parseResponseWithJsonData(response, validStatusCodes, RenderingInfo.class);
        }
        return renderingInfo;
    }

    @Override
    public List<RunningRender> render(RenderRequest... renderRequests) {
        ArgumentGuard.notNull(renderRequests, "renderRequests");
        this.logger.verbose("called with " + Arrays.toString(renderRequests));

        WebTarget target = restClient.target(renderingInfo.getServiceUrl()).path((RENDER));
        if (renderRequests.length > 1) {
            target.matrixParam("render-id", (Object) renderRequests);
        } else if (renderRequests.length == 1) {
            target.queryParam("render-id", (Object) renderRequests);
        }
        Invocation.Builder request = makeEyesRequest(target);
        request.header("X-Auth-Token", renderingInfo.getAccessToken());

        // Ok, let's create the running session from the response
        List<Integer> validStatusCodes = new ArrayList<>();
        validStatusCodes.add(Response.Status.OK.getStatusCode());
        validStatusCodes.add(Response.Status.NOT_FOUND.getStatusCode());

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
            String json = objectMapper.writeValueAsString(renderRequests);
            Response response = request.post(Entity.json(json));
            if (validStatusCodes.contains(response.getStatus())) {
                RunningRender[] runningRenders = parseResponseWithJsonData(response, validStatusCodes, RunningRender[].class);
                return Arrays.asList(runningRenders);
            }
            throw new EyesException("Jersey2 ServerConnector.render - unexpected status (" + response.getStatus() + "), msg (" + response.readEntity(String.class) + ")");
        } catch (JsonProcessingException e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }

        return null;
    }

    @Override
    public boolean renderCheckResource(final RunningRender runningRender, RGridResource resource) {
        ArgumentGuard.notNull(runningRender, "runningRender");
        ArgumentGuard.notNull(resource, "resource");
        // eslint-disable-next-line max-len
        this.logger.verbose("called with resource#" + resource.getSha256() + " for render: " + runningRender.getRenderId());

        WebTarget target = restClient.target(renderingInfo.getServiceUrl()).path((RESOURCES_SHA_256) + resource.getSha256());
        Map<String, Object> queryParams = new HashMap<String, Object>(){{
            put("render-id", runningRender.getRenderId());
        }};
        Invocation.Builder request = makeEyesRequest(target, queryParams);
        request.header("X-Auth-Token", renderingInfo.getAccessToken());

        // Ok, let's create the running session from the response
        List<Integer> validStatusCodes = new ArrayList<>();
        validStatusCodes.add(Response.Status.OK.getStatusCode());
        validStatusCodes.add(Response.Status.NOT_FOUND.getStatusCode());

        Response response = request.head();
        if (validStatusCodes.contains(response.getStatus())) {
            this.logger.verbose("request succeeded");
            return response.getStatus() == Response.Status.OK.getStatusCode();
        }

        throw new EyesException("Jersey2 ServerConnector.renderCheckResource - unexpected status (" + response.getStatus() + ")");
    }

    @Override
    public IPutFuture renderPutResource(final RunningRender runningRender, final RGridResource resource, String userAgent, final IResourceUploadListener listener) {
        ArgumentGuard.notNull(runningRender, "runningRender");
        ArgumentGuard.notNull(resource, "resource");
        byte[] content = resource.getContent();
        ArgumentGuard.notNull(content, "resource.getContent()");

        String hash = resource.getSha256();
        final String renderId = runningRender.getRenderId();
        logger.verbose("resource hash:" + hash + " ; url: " + resource.getUrl() + " ; render id: " + renderId);

        WebTarget target = restClient.target(renderingInfo.getServiceUrl()).path(RESOURCES_SHA_256 + hash);
        Map<String, Object> queryParams = new HashMap<String, Object>(){{
            put("render-id", renderId);
        }};

        String contentType = resource.getContentType();
        Invocation.Builder request = makeEyesRequest(target, queryParams, contentType);
        request.header("X-Auth-Token", renderingInfo.getAccessToken());
        request.header("User-Agent", userAgent);
        Entity entity;
        if (contentType != null && !"None".equalsIgnoreCase(contentType)) {
            entity = Entity.entity(content, contentType);

        } else {
            entity = Entity.entity(content, MediaType.APPLICATION_OCTET_STREAM_TYPE);
        }
        final Future<Response> future;
        future = request.async().put(entity, new InvocationCallback<Response>() {
            @Override
            public void completed(Response response) {
                logger.verbose("PUT callback  success");
                response.close();
                logger.verbose("Response closed");
            }

            @Override
            public void failed(Throwable throwable) {
                logger.verbose("PUT callback failed");
            }
        });
        logger.verbose("future created.");
        PutFuture putFuture = new PutFuture(future, resource, runningRender, this, logger, userAgent);
        return putFuture;
    }


    @Override
    public RenderStatusResults renderStatus(RunningRender runningRender) {
        return null;
    }

    @Override
    public List<RenderStatusResults> renderStatusById(String... renderIds) {

        Response response = null;
        try {
            ArgumentGuard.notNull(renderIds, "renderIds");
            this.logger.verbose("called for render: " + Arrays.toString(renderIds));

            WebTarget target = restClient.target(renderingInfo.getServiceUrl()).path((RENDER_STATUS));
            Invocation.Builder request = makeEyesRequest(target, null, MediaType.TEXT_PLAIN);
            request.header("X-Auth-Token", renderingInfo.getAccessToken());

            // Ok, let's create the running session from the response
            List<Integer> validStatusCodes = new ArrayList<>();
            validStatusCodes.add(Response.Status.OK.getStatusCode());
            validStatusCodes.add(Response.Status.NOT_FOUND.getStatusCode());

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
            try {
                String json = objectMapper.writeValueAsString(renderIds);
                Entity<String> entity = Entity.entity(json, MediaType.APPLICATION_JSON);
                response = request.post(entity);
                if (validStatusCodes.contains(response.getStatus())) {
                    this.logger.verbose("request succeeded");
                    RenderStatusResults[] renderStatusResults = parseResponseWithJsonData(response, validStatusCodes, RenderStatusResults[].class);
                    for (int i = 0; i < renderStatusResults.length; i++) {
                        RenderStatusResults renderStatusResult = renderStatusResults[i];
                        if (renderStatusResult != null && renderStatusResult.getStatus() == RenderStatus.ERROR) {
                            logger.verbose("error on render id - " + renderStatusResult);
                        }
                    }
                    return Arrays.asList(renderStatusResults);
                }
            } catch (JsonProcessingException e) {
                logger.log("exception in render status");
                GeneralUtils.logExceptionStackTrace(logger, e);
            }
            return null;
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return null;

    }

    @Override
    public IResourceFuture createResourceFuture(RGridResource gridResource, String userAgent) {
        return new ResourceFuture(gridResource, logger, this, userAgent);

    }

    @Override
    public void setRenderingInfo(RenderingInfo renderInfo) {
        this.renderingInfo = renderInfo;
    }

    @Override
    public void closeBatch(String batchId) {
        String dontCloseBatchesStr = GeneralUtils.getEnvString("APPLITOOLS_DONT_CLOSE_BATCHES");
        if (Boolean.parseBoolean(dontCloseBatchesStr)) {
            logger.log("APPLITOOLS_DONT_CLOSE_BATCHES environment variable set to true. Skipping batch close.");
            return;
        }
        ArgumentGuard.notNull(batchId, "batchId");
        this.logger.verbose("called with " + batchId);
        this.configureRestClient();
        String url = String.format(CLOSE_BATCH, batchId);
        WebTarget target = restClient.target(serverUrl).path(url);
        Map<String, Object> queryParams = new HashMap<String, Object>(){{
            put("apiKey", getApiKey());
        }};
        Response delete = makeEyesRequest(target, queryParams, (String) null).delete();
        logger.verbose("delete batch is done with " + delete.getStatus() + " status");
        this.restClient.close();
    }

    @Override
    public void closeConnector() {
        if (restClient != null) {
            restClient.close();
        }
    }

    @Override
    public boolean getDontCloseBatches() {
        return "true".equalsIgnoreCase(GeneralUtils.getEnvString("APPLITOOLS_DONT_CLOSE_BATCHES"));
    }

    private byte[] downloadFile(Response response) {

        InputStream inputStream = response.readEntity(InputStream.class);
        Object contentEncoding = response.getHeaders().getFirst("Content-Encoding");
        byte[] bytes = new byte[0];
        try {
            if ("br".equalsIgnoreCase((String) contentEncoding)) {
                inputStream = new BrotliInputStream(inputStream);
            }
            bytes = IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
        return bytes;
    }

    private void configureRestClient() {
        restClient = buildRestClient(getTimeout(), getProxy());
        endPoint = restClient.target(serverUrl);
        endPoint = endPoint.path(API_PATH);
    }

    @Override
    protected Response sendHttpWebRequest(String path, final String method, String accept) {
        Map<String, Object> queryParams = new HashMap<String, Object>(){{
            put("apiKey", getApiKey());
        }};
        Invocation.Builder invocationBuilder = makeEyesRequest(restClient.target(path), queryParams, accept);
        return invocationBuilder.method(method);
    }
}
