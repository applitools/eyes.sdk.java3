package com.applitools.connectivity;

import com.applitools.connectivity.api.HttpClient;
import com.applitools.connectivity.api.HttpClientImpl;
import com.applitools.connectivity.api.Request;
import com.applitools.connectivity.api.Response;
import com.applitools.eyes.AbstractProxySettings;
import com.applitools.eyes.EyesException;
import com.applitools.eyes.Logger;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;

import javax.ws.rs.HttpMethod;
import java.io.IOException;
import java.net.URI;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class RestClient {

    /**
     * An interface used as base for anonymous classes wrapping Http Method
     * calls.
     */
    protected interface HttpMethodCall {
        Response call();
    }

    protected interface HttpRequestBuilder {
        Request build();
    }

    private static final String AGENT_ID_CUSTOM_HEADER = "x-applitools-eyes-client";

    protected Logger logger;
    protected HttpClient restClient;
    protected URI serverUrl;
    protected String agentId;

    // Used for JSON serialization/de-serialization.
    protected ObjectMapper jsonMapper;

    /***
     * @param logger    Logger instance.
     * @param serverUrl The URI of the rest server.
     */
    public RestClient(Logger logger, URI serverUrl, int timeout) {
        ArgumentGuard.notNull(serverUrl, "serverUrl");

        this.logger = logger;
        jsonMapper = new ObjectMapper();
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        this.serverUrl = serverUrl;
        this.restClient = new HttpClientImpl(logger, timeout, null);
    }

    public void setLogger(Logger logger) {
        ArgumentGuard.notNull(logger, "logger");
        this.logger = logger;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public void setProxy(AbstractProxySettings proxySettings) {
        int timeout = restClient.getTimeout();
        restClient.close();
        restClient = new HttpClientImpl(logger, timeout, proxySettings);
    }

    public AbstractProxySettings getProxy() {
        return restClient.getProxySettings();
    }

    public void setTimeout(int timeout) {
        ArgumentGuard.greaterThanOrEqualToZero(timeout, "timeout");
        AbstractProxySettings proxySettings = restClient.getProxySettings();
        restClient.close();
        restClient = new HttpClientImpl(logger, timeout, proxySettings);
    }

    public int getTimeout() {
        return restClient.getTimeout();
    }

    protected void setServerUrlBase(URI serverUrl) {
        ArgumentGuard.notNull(serverUrl, "serverUrl");
        this.serverUrl = serverUrl;
    }

    protected URI getServerUrlBase() {
        return serverUrl;
    }

    protected void initClient() {
        if (restClient.isClosed()) {
            restClient = new HttpClientImpl(logger, getTimeout(), getProxy());
        }
    }

    /**
     * Sending HTTP request to a specific url
     * @param url The target url
     * @param method The HTTP method
     * @param accept Accepted response content types
     * @return The response from the server
     */
    public Response sendHttpWebRequest(final String url, final String method, final String... accept) {
        Request request = makeEyesRequest(new HttpRequestBuilder() {
            @Override
            public Request build() {
                return restClient.target(url).request(accept);
            }
        });

        String currentTime = GeneralUtils.toRfc1123(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
        return request.header("Eyes-Date", currentTime).method(method, null, null);
    }

    /**
     * Creates a request for the eyes server
     */
    protected Request makeEyesRequest(HttpRequestBuilder builder) {
        Request request = builder.build();
        if (agentId == null) {
            logger.log("Sending a request without agent id");
            return request;
        }

        return request.header(AGENT_ID_CUSTOM_HEADER, agentId);
    }

    protected Response sendLongRequest(Request request, String method, String data, String mediaType) throws EyesException {
        String currentTime = GeneralUtils.toRfc1123(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
        request = request
                .header("Eyes-Expect", "202+location")
                .header("Eyes-Date", currentTime);

        byte[] dataBytes = data == null ? null : data.getBytes();
        Response response = request.method(method, dataBytes, mediaType);
        String statusUrl = response.getHeader(HttpHeaders.LOCATION, false);
        int status = response.getStatusCode();
        if (statusUrl != null && status == HttpStatus.SC_ACCEPTED) {
            response.close();

            int wait = 500;
            while (true) {
                response = sendHttpWebRequest(statusUrl, HttpMethod.GET);
                if (response.getStatusCode() == HttpStatus.SC_CREATED) {
                    logger.verbose("exit (CREATED)");
                    return sendHttpWebRequest(response.getHeader(HttpHeaders.LOCATION, false), HttpMethod.DELETE);
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
    protected <T> T parseResponseWithJsonData(Response response, List<Integer> validHttpStatusCodes, TypeReference<T> resultType)
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
