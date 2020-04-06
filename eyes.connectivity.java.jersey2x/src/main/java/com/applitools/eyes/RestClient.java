package com.applitools.eyes;

import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.RequestEntityProcessing;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.util.*;

/**
 * Provides common rest client functionality.
 */
public class RestClient {

    /**
     * An interface used as base for anonymous classes wrapping Http Method
     * calls.
     */
    protected interface HttpMethodCall {
        Response call();
    }

    protected static final String AGENT_ID_CUSTOM_HEADER = "x-applitools-eyes-client";

    private AbstractProxySettings abstractProxySettings;
    private int timeout; // seconds

    protected Logger logger;
    protected Client restClient;
    protected URI serverUrl;
    protected WebTarget endPoint;
    protected String agentId;

    // Used for JSON serialization/de-serialization.
    protected ObjectMapper jsonMapper;

    /**
     * @param timeout               Connect/Read timeout in milliseconds. 0 equals infinity.
     * @param abstractProxySettings (optional) Setting for communicating via proxy.
     */
    static Client buildRestClient(int timeout,
                                  AbstractProxySettings abstractProxySettings) {
        // Creating the client configuration
        ClientConfig cc = new ClientConfig();
        cc.property(ClientProperties.CONNECT_TIMEOUT, timeout);
        cc.property(ClientProperties.READ_TIMEOUT, timeout);
        if (abstractProxySettings != null) {
            // URI is mandatory.
            cc = cc.property(ClientProperties.PROXY_URI,
                    abstractProxySettings.getUri());
            // username/password are optional
            if (abstractProxySettings.getUsername() != null) {
                cc = cc.property(ClientProperties.PROXY_USERNAME,
                        abstractProxySettings.getUsername());
            }
            if (abstractProxySettings.getPassword() != null) {
                cc = cc.property(ClientProperties.PROXY_PASSWORD,
                        abstractProxySettings.getPassword());
            }
        }

        // This tells the connector NOT to use "chunked encoding" ,
        // since Eyes server does not handle it.
        cc.property(ClientProperties.REQUEST_ENTITY_PROCESSING,
                RequestEntityProcessing.BUFFERED);
        // We must use the Apache connector, since Jersey's default connector
        // does not support proxy settings.
        cc.connectorProvider(new ApacheConnectorProvider());

        return ClientBuilder.newBuilder().withConfig(cc).build();
    }

    /***
     * @param logger    Logger instance.
     * @param serverUrl The URI of the rest server.
     * @param timeout Connect/Read timeout in milliseconds. 0 equals infinity.
     */
    public RestClient(Logger logger, URI serverUrl, int timeout) {
        ArgumentGuard.notNull(serverUrl, "serverUrl");
        ArgumentGuard.greaterThanOrEqualToZero(timeout, "timeout");

        this.logger = logger;
        jsonMapper = new ObjectMapper();
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false);
        this.timeout = timeout;
        this.serverUrl = serverUrl;

