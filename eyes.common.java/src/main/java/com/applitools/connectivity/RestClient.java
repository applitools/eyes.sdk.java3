package com.applitools.connectivity;

import com.applitools.connectivity.api.HttpClient;
import com.applitools.connectivity.api.Request;
import com.applitools.connectivity.api.Response;
import com.applitools.connectivity.api.Target;
import com.applitools.eyes.AbstractProxySettings;
import com.applitools.eyes.EyesException;
import com.applitools.eyes.Logger;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URI;
import java.util.*;

public class RestClient {

    /**
     * An interface used as base for anonymous classes wrapping Http Method
     * calls.
     */
    protected interface HttpMethodCall {
        Response call();
    }

    public static final int DEFAULT_CLIENT_TIMEOUT = 1000 * 60 * 5; // 5 minutes
    private static final String AGENT_ID_CUSTOM_HEADER = "x-applitools-eyes-client";

    protected Logger logger;
    protected HttpClient restClient;
    protected URI serverUrl;
    protected Target endPoint;
    protected String agentId;

    // Used for JSON serialization/de-serialization.
    protected ObjectMapper jsonMapper;

    /***
     * @param restClient The client for communication
     * @param logger    Logger instance.
     * @param serverUrl The URI of the rest server.
     */
    public RestClient(HttpClient restClient, Logger logger, URI serverUrl) {
        ArgumentGuard.notNull(restClient, "restClient");
        ArgumentGuard.notNull(serverUrl, "serverUrl");

        this.logger = logger;
        jsonMapper = new ObjectMapper();
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        this.serverUrl = serverUrl;
        this.restClient = restClient;
        endPoint = restClient.target(serverUrl);
    }

    public void setLogger(Logger logger) {
        ArgumentGuard.notNull(logger, "logger");
        this.logger = logger;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public AbstractProxySettings getProxySettings() {
        return restClient.getProxySettings();
    }

    public int getTimeout() {
        return restClient.getTimeout();
    }

    protected void setServerUrlBase(URI serverUrl) {
        ArgumentGuard.notNull(serverUrl, "serverUrl");
        this.serverUrl = serverUrl;
        endPoint = restClient.target(serverUrl);
    }

    protected URI getServerUrlBase() {
        return serverUrl;
    }

    public void updateClient(HttpClient client) {
        ArgumentGuard.notNull(client, "client");
        restClient = client;
        endPoint = restClient.target(serverUrl);
    }

    /**
     * Sending HTTP request to a specific url
     * @param url The target url
     * @param method The HTTP method
     * @param accept Accepted response content types
     * @return The response from the server
     */
    protected Response sendHttpWebRequest(String url, final String method, String... accept) {
        Request request = makeEyesRequest(restClient.target(url), null, accept);
        return request.method(method, null, null);
    }

    /**
     * Creates a request for the eyes server
     * @param target The target to start building from
     * @param queryParams Query parameters for the URI
     * @param responseTypes The accepted response type
     * @return The created request
     */
    protected Request makeEyesRequest(Target target, Map<String, Object> queryParams, String... responseTypes) {
        if (queryParams == null) {
            queryParams = Collections.emptyMap();
        }

        for (Map.Entry<String, Object> param : queryParams.entrySet()) {
            target = target.queryParam(param.getKey(), (String) param.getValue());
        }

        Request request = target.request(responseTypes);
        return request.header(AGENT_ID_CUSTOM_HEADER, agentId);
    }

    protected Request makeEyesRequest(Target target, Map<String, Object> queryParams) {
        return makeEyesRequest(target, queryParams, MediaType.APPLICATION_JSON);
    }

    protected Request makeEyesRequest(Target target) {
        return makeEyesRequest(target, null);
    }

    protected Response sendLongRequest(Request invocationBuilder, String method, String data, String mediaType) throws EyesException {
        String currentTime = GeneralUtils.toRfc1123(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
        invocationBuilder = invocationBuilder
                .header("Eyes-Expect", "202+location")
                .header("Eyes-Date", currentTime);
        Response response = invocationBuilder.method(method, data, mediaType);
        String statusUrl = response.getHeader(HttpHeaders.LOCATION);
        int status = response.getStatusCode();
        if (statusUrl != null && status == HttpStatus.SC_ACCEPTED) {
            response.close();

            int wait = 500;
            while (true) {
                response = sendHttpWebRequest(statusUrl, HttpMethod.GET);
                if (response.getStatusCode() == HttpStatus.SC_CREATED) {
                    logger.verbose("exit (CREATED)");
                    return sendHttpWebRequest(response.getHeader(HttpHeaders.LOCATION), HttpMethod.DELETE);
                }

                status = response.getStatusCode();
                if (status != HttpStatus.SC_OK) {
                    // Something went wrong.
                    logger.verbose("exit (inside loop) (" + status + ")");
                    return response;
                }

                try {
                    Thread.sleep(wait);
                } catch (InterruptedException e) {
                    throw new EyesException("Long request interrupted!", e);
                }
                wait *= 2;
                wait = Math.min(10000, wait);
                response.close();
                logger.verbose("polling...");
            }
        }
        logger.verbose("exit (" + status + ")");
        return response;
    }

    /**
     * Builds an error message which includes the response model.
     * @param errMsg       The error message.
     * @param statusCode   The response status code.
     * @param statusPhrase The response status phrase.
     * @param responseBody The response body.
     * @return An error message which includes the response model.
     */
    protected String getReadResponseError(String errMsg, int statusCode, String statusPhrase, String responseBody) {
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
     *
     * @param response The response to parse.
     * @param validHttpStatusCodes The list of acceptable status codes.
     * @param resultType The class object of the type of result this response
     *                   should be parsed to.
     * @param <T> The return value type.
     * @return The parse response of the type given in {@code resultType}.
     * @throws EyesException For invalid status codes or if the response
     * parsing failed.
     */
    protected <T> T parseResponseWithJsonData(Response response, List<Integer> validHttpStatusCodes, Class<T> resultType)
            throws EyesException {
        ArgumentGuard.notNull(response, "response");
        ArgumentGuard.notNull(validHttpStatusCodes, "validHttpStatusCodes");
        ArgumentGuard.notNull(resultType, "resultType");

        T resultObject;
        int statusCode = response.getStatusCode();
        String statusPhrase = response.getStatusPhrase();
        String data = response.readEntity(String.class);
        response.close();

        // Validate the status code.
        if (!validHttpStatusCodes.contains(statusCode)) {
            String errorMessage = getReadResponseError("Invalid status code", statusCode, statusPhrase, data);
            if (statusCode == 401 || statusCode == 403) {
                errorMessage += "\nThis is most likely due to an invalid API key.";
            }
            throw new EyesException(errorMessage);
        }

        // Parse model.
        try {
            resultObject = jsonMapper.readValue(data, resultType);
        } catch (IOException e) {
            String errorMessage = getReadResponseError("Failed deserialize response body",
                    statusCode, statusPhrase, data);
            throw new EyesException(errorMessage, e);
        }

        return resultObject;
    }
}
