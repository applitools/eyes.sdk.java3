package com.applitools.connectivity;

import com.applitools.connectivity.api.AsyncRequest;
import com.applitools.connectivity.api.AsyncRequestCallback;
import com.applitools.connectivity.api.HttpClient;
import com.applitools.connectivity.api.Response;
import com.applitools.eyes.*;
import com.applitools.eyes.locators.VisualLocatorsData;
import com.applitools.eyes.visualgrid.model.RenderRequest;
import com.applitools.eyes.visualgrid.model.RenderStatusResults;
import com.applitools.eyes.visualgrid.model.RenderingInfo;
import com.applitools.eyes.visualgrid.model.RunningRender;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.http.HttpStatus;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ServerConnector extends UfgConnector {

    static final String CLOSE_BATCH = "api/sessions/batches/%s/close/bypointerid";
    static final String RENDER_STATUS = "/render-status";
    static final String RENDER = "/render";
    public static final String API_PATH = "/api/sessions/running";

    /***
     * @param logger    Logger instance.
     * @param serverUrl The URI of the rest server.
     */
    public ServerConnector(Logger logger, URI serverUrl, int timeout) {
        super(logger, serverUrl, timeout);
    }

    public ServerConnector(Logger logger, URI serverUrl) {
        this(logger, serverUrl, DEFAULT_CLIENT_TIMEOUT);
    }

    public ServerConnector(Logger logger) {
        this(logger, GeneralUtils.getServerUrl());
    }

    public ServerConnector() {
        this(new Logger());
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getAgentId() {
        return this.agentId;
    }

    /**
     * Sets the current server URL used by the rest client.
     * @param serverUrl The URI of the rest server.
     */
    public void setServerUrl(URI serverUrl) {
        setServerUrlBase(serverUrl);
    }

    public URI getServerUrl() {
        return getServerUrlBase();
    }

    public void updateClient(HttpClient client) {
        restClient = client;
    }

    /**
     * Starts a new running session in the agent. Based on the given parameters,
     * this running session will either be linked to an existing session, or to
     * a completely new session.
     * @param sessionStartInfo The start parameters for the session.
     * @throws EyesException For invalid status codes, or if response parsing
     *                       failed.
     */
    public void startSession(final TaskListener<RunningSession> listener, SessionStartInfo sessionStartInfo) throws EyesException {
        ArgumentGuard.notNull(sessionStartInfo, "sessionStartInfo");
        logger.verbose("enter");
        initClient();
        String postData;
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

        AsyncRequest request = makeEyesRequest(new HttpRequestBuilder() {
            @Override
            public AsyncRequest build() {
                return restClient.target(serverUrl).path(API_PATH)
                        .queryParam("apiKey", getApiKey()).asyncRequest(MediaType.APPLICATION_JSON);
            }
        });

        AsyncRequestCallback callback = new AsyncRequestCallback() {
            @Override
            public void onComplete(Response response) {
                try {
                    List<Integer> validStatusCodes = new ArrayList<>();
                    validStatusCodes.add(HttpStatus.SC_OK);
                    validStatusCodes.add(HttpStatus.SC_CREATED);
                    RunningSession runningSession = parseResponseWithJsonData(response, validStatusCodes, new TypeReference<RunningSession>() {});
                    if (runningSession.getIsNew() == null) {
                        runningSession.setIsNew(response.getStatusCode() == HttpStatus.SC_CREATED);
                    }
                    listener.onComplete(runningSession);
                } catch (Throwable t) {
                    onFail(t);
                } finally {
                    response.close();
                }
            }

            @Override
            public void onFail(Throwable throwable) {
                GeneralUtils.logExceptionStackTrace(logger, throwable);
                listener.onFail();
            }
        };
        sendLongRequest(request, HttpMethod.POST, callback, postData, MediaType.APPLICATION_JSON);
    }

    /**
     * Stops the running session.
     * @param runningSession The running session to be stopped.
     * @throws EyesException For invalid status codes, or if response parsing
     *                       failed.
     */
    public void stopSession(TaskListener<TestResults> listener, final RunningSession runningSession,
                            final boolean isAborted, final boolean save) throws EyesException {
        ArgumentGuard.notNull(runningSession, "runningSession");
        AsyncRequest request = makeEyesRequest(new HttpRequestBuilder() {
            @Override
            public AsyncRequest build() {
                return restClient.target(serverUrl).path(API_PATH).path(runningSession.getId())
                        .queryParam("apiKey", getApiKey())
                        .queryParam("aborted", String.valueOf(isAborted))
                        .queryParam("updateBaseline", String.valueOf(save))
                        .asyncRequest(MediaType.APPLICATION_JSON);
            }
        });

        List<Integer> validStatusCodes = new ArrayList<>();
        validStatusCodes.add(HttpStatus.SC_OK);

        ResponseParsingCallback<TestResults> callback = new ResponseParsingCallback<>(this, validStatusCodes, listener, new TypeReference<TestResults>() {});
        sendLongRequest(request, HttpMethod.DELETE, callback, null, null);
    }

    public void deleteSession(final TaskListener<Void> listener, final TestResults testResults) {
        ArgumentGuard.notNull(testResults, "testResults");
        AsyncRequest request = makeEyesRequest(new HttpRequestBuilder() {
            @Override
            public AsyncRequest build() {
                return restClient.target(serverUrl)
                        .path("/api/sessions/batches/")
                        .path(testResults.getBatchId())
                        .path("/")
                        .path(testResults.getId())
                        .queryParam("apiKey", getApiKey())
                        .queryParam("AccessToken", testResults.getSecretToken())
                        .asyncRequest(MediaType.APPLICATION_JSON);
            }
        });

        sendAsyncRequest(request, HttpMethod.DELETE, new AsyncRequestCallback() {
            @Override
            public void onComplete(Response response) {
                listener.onComplete(null);
            }

            @Override
            public void onFail(Throwable throwable) {
                listener.onFail();
            }
        });
    }

    /**
     * Matches the current window (held by the WebDriver) to the expected
     * window.
     * @param runningSession The current agent's running session.
     * @param matchData      Encapsulation of a capture taken from the application.
     * @throws EyesException For invalid status codes, or response parsing
     *                       failed.
     */
    public void matchWindow(TaskListener<MatchResult> listener, final RunningSession runningSession, MatchWindowData matchData) throws EyesException {
        ArgumentGuard.notNull(runningSession, "runningSession");
        ArgumentGuard.notNull(matchData, "model");

        // Serializing model into JSON (we'll treat it as binary later).
        String jsonData;
        try {
            jsonData = jsonMapper.writeValueAsString(matchData);
        } catch (IOException e) {
            throw new EyesException("Failed to serialize model for matchWindow!", e);
        }

        AsyncRequest request = makeEyesRequest(new HttpRequestBuilder() {
            @Override
            public AsyncRequest build() {
                return restClient.target(serverUrl).path(API_PATH).path(runningSession.getId())
                        .queryParam("apiKey", getApiKey())
                        .asyncRequest(MediaType.APPLICATION_JSON);
            }
        });

        List<Integer> validStatusCodes = new ArrayList<>(1);
        validStatusCodes.add(HttpStatus.SC_OK);
        ResponseParsingCallback<MatchResult> callback = new ResponseParsingCallback<>(this, validStatusCodes, listener, new TypeReference<MatchResult>() {});
        sendLongRequest(request, HttpMethod.POST, callback, jsonData, MediaType.APPLICATION_JSON);
    }

    public void render(final TaskListener<List<RunningRender>> listener, RenderRequest... renderRequests) {
        ArgumentGuard.notNull(renderRequests, "renderRequests");
        this.logger.verbose("called with " + Arrays.toString(renderRequests));
        AsyncRequest request = restClient.target(renderingInfo.getServiceUrl()).path(RENDER).asyncRequest(MediaType.APPLICATION_JSON);
        request.header("X-Auth-Token", renderingInfo.getAccessToken());
        List<Integer> validStatusCodes = new ArrayList<>();
        validStatusCodes.add(HttpStatus.SC_OK);
        validStatusCodes.add(HttpStatus.SC_NOT_FOUND);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
            String json = objectMapper.writeValueAsString(renderRequests);
            ResponseParsingCallback<RunningRender[]> callback = new ResponseParsingCallback<>(this, validStatusCodes, new TaskListener<RunningRender[]>() {
                @Override
                public void onComplete(RunningRender[] runningRenders) {
                    listener.onComplete(runningRenders == null ? null : Arrays.asList(runningRenders));
                }

                @Override
                public void onFail() {
                    listener.onFail();
                }
            }, new TypeReference<RunningRender[]>() {});
            sendAsyncRequest(request, HttpMethod.POST, callback, json, MediaType.APPLICATION_JSON);
        } catch (JsonProcessingException e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
            listener.onComplete(null);
        }
    }

    public void renderStatusById(final TaskListener<List<RenderStatusResults>> listener, String... renderIds) {
        try {
            ArgumentGuard.notNull(renderIds, "renderIds");
            this.logger.verbose("called for render: " + Arrays.toString(renderIds));
            AsyncRequest request = makeEyesRequest(new HttpRequestBuilder() {
                @Override
                public AsyncRequest build() {
                    return restClient.target(renderingInfo.getServiceUrl()).path((RENDER_STATUS)).asyncRequest(MediaType.TEXT_PLAIN);
                }
            });
            request.header("X-Auth-Token", renderingInfo.getAccessToken());

            List<Integer> validStatusCodes = new ArrayList<>();
            validStatusCodes.add(HttpStatus.SC_OK);
            validStatusCodes.add(HttpStatus.SC_NOT_FOUND);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
            String json = objectMapper.writeValueAsString(renderIds);

            ResponseParsingCallback<RenderStatusResults[]> callback = new ResponseParsingCallback<>(this, validStatusCodes, new TaskListener<RenderStatusResults[]>() {
                @Override
                public void onComplete(RenderStatusResults[] renderStatusResults) {
                    listener.onComplete(renderStatusResults == null ? null : Arrays.asList(renderStatusResults));
                }

                @Override
                public void onFail() {
                    listener.onFail();
                }
            }, new TypeReference<RenderStatusResults[]>() {});
            sendAsyncRequest(request, HttpMethod.POST, callback, json, MediaType.APPLICATION_JSON);
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
            listener.onComplete(null);
        }
    }

    public void uploadData(AsyncRequestCallback callback, byte[] bytes, RenderingInfo renderingInfo,
                           final String targetUrl, String contentType, final String mediaType) {

        AsyncRequest request = makeEyesRequest(new HttpRequestBuilder() {
            @Override
            public AsyncRequest build() {
                return restClient.target(targetUrl).asyncRequest(mediaType);
            }
        });
        request = request.header("X-Auth-Token", renderingInfo.getAccessToken())
                .header("x-ms-blob-type", "BlockBlob");
        sendAsyncRequest(request, HttpMethod.PUT, callback, bytes, contentType);
    }

    public void postViewportImage(final TaskListener<String> listener, final byte[] bytes) {
        final RenderingInfo renderingInfo = getRenderInfo();
        String targetUrl = renderingInfo.getResultsUrl();

        UUID uuid = UUID.randomUUID();
        final String finalUrl = targetUrl.replace("__random__", uuid.toString());
        logger.verbose("uploading viewport image to " + targetUrl);

        final AtomicInteger attemptNumber = new AtomicInteger(0);
        AsyncRequestCallback callback = new AsyncRequestCallback() {
            @Override
            public void onComplete(Response response) {
                int statusCode = response.getStatusCode();
                String statusPhrase = response.getStatusPhrase();
                response.close();

                if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
                    listener.onComplete(finalUrl);
                    return;
                }

                String errorMessage = String.format("Status: %d %s.",
                        statusCode, statusPhrase);

                if (statusCode < 500) {
                    onFail(new IOException(String.format("Failed uploading image. %s", errorMessage)));
                }

                logger.log(String.format("Failed uploading image, retrying. %s", errorMessage));
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    onFail(e);
                    return;
                }

                if (attemptNumber.incrementAndGet() >= ServerConnector.MAX_CONNECTION_RETRIES) {
                    listener.onComplete(null);
                    return;
                }

                uploadData(this, bytes, renderingInfo, finalUrl, "image/png", "image/png");
            }

            @Override
            public void onFail(Throwable throwable) {
                GeneralUtils.logExceptionStackTrace(logger, throwable);
                listener.onFail();
            }
        };

        uploadData(callback, bytes, renderingInfo, finalUrl, "image/png", "image/png");
    }

    public void postLocators(TaskListener<Map<String, List<Region>>> listener, VisualLocatorsData visualLocatorsData) {
        String postData;
        try {
            jsonMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
            postData = jsonMapper.writeValueAsString(visualLocatorsData);
        } catch (IOException e) {
            throw new EyesException("Failed to convert " +
                    "visualLocatorsData into Json string!", e);
        }

        AsyncRequest request = makeEyesRequest(new HttpRequestBuilder() {
            @Override
            public AsyncRequest build() {
                return restClient.target(serverUrl).path(("api/locators/locate"))
                        .queryParam("apiKey", getApiKey())
                        .asyncRequest(MediaType.APPLICATION_JSON);
            }
        });

        List<Integer> validStatusCodes = new ArrayList<>();
        validStatusCodes.add(HttpStatus.SC_OK);
        ResponseParsingCallback<Map<String, List<Region>>> callback = new ResponseParsingCallback<>(this, validStatusCodes, listener, new TypeReference<Map<String, List<Region>>>() {});
        sendLongRequest(request, HttpMethod.POST, callback, postData, MediaType.APPLICATION_JSON);
    }

    public void closeBatch(String batchId) {
        closeBatch(batchId, false);
    }

    public void closeBatch(String batchId, boolean forceClose) {
        final AtomicReference<Object> lock = new AtomicReference<>(new Object());
        boolean dontCloseBatchesStr = GeneralUtils.getDontCloseBatches();
        if (dontCloseBatchesStr && !forceClose) {
            logger.log("APPLITOOLS_DONT_CLOSE_BATCHES environment variable set to true. Skipping batch close.");
            return;
        }
        closeBatchAsync(new SyncTaskListener<Void>(lock), batchId, forceClose);
        synchronized (lock.get()) {
            try {
                lock.get().wait();
            } catch (InterruptedException e) {
                throw new EyesException("Failed waiting for close batch", e);
            }
        }
    }

    public void closeBatchAsync(final TaskListener<Void> listener, String batchId, boolean forceClose) {
        boolean dontCloseBatchesStr = GeneralUtils.getDontCloseBatches();
        if (dontCloseBatchesStr && !forceClose) {
            logger.log("APPLITOOLS_DONT_CLOSE_BATCHES environment variable set to true. Skipping batch close.");
            listener.onComplete(null);
            return;
        }
        ArgumentGuard.notNull(batchId, "batchId");
        this.logger.log("called with " + batchId);

        final String url = String.format(CLOSE_BATCH, batchId);
        initClient();
        AsyncRequest request = makeEyesRequest(new HttpRequestBuilder() {
            @Override
            public AsyncRequest build() {
                return restClient.target(serverUrl).path(url)
                        .queryParam("apiKey", getApiKey())
                        .asyncRequest((String) null);
            }
        });

        sendAsyncRequest(request, HttpMethod.DELETE, new AsyncRequestCallback() {
            @Override
            public void onComplete(Response response) {
                closeConnector();
                listener.onComplete(null);
            }

            @Override
            public void onFail(Throwable throwable) {
                GeneralUtils.logExceptionStackTrace(logger, throwable);
                closeConnector();
                listener.onFail();
            }
        });
    }

    public void closeConnector() {
        if (restClient != null) {
            restClient.close();
        }
    }

    public boolean getDontCloseBatches() {
        return "true".equalsIgnoreCase(GeneralUtils.getEnvString("APPLITOOLS_DONT_CLOSE_BATCHES"));
    }
}