        restClient = buildRestClient(timeout, abstractProxySettings);
        endPoint = restClient.target(serverUrl);
    }

    public void setLogger(Logger logger) {
        ArgumentGuard.notNull(logger, "logger");
        this.logger = logger;
    }

    public Logger getLogger() {
        return this.logger;
    }

    /**
     * Creates a rest client instance with timeout default of 5 minutes and
     * no proxy settings.
     * @param logger    A logger instance.
     * @param serverUrl The URI of the rest server.
     */
    public RestClient(Logger logger, URI serverUrl) {
        this(logger, serverUrl, 1000 * 60 * 5);
    }

    /**
     * Sets the proxy settings to be used by the rest client.
     * @param abstractProxySettings The proxy settings to be used by the rest client.
     *                              If {@code null} then no proxy is set.
     */
    @SuppressWarnings("UnusedDeclaration")
    public void setProxyBase(AbstractProxySettings abstractProxySettings) {
        this.abstractProxySettings = abstractProxySettings;

        restClient = buildRestClient(timeout, abstractProxySettings);
        endPoint = restClient.target(serverUrl);
    }

    /**
     * @return The current proxy settings used by the rest client,
     * or {@code null} if no proxy is set.
     */
    @SuppressWarnings("UnusedDeclaration")
    public AbstractProxySettings getProxyBase() {
        return abstractProxySettings;
    }

    /**
     * Sets the connect and read timeouts for web requests.
     * @param timeout Connect/Read timeout in milliseconds. 0 equals infinity.
     */
    public void setTimeout(int timeout) {
        ArgumentGuard.greaterThanOrEqualToZero(timeout, "timeout");
        this.timeout = timeout;

        restClient = buildRestClient(timeout, abstractProxySettings);
        endPoint = restClient.target(serverUrl);
    }

    /**
     * @return The timeout for web requests (in seconds).
     */
    public int getTimeout() {
        return timeout;
    }


    /**
     * Sets the current server URL used by the rest client.
     * @param serverUrl The URI of the rest server.
     */
    @SuppressWarnings("UnusedDeclaration")
    protected void setServerUrlBase(URI serverUrl) {
        ArgumentGuard.notNull(serverUrl, "serverUrl");
        this.serverUrl = serverUrl;

        endPoint = restClient.target(serverUrl);
    }

    /**
     * @return The URI of the eyes server.
     */
    protected URI getServerUrlBase() {
        return serverUrl;
    }

    /**
     * Creates a request for the eyes server
     * @param target The target to start building from
     * @param queryParams Query parameters for the URI
     * @param responseTypes The accepted response type
     * @return The created request
     */
    protected Invocation.Builder makeEyesRequest(WebTarget target, Map<String, Object> queryParams,  String... responseTypes) {
        if (queryParams == null) {
            queryParams = Collections.emptyMap();
        }

        for (Map.Entry<String, Object> param : queryParams.entrySet()) {
            target = target.queryParam(param.getKey(), param.getValue());
        }

        Invocation.Builder request = target.request(responseTypes);
        return request.header(AGENT_ID_CUSTOM_HEADER, agentId);
    }

    protected Invocation.Builder makeEyesRequest(WebTarget target, Map<String, Object> queryParams) {
        return makeEyesRequest(target, queryParams, MediaType.APPLICATION_JSON);
    }

    protected Invocation.Builder makeEyesRequest(WebTarget target) {
        return makeEyesRequest(target, null);
    }

    protected Response sendLongRequest(Invocation.Builder invocationBuilder, String method, Entity<?> entity)
            throws EyesException {

        logger.verbose("enter");
        String currentTime = GeneralUtils.toRfc1123(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
        invocationBuilder = invocationBuilder
                .header("Eyes-Expect", "202+location")
                .header("Eyes-Date", currentTime)
                .header(AGENT_ID_CUSTOM_HEADER, agentId);
        Response response = invocationBuilder.method(method, entity);

        String statusUrl = response.getHeaderString(HttpHeaders.LOCATION);
        int status = response.getStatus();
        if (statusUrl != null && status == HttpStatus.SC_ACCEPTED) {
            response.close();

            int wait = 500;
            while (true) {
                response = get(statusUrl);
                if (response.getStatus() == HttpStatus.SC_CREATED) {
                    logger.verbose("exit (CREATED)");
                    return delete(response.getHeaderString(HttpHeaders.LOCATION));
                }

                status = response.getStatus();
                if (status == HttpStatus.SC_OK) {
                    try {
                        Thread.sleep(wait);
                    } catch (InterruptedException e) {
                        throw new EyesException("Long request interrupted!", e);
                    }
                    wait *= 2;
                    wait = Math.min(10000, wait);
                    response.close();
                    logger.verbose("polling...");
                    continue;
                }

                // Something went wrong.
                logger.verbose("exit (inside loop) (" + status + ")");
                return response;
            }
        }
        logger.verbose("exit (" + status + ")");
        return response;
    }

    public String getString(String path, String accept) {
        Response response = sendHttpWebRequest(path, HttpMethod.GET, accept);
        return response.readEntity(String.class);
    }

    private Response get(String path, String accept) {
        return sendHttpWebRequest(path, HttpMethod.GET, accept);
    }

    private Response get(String path) {
        return get(path, null);
    }

    private Response delete(String path, String accept) {
        return sendHttpWebRequest(path, HttpMethod.DELETE, accept);
    }

    private Response delete(String path) {
        return delete(path, null);
    }

    protected Response sendHttpWebRequest(String path, final String method, String accept) {
        // Building the request
        Invocation.Builder invocationBuilder = restClient.target(path).request(accept);
        invocationBuilder.header(AGENT_ID_CUSTOM_HEADER, agentId);

        // Actually perform the method call and return the result
        return invocationBuilder.method(method);
    }

    /**
     * Builds an error message which includes the response model.
     * @param errMsg       The error message.
     * @param statusCode   The response status code.
     * @param statusPhrase The response status phrase.
     * @param responseBody The response body.
     * @return An error message which includes the response model.
     */
    protected String getReadResponseError(
            String errMsg, int statusCode, String statusPhrase,
            String responseBody) {
        ArgumentGuard.notNull(statusPhrase, "statusPhrase");

        if (errMsg == null) {
            errMsg = "";
        }

        if (responseBody == null) {
            responseBody = "";
        }

        return errMsg + " [" + statusCode + " " + statusPhrase + "] " + responseBody;
    }

    /**
     * Generic handling of response with model. Response Handling includes the
     * following:
     * 1. Verify that we are able to read response model.
     * 2. verify that the status code is valid
     * 3. Parse the response model from JSON to the relevant type.
     * @param response             The response to parse.
     * @param validHttpStatusCodes The list of acceptable status codes.
     * @param resultType           The class object of the type of result this response
     *                             should be parsed to.
     * @param <T>                  The return value type.
     * @return The parse response of the type given in {@code resultType}.
     * @throws EyesException For invalid status codes or if the response
     *                       parsing failed.
     */
    protected <T> T parseResponseWithJsonData(Response response,
                                              List<Integer> validHttpStatusCodes, Class<T> resultType)
            throws EyesException {
        ArgumentGuard.notNull(response, "response");
        ArgumentGuard.notNull(validHttpStatusCodes, "validHttpStatusCodes");
        ArgumentGuard.notNull(resultType, "resultType");

        T resultObject;
        int statusCode = response.getStatus();
        String statusPhrase =
                response.getStatusInfo().getReasonPhrase();
        String data = response.readEntity(String.class);
        response.close();
        // Validate the status code.
        if (!validHttpStatusCodes.contains(statusCode)) {
            String errorMessage = getReadResponseError(
                    "Invalid status code",
                    statusCode,
                    statusPhrase,
                    data);
            if (statusCode == 401 || statusCode == 403) {
                errorMessage += "\nThis is most likely due to an invalid API key.";
            }
            throw new EyesException(errorMessage);
        }

        // Parse model.
        try {
            resultObject = jsonMapper.readValue(data, resultType);
        } catch (IOException e) {
            String errorMessage = getReadResponseError(
                    "Failed to de-serialize response body",
                    statusCode,
                    statusPhrase,
                    data);

            throw new EyesException(errorMessage, e);
        }

        return resultObject;
    }
}
