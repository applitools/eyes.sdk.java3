package com.applitools.eyes;

import com.applitools.eyes.events.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

public class RemoteSessionEventHandler extends RestClient implements ISessionEventHandler {

    private String autSessionId;

    private static final String SERVER_SUFFIX = "/applitools/sessions";
    private final String accessKey;
    private WebTarget defaultEndPoint;
    private boolean throwExceptions = true;

    @SuppressWarnings("WeakerAccess")
    public RemoteSessionEventHandler(Logger logger, URI serverUrl, String accessKey, int timeout) {
        super(logger, serverUrl, timeout);
        this.accessKey = accessKey;
        this.defaultEndPoint = endPoint.queryParam("accessKey", accessKey).path(SERVER_SUFFIX);
    }

    public RemoteSessionEventHandler(Logger logger, URI serverUrl, String accessKey) {
        this(logger, serverUrl, accessKey, 30 * 1000);
    }

    public RemoteSessionEventHandler(URI serverUrl, String accessKey, int timeout) {
        this(new Logger(), serverUrl, accessKey, timeout);
    }

    public RemoteSessionEventHandler(URI serverUrl, String accessKey) {
        this(new Logger(), serverUrl, accessKey);
    }

    public void setProxy(AbstractProxySettings abstractProxySettings){
        setProxyBase(abstractProxySettings);
        this.defaultEndPoint = endPoint.queryParam("accessKey", accessKey).path(SERVER_SUFFIX);
    }

    private void sendMessage(HttpMethodCall method) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String methodName = "";
        // getStackTrace()<-sendMessage()<-"actual caller"
        if (stackTraceElements.length >= 3) {
            methodName = stackTraceElements[2].getMethodName();
        }

        Response response = null;
        try {
            response = method.call();
            if (response.getStatus() != 200) {
                logger.verbose("'" + methodName + "' notification handler returned an error: " + response.getStatusInfo());
            } else {
                logger.verbose("'" + methodName + "' succeeded: " + response);
            }
        } catch (RuntimeException e) {
            logger.log("'" + methodName + "' Server request failed: " + e.getMessage());
            if (this.throwExceptions) {
                throw e;
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    @Override
    public void initStarted() {
        sendMessage(new HttpMethodCall() {
            public Response call() {
                Invocation.Builder invocationBuilder = defaultEndPoint.path(autSessionId)
                        .request(MediaType.APPLICATION_JSON);

                return invocationBuilder.put(Entity.json("{\"action\": \"initStart\"}"));
            }
        });
    }

    @Override
    public void initEnded() {
        sendMessage(new HttpMethodCall() {
            public Response call() {
                Invocation.Builder invocationBuilder = defaultEndPoint.path(autSessionId)
                        .request(MediaType.APPLICATION_JSON);

                return invocationBuilder.put(Entity.json("{\"action\": \"initEnd\"}"));
            }
        });
    }

    @Override
    public void setSizeWillStart(RectangleSize sizeToSet) {
        final RectangleSize size = sizeToSet;
        sendMessage(new HttpMethodCall() {
            public Response call() {
                Invocation.Builder invocationBuilder = defaultEndPoint.path(autSessionId)
                        .request(MediaType.APPLICATION_JSON);

                return invocationBuilder.put(Entity.json("{\"action\": \"setSizeStart\", \"size\":{\"width\": " + size.getWidth() + ", \"height\": " + size.getHeight() + "}}"));
            }
        });
    }

    @Override
    public void setSizeEnded() {
        sendMessage(new HttpMethodCall() {
            public Response call() {
                Invocation.Builder invocationBuilder = defaultEndPoint.path(autSessionId)
                        .request(MediaType.APPLICATION_JSON);

                return invocationBuilder.put(Entity.json("{\"action\": \"setSizeEnd\"}"));
            }
        });
    }

    @Override
    public void testStarted(String autSessionId) {
        final String autSessionIdFinal = autSessionId;
        sendMessage(new HttpMethodCall() {
            public Response call() {
                Invocation.Builder invocationBuilder = defaultEndPoint
                        .request(MediaType.APPLICATION_JSON);

                return invocationBuilder.post(Entity.json("{\"autSessionId\": \"" + autSessionIdFinal + "\"}"));
            }
        });
        this.autSessionId = autSessionId;
    }

    @Override
    public void testEnded(String autSessionId, final TestResults testResults) {
        final String autSessionIdFinal = autSessionId;
        sendMessage(new HttpMethodCall() {
            public Response call() {
                Invocation.Builder invocationBuilder = defaultEndPoint.path(autSessionIdFinal)
                        .request(MediaType.APPLICATION_JSON);
                // since the web API requires a root property for this message
                ObjectMapper jsonMapper = new ObjectMapper();
                String testResultJson;
                try {
                    testResultJson = jsonMapper.writeValueAsString(testResults);
                } catch (JsonProcessingException e) {
                    testResultJson = "{}";
                    e.printStackTrace();
                }

                return invocationBuilder.put(Entity.json("{\"action\": \"testEnd\", \"testResults\":" + testResultJson + "}"));
            }
        });
    }

    @Override
    public void validationWillStart(String autSessionId, final ValidationInfo validationInfo) {
        final String autSessionIdFinal = autSessionId;
        sendMessage(new HttpMethodCall() {
            public Response call() {
                Invocation.Builder invocationBuilder = defaultEndPoint.path(autSessionIdFinal).path("validations")
                        .request(MediaType.APPLICATION_JSON);

                return invocationBuilder.post(Entity.json(validationInfo.toJsonString()));
            }
        });
    }

    @Override
    public void validationEnded(String autSessionId, final String validationId, final ValidationResult validationResult) {
        final String autSessionIdFinal = autSessionId;
        sendMessage(new HttpMethodCall() {
            public Response call() {
                Invocation.Builder invocationBuilder = defaultEndPoint
                        .path(autSessionIdFinal).path("validations").path(validationId)
                        .request(MediaType.APPLICATION_JSON);

                return invocationBuilder.put(Entity.json("{\"action\":\"validationEnd\", \"asExpected\":" + validationResult.isAsExpected() + "}"));
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
