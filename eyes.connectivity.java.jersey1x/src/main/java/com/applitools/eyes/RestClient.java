package com.applitools.eyes;

import com.applitools.utils.ArgumentGuard;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;

import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * Provides common rest client functionality.
 */
public class RestClient {

    /**
     * An interface used as base for anonymous classes wrapping Http Method
     * calls.
     */
    protected interface HttpMethodCall {
        ClientResponse call();
    }

    public static final int DEFAULT_APPLITOOLS_TIMEOUT = 1000 * 60 * 5; // ms

    private ProxySettings proxySettings;
    private int timeout; // seconds

    protected final Logger logger;
    protected Client restClient;
    protected URI serverUrl;
    protected WebResource endPoint;

    // Used for JSON serialization/de-serialization.
    protected ObjectMapper jsonMapper;

    /**
     *
     * @param timeout Connect/Read timeout in milliseconds. 0 equals infinity.
     * @param proxySettings (optional) Setting for communicating via proxy.
     */
    public static Client buildRestClient(int timeout,
                                          ProxySettings proxySettings) {

        URLConnectionClientHandler ch  = new URLConnectionClientHandler(new ConnectionFactory(proxySettings));

        // Creating the client configuration
        ClientConfig cc = new DefaultClientConfig();
        cc.getProperties().put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, timeout);
        cc.getProperties().put(ClientConfig.PROPERTY_READ_TIMEOUT, timeout);

        // We ignore the proxy settings

        return new Client(ch, cc);
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

        restClient = buildRestClient(timeout, proxySettings);
        endPoint = restClient.resource(serverUrl);
    }

    /**
     * Creates a rest client instance with timeout default of 5 minutes and
     * no proxy settings.
     * @param logger    A logger instance.
     * @param serverUrl The URI of the rest server.
     */
    public RestClient(Logger logger, URI serverUrl) {
        this(logger, serverUrl, DEFAULT_APPLITOOLS_TIMEOUT);
    }


    /**
     * Sets the proxy settings to be used by the rest client.
     * @param proxySettings The proxy settings to be used by the rest client.
     * If {@code null} then no proxy is set.
     */
    @SuppressWarnings("UnusedDeclaration")
    public void setProxyBase(ProxySettings proxySettings) {
        this.proxySettings = proxySettings;

        restClient = buildRestClient(timeout, proxySettings);
        endPoint = restClient.resource(serverUrl);
    }

    /**
     *
     * @return The current proxy settings used by the rest client,
     * or {@code null} if no proxy is set.
     */
    @SuppressWarnings("UnusedDeclaration")
    public ProxySettings getProxyBase() {
        return proxySettings;
    }

    /**
     * Sets the connect and read timeouts for web requests.
     *
     * @param timeout Connect/Read timeout in milliseconds. 0 equals infinity.
     */
    public void setTimeout(int timeout) {
        ArgumentGuard.greaterThanOrEqualToZero(timeout, "timeout");
        this.timeout = timeout;

        restClient = buildRestClient(timeout, proxySettings);
        endPoint = restClient.resource(serverUrl);
    }

    /**
     *
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

        endPoint = restClient.resource(serverUrl);
    }

    /**
     *
     * @return The URI of the eyes server.
     */
    protected URI getServerUrlBase() {
        return serverUrl;
    }

    protected ClientResponse sendLongRequest(HttpMethodCall method, String name)
            throws EyesException {

        // Adding the long request headers
        int maxDelay = 10000;
        int delay = 2000;  // milliseconds
        ClientResponse response;
        while (true) {
            response = method.call();
            if (response.getStatus() != 202) {
                return response;
            }

            // Since we haven't read the entity, We must release the response
            // or the connection stays open (meaning it'll get stuck after two
            // requests).
            response.close();

            // Waiting a delay
            logger.verbose(String.format(
                    "%s: Still running... Retrying in %d ms", name, delay));
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                throw new EyesException("Long request interrupted!", e);
            }

            // increasing the delay
            delay = Math.min(maxDelay, (int) Math.floor(delay * 1.5));
        }
    }


    /**
     * Builds an error message which includes the response data.
     *
     * @param errMsg The error message.
     * @param statusCode The response status code.
     * @param statusPhrase The response status phrase.
     * @param responseBody The response body.
     * @return An error message which includes the response data.
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

        return errMsg + " [" + statusCode + " " + statusPhrase + "] "
                + responseBody;
    }

    /**
     * Generic handling of response with data. Response Handling includes the
     * following:
     * 1. Verify that we are able to read response data.
     * 2. verify that the status code is valid
     * 3. Parse the response data from JSON to the relevant type.
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
    protected <T> T parseResponseWithJsonData(ClientResponse response,
        List<Integer> validHttpStatusCodes, Class<T> resultType)
            throws EyesException {
        ArgumentGuard.notNull(response, "response");
        ArgumentGuard.notNull(validHttpStatusCodes, "validHttpStatusCodes");
        ArgumentGuard.notNull(resultType, "resultType");

        T resultObject;
        int statusCode = response.getStatus();
        String statusPhrase =
                ClientResponse.Status.fromStatusCode(response.getStatus()).getReasonPhrase();
        String data = response.getEntity(String.class);
        response.close();
        // Validate the status code.
        if (!validHttpStatusCodes.contains(statusCode)) {
            String errorMessage = getReadResponseError(
                    "Invalid status code",
                    statusCode,
                    statusPhrase,
                    data);

            throw new EyesException(errorMessage);
        }

        // Parse data.
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