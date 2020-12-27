package com.applitools.connectivity;

import com.applitools.connectivity.api.ConnectivityTarget;
import com.applitools.connectivity.api.Request;
import com.applitools.connectivity.api.Response;
import com.applitools.eyes.AbstractProxySettings;
import com.applitools.eyes.Logger;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.events.ValidationInfo;
import com.applitools.eyes.events.ValidationResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;
import java.net.URI;

public class RemoteSessionEventHandler extends RestClient {

    public static final int DEFAULT_CLIENT_TIMEOUT = 1000 * 60 * 5; // 5 minutes
    private static final String SERVER_SUFFIX = "/applitools/sessions";

    private String autSessionId;
    private final String accessKey;
    private ConnectivityTarget defaultEndPoint;
    private boolean throwExceptions = true;

    public RemoteSessionEventHandler(Logger logger, URI serverUrl, String accessKey, int timeout) {
        super(logger, serverUrl, timeout);
        this.accessKey = accessKey;
        updateEndpoint();
    }

    public RemoteSessionEventHandler(Logger logger, URI serverUrl, String accessKey) {
        this(logger, serverUrl, accessKey, DEFAULT_CLIENT_TIMEOUT);
    }

    public RemoteSessionEventHandler(URI serverUrl, String accessKey) {
        this(new Logger(), serverUrl, accessKey);
    }

    private void updateEndpoint() {
        this.defaultEndPoint = restClient.target(serverUrl).queryParam("accessKey", accessKey).path(SERVER_SUFFIX);
    }

    public void setProxy(AbstractProxySettings abstractProxySettings) {
        super.setProxy(abstractProxySettings);
        updateEndpoint();
    }

    public void setTimeout(int timeout) {
        super.setTimeout(timeout);
        updateEndpoint();
    }

    private void sendMessage(HttpMethodCall method) {
        Response response = null;
        try {
            response = method.call();
        } catch (RuntimeException e) {
            if (this.throwExceptions) {
                throw e;
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public void initStarted() {
        sendMessage(new HttpMethodCall() {
            public Response call() {
                Request request = defaultEndPoint.path(autSessionId).request(MediaType.APPLICATION_JSON);
                return request.method(HttpMethod.PUT, "{\"action\": \"initStart\"}", MediaType.APPLICATION_JSON);
            }
        });
    }

    public void initEnded() {
        sendMessage(new HttpMethodCall() {
            public Response call() {
                Request request = defaultEndPoint.path(autSessionId).request(MediaType.APPLICATION_JSON);
                return request.method(HttpMethod.PUT, "{\"action\": \"initEnd\"}", MediaType.APPLICATION_JSON);
            }
        });
    }

    public void setSizeWillStart(RectangleSize sizeToSet) {
        final RectangleSize size = sizeToSet;
        sendMessage(new HttpMethodCall() {
            public Response call() {
                String data = "{\"action\": \"setSizeStart\", \"size\":{\"width\": " + size.getWidth() + ", \"height\": " + size.getHeight() + "}}";
                Request request = defaultEndPoint.path(autSessionId).request(MediaType.APPLICATION_JSON);
                return request.method(HttpMethod.PUT, data, MediaType.APPLICATION_JSON);
            }
        });
    }

    public void setSizeEnded() {
        sendMessage(new HttpMethodCall() {
            public Response call() {
                Request request = defaultEndPoint.path(autSessionId).request(MediaType.APPLICATION_JSON);
                return request.method(HttpMethod.PUT, "{\"action\": \"setSizeEnd\"}", MediaType.APPLICATION_JSON);
            }
        });
    }

    public void testStarted(String autSessionId) {
        final String autSessionIdFinal = autSessionId;
        sendMessage(new HttpMethodCall() {
            public Response call() {
                Request request = defaultEndPoint.request(MediaType.APPLICATION_JSON);
                String data = "{\"autSessionId\": \"" + autSessionIdFinal + "\"}";
                return request.method(HttpMethod.POST, data, MediaType.APPLICATION_JSON);
            }
        });
        this.autSessionId = autSessionId;
    }

    public void testEnded(String autSessionId, final TestResults testResults) {
        final String autSessionIdFinal = autSessionId;
        sendMessage(new HttpMethodCall() {
            public Response call() {
                Request invocationBuilder = defaultEndPoint.path(autSessionIdFinal).request(MediaType.APPLICATION_JSON);
                // since the web API requires a root property for this message
                ObjectMapper jsonMapper = new ObjectMapper();
                String testResultJson;
                try {
                    testResultJson = jsonMapper.writeValueAsString(testResults);
                } catch (JsonProcessingException e) {
                    testResultJson = "{}";
                    e.printStackTrace();
                }

                String data = "{\"action\": \"testEnd\", \"testResults\":" + testResultJson + "}";
                return invocationBuilder.method(HttpMethod.PUT, data, MediaType.APPLICATION_JSON);
            }
        });
    }

    public void validationWillStart(String autSessionId, final ValidationInfo validationInfo) {
        final String autSessionIdFinal = autSessionId;
        sendMessage(new HttpMethodCall() {
            public Response call() {
                Request request = defaultEndPoint.path(autSessionIdFinal).path("validations")
                        .request(MediaType.APPLICATION_JSON);

                return request.method(HttpMethod.POST, validationInfo.toJsonString(), MediaType.APPLICATION_JSON);
            }
        });
    }

    public void validationEnded(String autSessionId, final String validationId, final ValidationResult validationResult) {
        final String autSessionIdFinal = autSessionId;
        sendMessage(new HttpMethodCall() {
            public Response call() {
                Request request = defaultEndPoint
                        .path(autSessionIdFinal).path("validations").path(validationId)
                        .request(MediaType.APPLICATION_JSON);

                String data = "{\"action\":\"validationEnd\", \"asExpected\":" + validationResult.isAsExpected() + "}";
                return request.method(HttpMethod.PUT, data, MediaType.APPLICATION_JSON);
            }
        });
    }

    public boolean getThrowExceptions() {
        return throwExceptions;
    }

    public void setThrowExceptions(boolean throwExceptions) {
        this.throwExceptions = throwExceptions;
    }
}
