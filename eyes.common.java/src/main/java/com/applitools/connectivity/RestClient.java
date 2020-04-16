package com.applitools.connectivity;

import com.applitools.connectivity.api.HttpClient;
import com.applitools.connectivity.api.Request;
import com.applitools.connectivity.api.Response;
import com.applitools.connectivity.api.Target;
import com.applitools.eyes.EyesException;
import com.applitools.eyes.Logger;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;

import java.net.URI;
import java.util.Calendar;
import java.util.TimeZone;

public class RestClient {

    /**
     * An interface used as base for anonymous classes wrapping Http Method
     * calls.
     */
    protected interface HttpMethodCall {
        Response call();
    }

    public static final int DEFAULT_CLIENT_TIMEOUT = 1000 * 60 * 5; // 5 minutes

    protected Logger logger;
    protected HttpClient restClient;
    protected URI serverUrl;
    protected Target endPoint;

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
    public Response sendHttpWebRequest(String url, final String method, String... accept) {
        Request request = restClient.target(url).request(accept);
        return request.method(method, null, null);
    }

    protected Response sendLongRequest(Request invocationBuilder, String method, String data) throws EyesException {
        String currentTime = GeneralUtils.toRfc1123(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
        invocationBuilder = invocationBuilder.header("Eyes-Expect", "202+location").header("Eyes-Date", currentTime);
        Response response = invocationBuilder.method(method, data, null);

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
}
