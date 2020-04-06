package com.applitools.eyes;

import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Provides common rest client functionality.
 */
public class RestClient {

    private static final int DEFAULT_HTTP_PROXY_PORT = 80;
    private static final int DEFAULT_HTTPS_PROXY_PORT = 443;

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
     *
     * @param timeout Connect/Read timeout in milliseconds. 0 equals infinity.
     * @param abstractProxySettings (optional) Setting for communicating via proxy.
     */
    static Client buildRestClient(int timeout,
                                      AbstractProxySettings abstractProxySettings) {
        ResteasyClientBuilder builder = new ResteasyClientBuilder();

        builder = builder.establishConnectionTimeout(timeout, TimeUnit.MILLISECONDS)
                .socketTimeout(timeout, TimeUnit.MILLISECONDS);

        if (abstractProxySettings == null) {
            return builder.build();
        }

        // Setting the proxy configuration
        String uri = abstractProxySettings.getUri();
        String[] uriParts = uri.split(":",3);

        // There must be at least http':'//...
        if (uriParts.length < 2) {
            throw new EyesException("Invalid proxy URI: " + uri);
        }
        String scheme = uriParts[0];
        String hostName = uriParts[1].substring(2); // remove "//" part of the hostname.;

        int port = scheme.equalsIgnoreCase("https") ? DEFAULT_HTTPS_PROXY_PORT : DEFAULT_HTTP_PROXY_PORT;

        // If a port is specified
        if (uriParts.length > 2) {
            String leftOverUri = uriParts[2];
            String[] leftOverParts = leftOverUri.split("/", 2);

            port = Integer.valueOf(leftOverParts[0]);

            // If there's a "path" part following the port
            if (leftOverParts.length == 2) {
                hostName += "/" + leftOverParts[1];
            }
        }

        ResteasyClient client = builder.defaultProxy(hostName, port, scheme).build();

        if (abstractProxySettings.getUsername() != null) {
            Credentials credentials = new UsernamePasswordCredentials(abstractProxySettings.getUsername(),
                    abstractProxySettings.getPassword());

            ApacheHttpClient4Engine engine = (ApacheHttpClient4Engine) client.httpEngine();
            HttpContext context = new BasicHttpContext();
            engine.setHttpContext(context);

            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            context.setAttribute(HttpClientContext.CREDS_PROVIDER, credsProvider);
            AuthScope authScope = new AuthScope(hostName, port, null, null);
            credsProvider.setCredentials(authScope, credentials);
        }

        return client;
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

    public void setLogger(Logger logger){
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
        this(logger, serverUrl, 1000*60*5);
    }


    /**
     * Sets the proxy settings to be used by the rest client.
     * @param abstractProxySettings The proxy settings to be used by the rest client.
     * If {@code null} then no proxy is set.
     */
    @SuppressWarnings("UnusedDeclaration")
    public void setProxyBase(AbstractProxySettings abstractProxySettings) {
        this.abstractProxySettings = abstractProxySettings;

        restClient = buildRestClient(timeout, abstractProxySettings);
        endPoint = restClient.target(serverUrl);
    }

    /**
     *
     * @return The current proxy settings used by the rest client,
     * or {@code null} if no proxy is set.
     */
    @SuppressWarnings("UnusedDeclaration")
    public AbstractProxySettings getProxyBase() {
        return abstractProxySettings;
    }

    /**
     * Sets the connect and read timeouts for web requests.
     *
     * @param timeout Connect/Read timeout in milliseconds. 0 equals infinity.
     */
    public void setTimeout(int timeout) {
        ArgumentGuard.greaterThanOrEqualToZero(timeout, "timeout");
        this.timeout = timeout;

        restClient = buildRestClient(timeout, abstractProxySettings);
        endPoint = restClient.target(serverUrl);
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

        endPoint = restClient.target(serverUrl);
    }

    /**
     *
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
    protected Invocation.Builder makeEyesRequest(WebTarget target, Map<String, Object> queryParams, String... responseTypes) {
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
     *
     * @param errMsg The error message.
     * @param statusCode The response status code.
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
            if(statusCode == 401 || statusCode == 403){
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
