/*
 * Applitools SDK for Selenium integration.
 */
package com.applitools.eyes;

import com.applitools.IResourceUploadListener;
import com.applitools.eyes.visualgrid.PutFuture;
import com.applitools.eyes.visualgrid.ResourceFuture;
import com.applitools.eyes.visualgrid.services.IResourceFuture;
import com.applitools.eyes.visualgrid.model.*;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sun.jersey.api.client.AsyncWebResource;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.async.TypeListener;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.apache.commons.io.IOUtils;
import org.brotli.dec.BrotliInputStream;

import javax.jws.WebParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Provides an API for communication with the Applitools agent
 */
public class ServerConnector extends RestClient
        implements IServerConnector {

    private static final int TIMEOUT = 1000 * 60 * 5; // 5 Minutes
    private static final String API_PATH = "/api/sessions/running";
    private static final String DEFAULT_CHARSET_NAME = "UTF-8";
    public static final String RENDER_ID = "render-id";

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
    @SuppressWarnings("WeakerAccess")
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
     *
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
        return this.apiKey != null ? this.apiKey : GeneralUtils.getEnvString("APPLITOOLS_API_KEY");
    }

    /**
     * Sets the proxy settings to be used by the rest client.
     *
     * @param abstractProxySettings The proxy settings to be used by the rest client.
     *                              If {@code null} then no proxy is set.
     */
    @SuppressWarnings("UnusedDeclaration")
    public void setProxy(AbstractProxySettings abstractProxySettings) {
        setProxyBase(abstractProxySettings);
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
     *
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
     *
     * @param sessionStartInfo The start parameters for the session.
     * @return RunningSession object which represents the current running
     * session
     * @throws EyesException For invalid status codes, or if response parsing
     *                       failed.
     */
    public RunningSession startSession(SessionStartInfo sessionStartInfo)
            throws EyesException {

        ArgumentGuard.notNull(sessionStartInfo, "sessionStartInfo");

        logger.verbose("Using Jersey1 for REST API calls.");

        String postData;
        ClientResponse response;
        int statusCode;
        List<Integer> validStatusCodes;
        boolean isNewSession;
        RunningSession runningSession;

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
            response = endPoint.queryParam("apiKey", getApiKey()).
                    accept(MediaType.APPLICATION_JSON).
                    entity(postData, MediaType.APPLICATION_JSON_TYPE).
                    post(ClientResponse.class);
        } catch (RuntimeException e) {
            logger.log("startSession(): Server request failed: " + e.getMessage());
            throw e;
        }

        // Ok, let's create the running session from the response
        validStatusCodes = new ArrayList<>();
        validStatusCodes.add(ClientResponse.Status.OK.getStatusCode());
        validStatusCodes.add(ClientResponse.Status.CREATED.getStatusCode());

        runningSession = parseResponseWithJsonData(response, validStatusCodes,
                RunningSession.class);

        // If this is a new session, we set this flag.
        statusCode = response.getStatus();
        isNewSession = (statusCode == ClientResponse.Status.CREATED.getStatusCode());
        runningSession.setIsNewSession(isNewSession);

        return runningSession;
    }

    /**
     * Stops the running session.
     *
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
        ClientResponse response;
        List<Integer> validStatusCodes;
        TestResults result;

        HttpMethodCall delete = new HttpMethodCall() {
            public ClientResponse call() {

                String currentTime = GeneralUtils.toRfc1123(
                        Calendar.getInstance(TimeZone.getTimeZone("UTC")));

                // Building the request
                WebResource.Builder builder = endPoint.path(sessionId)
                        .queryParam("apiKey", getApiKey())
                        .queryParam("aborted", String.valueOf(isAborted))
                        .queryParam("updateBaseline", String.valueOf(save))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Eyes-Expect", "202-accepted")
                        .header("Eyes-Date", currentTime);

                // Actually perform the method call and return the result
                return builder.delete(ClientResponse.class);
            }
        };

        response = sendLongRequest(delete, "stopSession");

        // Ok, let's create the running session from the response
        validStatusCodes = new ArrayList<>();
        validStatusCodes.add(ClientResponse.Status.OK.getStatusCode());

        result = parseResponseWithJsonData(response, validStatusCodes,
                TestResults.class);
        return result;
    }

    @Override
    public void deleteSession(TestResults testResults) {
        ArgumentGuard.notNull(testResults, "testResults");

        WebResource sessionsResources = restClient.resource(serverUrl);
        WebResource.Builder builder = sessionsResources
                .path("/api/sessions/batches/")
                .path(testResults.getBatchId())
                .path("/")
                .path(testResults.getId())
                .queryParam("apiKey", getApiKey())
                .queryParam("AccessToken", testResults.getSecretToken())
                .accept(MediaType.APPLICATION_JSON);

        builder.delete();
    }

    /**
     * Matches the current window (held by the WebDriver) to the expected
     * window.
     *
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

        ClientResponse response;
        List<Integer> validStatusCodes;
        MatchResult result;
        String jsonData;

        // since we rather not add an empty "tag" param
        WebResource runningSessionsEndpoint =
                endPoint.path(runningSession.getId());

        // Serializing model into JSON (we'll treat it as binary later).
        // IMPORTANT This serializes everything EXCEPT for the screenshot (which
        // we'll add later).
        try {
            jsonData = jsonMapper.writeValueAsString(matchData);
        } catch (IOException e) {
            throw new EyesException("Failed to serialize model for matchWindow!",
                    e);
        }

        // Convert the JSON to binary.
        byte[] jsonBytes;
        ByteArrayOutputStream jsonToBytesConverter = new ByteArrayOutputStream();
        try {
            jsonToBytesConverter.write(
                    jsonData.getBytes(DEFAULT_CHARSET_NAME));
            jsonToBytesConverter.flush();
            jsonBytes = jsonToBytesConverter.toByteArray();
        } catch (IOException e) {
            throw new EyesException("Failed create binary model from JSON!", e);
        }

        // Getting the screenshot's bytes (notice this can be either
        // compressed/uncompressed form).
        byte[] screenshot = new byte[0];
            String screenshot64 = matchData.getAppOutput().getScreenshot64();
        if (screenshot64 != null) {
            screenshot = DatatypeConverter.parseBase64Binary(
                    screenshot64);
        }

        // Ok, let's create the request model
        ByteArrayOutputStream requestOutputStream = new ByteArrayOutputStream();
        DataOutputStream requestDos = new DataOutputStream(requestOutputStream);
        byte[] requestData;
        try {
            requestDos.writeInt(jsonBytes.length);
            requestDos.flush();
            requestOutputStream.write(jsonBytes);
            if (screenshot64 != null) {
                requestOutputStream.write(screenshot);
            }
            requestOutputStream.flush();

            // Ok, get the model bytes
            requestData = requestOutputStream.toByteArray();

            // Release the streams
            requestDos.close();
        } catch (IOException e) {
            throw new EyesException("Failed send check window request!", e);
        }

        // Sending the request
        response = runningSessionsEndpoint.queryParam("apiKey", getApiKey()).
                accept(MediaType.APPLICATION_JSON).
                entity(requestData, MediaType.APPLICATION_OCTET_STREAM_TYPE).
                post(ClientResponse.class);

        // Ok, let's create the running session from the response
        validStatusCodes = new ArrayList<>(1);
        validStatusCodes.add(ClientResponse.Status.OK.getStatusCode());

        result = parseResponseWithJsonData(response, validStatusCodes,
                MatchResult.class);

        return result;

    }

    @Override
    public void downloadString(URL uri, boolean isSecondRetry, final IDownloadListener<String> listener) {

        AsyncWebResource target = Client.create().asyncResource(uri.toString());

        AsyncWebResource.Builder request = target.accept(MediaType.WILDCARD);

        request.get(new TypeListener<ClientResponse>(ClientResponse.class) {

            public void onComplete(Future<ClientResponse> f) {
                int status = 0;
                ClientResponse clientResponse = null;
                try {
                    clientResponse = f.get();
                    status = clientResponse.getStatus();
                    if (status > 300) {
                        logger.verbose("Got response status code - " + status);
                        listener.onDownloadFailed();
                        return;
                    }
                    InputStream entityInputStream = clientResponse.getEntityInputStream();

                    StringWriter writer = new StringWriter();

                    IOUtils.copy(entityInputStream, writer, "UTF-8");

                    String theString = writer.toString();

                    listener.onDownloadComplete(theString, null);

                } catch (Exception e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                    logger.verbose("Failed to parse request(status= " + status + ") = " + clientResponse.getEntity(String.class));
                    listener.onDownloadFailed();

                }
            }

        });
    }

    @Override
    public IResourceFuture downloadResource(final URL url, String userAgent) {
        Client client = RestClient.buildRestClient(getTimeout(), getProxy());

        AsyncWebResource target = client.asyncResource(url.toString());

        AsyncWebResource.Builder request = target.accept(MediaType.WILDCARD);

        request.header("User-Agent", userAgent);

        final IResourceFuture newFuture = new ResourceFuture(url.toString(), logger, this, userAgent);

        Future<ClientResponse> responseFuture =  request.get(new TypeListener<ClientResponse>(ClientResponse.class) {

            public void onComplete(Future<ClientResponse> future) {
                logger.verbose("GET callback  success");

                ClientResponse response = null;
                try {
                    response = future.get();
                } catch (InterruptedException | ExecutionException e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                }
                int status = response.getStatus();
                List<String> contentLengthHeaders = response.getHeaders().get("Content-length");
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

        });

        newFuture.setResponseFuture(responseFuture);

        return newFuture;
    }


    @Override
    public String postDomSnapshot(String domJson) {

        WebResource target = restClient.resource(serverUrl).path((RUNNING_DATA_PATH)).queryParam("apiKey", getApiKey());

        byte[] resultStream = GeneralUtils.getGzipByteArrayOutputStream(domJson);

        WebResource.Builder request = target.accept(MediaType.APPLICATION_JSON).entity(resultStream, MediaType.APPLICATION_OCTET_STREAM_TYPE);

        ClientResponse response = request.post(ClientResponse.class);

        MultivaluedMap<String, String> headers = response.getHeaders();

        List<String> location = headers.get("Location");
        String entity = null;
        if (!location.isEmpty()) {
            entity = location.get(0);
        }

        return entity;
    }

    @Override
    public RenderingInfo getRenderInfo() {
        this.logger.verbose("enter");
        WebResource target = restClient.resource(serverUrl).path(RENDER_INFO_PATH).queryParam("apiKey", getApiKey());


        WebResource.Builder request = target.accept(MediaType.APPLICATION_JSON);

        ClientResponse response = request.get(ClientResponse.class);

        // Ok, let's create the running session from the response
        List<Integer> validStatusCodes = new ArrayList<>(1);
        validStatusCodes.add(ClientResponse.Status.OK.getStatusCode());

        renderingInfo = parseResponseWithJsonData(response, validStatusCodes,
                RenderingInfo.class);
        return renderingInfo;
    }

    @Override
    public List<RunningRender> render(RenderRequest... renderRequests) {
        ArgumentGuard.notNull(renderRequests, "renderRequests");
        this.logger.verbose("called with " + Arrays.toString(renderRequests));

        WebResource target = restClient.resource(renderingInfo.getServiceUrl()).path((RENDER));
        WebResource webResource = null;

        if (renderRequests.length > 1) {
            MultivaluedMap<String, String> params = new MultivaluedMapImpl();
            List<String> idsAsList = extractIdsFromRRequests(renderRequests);
            if (idsAsList != null && !idsAsList.isEmpty()) {
                params.put(RENDER_ID, idsAsList);
                webResource = target.queryParams(params);
            }

        } else {
            webResource = target.queryParam("render-id", renderRequests[0].getRenderId());
        }
        WebResource.Builder builder = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
            byte[] bytes = objectMapper.writeValueAsString(renderRequests).getBytes();
            if (webResource == null) {
                builder = target.getRequestBuilder();
                builder = builder.entity(bytes, MediaType.APPLICATION_JSON);
            } else {

                builder = webResource.entity(bytes, MediaType.APPLICATION_JSON);
            }

        } catch (JsonProcessingException e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
        WebResource.Builder request = builder.accept(MediaType.APPLICATION_JSON).header("X-Auth-Token", renderingInfo.getAccessToken());

        // Ok, let's create the running session from the response
        List<Integer> validStatusCodes = new ArrayList<>();
        validStatusCodes.add(Response.Status.OK.getStatusCode());
        validStatusCodes.add(Response.Status.NOT_FOUND.getStatusCode());
        ClientResponse response = request.post(ClientResponse.class);

        if (validStatusCodes.contains(response.getStatus())) {
            RunningRender[] runningRenders = parseResponseWithJsonData(response, validStatusCodes, RunningRender[].class);
            return Arrays.asList(runningRenders);
        }
//            throw new EyesException("Jersey2 ServerConnector.render - unexpected status (" + response.getStatus() + "), msg (" + new String(response + ")");

        return null;
    }

    private List<String> extractIdsFromRRequests(RenderRequest[] renderRequests) {
        List ids = new ArrayList();
        for (RenderRequest renderRequest : renderRequests) {
            String renderId = renderRequest.getRenderId();
            if (renderId != null) {
                ids.add(renderId);
            }
        }
        return ids;
    }

    @Override
    public boolean renderCheckResource(RunningRender runningRender, RGridResource resource) {

        ArgumentGuard.notNull(runningRender, "runningRender");
        ArgumentGuard.notNull(resource, "resource");
        // eslint-disable-next-line max-len
        this.logger.verbose("called with resource#" + resource.getSha256() + " for render: " + runningRender.getRenderId());

        WebResource target = restClient.resource(renderingInfo.getServiceUrl()).path((RESOURCES_SHA_256) + resource.getSha256()).queryParam("render-id", runningRender.getRenderId());


        WebResource.Builder request = target.accept(MediaType.APPLICATION_JSON);

        // Ok, let's create the running session from the response
        List<Integer> validStatusCodes = new ArrayList<>();
        validStatusCodes.add(ClientResponse.Status.OK.getStatusCode());
        validStatusCodes.add(ClientResponse.Status.NOT_FOUND.getStatusCode());


        ClientResponse response = request.head();
        return response.getStatus() == ClientResponse.Status.OK.getStatusCode();
    }


    @Override
    public IPutFuture renderPutResource(final RunningRender runningRender, final RGridResource resource, String userAgent, final IResourceUploadListener listener) {
        ArgumentGuard.notNull(runningRender, "runningRender");
        ArgumentGuard.notNull(resource, "resource");
        byte[] content = resource.getContent();
        ArgumentGuard.notNull(content, "resource.getContent()");

        String hash = resource.getSha256();
        String renderId = runningRender.getRenderId();
        logger.verbose("resource hash:" + hash + " ; url: " + resource.getUrl() + " ; render id: " + renderId);

        AsyncWebResource target = restClient.asyncResource(renderingInfo.getServiceUrl())
                .path(RESOURCES_SHA_256 + hash)
                .queryParam("render-id", renderId);

        String contentType = resource.getContentType();
        AsyncWebResource.Builder builder;
        if (contentType != null && !"None".equalsIgnoreCase(contentType)) {
            builder = target.entity(content, contentType);

        } else {
            builder = target.entity(content, MediaType.APPLICATION_OCTET_STREAM_TYPE);
        }
        builder = builder.header("X-Auth-Token", renderingInfo.getAccessToken());
        final Future<ClientResponse> future = builder.put(ClientResponse.class);
        logger.verbose("future created.");
        PutFuture putFuture = new PutFuture(future, resource, runningRender, this, logger, userAgent);
        return putFuture;
    }

    @Override
    public RenderStatusResults renderStatus(RunningRender runningRender) {
        List<RenderStatusResults> renderStatusResults = renderStatusById(runningRender.getRenderId());
        if (!renderStatusResults.isEmpty()) {
            return renderStatusResults.get(0);
        }
        return null;
    }

    @Override
    public List<RenderStatusResults> renderStatusById(String... renderIds) {
        try {
            ArgumentGuard.notNull(renderIds, "renderIds");
            this.logger.verbose("called for render: " + Arrays.toString(renderIds));

            WebResource.Builder target = restClient.resource(renderingInfo.getServiceUrl()).path((RENDER_STATUS)).header("X-Auth-Token", renderingInfo.getAccessToken());

            // Ok, let's create the running session from the response
            List<Integer> validStatusCodes = new ArrayList<>();
            validStatusCodes.add(Response.Status.OK.getStatusCode());
            validStatusCodes.add(Response.Status.NOT_FOUND.getStatusCode());

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
            try {
                String json = objectMapper.writeValueAsString(renderIds);
                target = target.entity(json, MediaType.APPLICATION_JSON);
                ClientResponse response = target.post(ClientResponse.class);
                if (validStatusCodes.contains(response.getStatus())) {
                    this.logger.verbose("request succeeded");
                    RenderStatusResults[] renderStatusResults = parseResponseWithJsonData(response, validStatusCodes, RenderStatusResults[].class);
                    for (int i = 0; i < renderStatusResults.length; i++) {
                        RenderStatusResults renderStatusResult = renderStatusResults[i];
                        if(renderStatusResult != null && renderStatusResult.getStatus() == RenderStatus.ERROR){
                            logger.verbose("error on render id - "+renderStatusResult);
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
        boolean dontCloseBatchesStr = GeneralUtils.getDontCloseBatches();
        if (dontCloseBatchesStr)
        {
            logger.log("APPLITOOLS_DONT_CLOSE_BATCHES environment variable set to true. Skipping batch close.");
            return;
        }
        ArgumentGuard.notNull(batchId, "batchId");
        this.logger.verbose("called with " + batchId);

        String url = String.format(CLOSE_BATCH, batchId);
        WebResource target = restClient.resource(serverUrl).path(url).queryParam("apiKey", getApiKey());
        target.delete();
    }

    @Override
    public void closeConnector() {}
    
    public boolean getDontCloseBatches() {
        return "true".equalsIgnoreCase(GeneralUtils.getEnvString("APPLITOOLS_DONT_CLOSE_BATCHES"));
    }

    private byte[] downloadFile(ClientResponse response) {

        InputStream inputStream = response.getEntity(InputStream.class);
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
}
