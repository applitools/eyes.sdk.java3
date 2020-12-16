package com.applitools.eyes.utils;

import com.applitools.connectivity.ServerConnector;
import com.applitools.connectivity.api.HttpClient;
import com.applitools.connectivity.api.HttpClientImpl;
import com.applitools.connectivity.api.Request;
import com.applitools.connectivity.api.Response;
import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.Logger;
import com.applitools.eyes.StdoutLogHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

public class CommunicationUtils {

    private static HttpClient createClient(Logger logger) {
        return new HttpClientImpl(logger, ServerConnector.DEFAULT_CLIENT_TIMEOUT, null);
    }

    public static <Tin> void jsonRequest(Logger logger, String url, Tin data, HttpAuth creds, String httpMethod) {
        HttpClient httpClient = createClient(logger);
        Response response = null;
        try {
            Request request = httpClient.target(url).request();
            setCredentials(creds, request);
            String json = createJsonString(data);
            response = request.method(httpMethod, json, MediaType.APPLICATION_JSON);
            if (response.getStatusCode() != HttpStatus.SC_OK) {
                throw new IllegalStateException(String.format("Test report failed. Status: %d %s. Body: %s",
                        response.getStatusCode(), response.getStatusPhrase(), response.getBodyString()));
            }
        } finally {
            if (response != null) {
                response.close();
            }
            httpClient.close();
        }
    }

    private static void setCredentials(HttpAuth creds, Request request) {
        if (creds != null) {
            request.header(creds.getHeader().getName(), creds.getHeader().getValue());
        }
    }

    public static <Tin> String createJsonString(Tin data) {
        ObjectMapper jsonMapper = new ObjectMapper();
        String json;
        try {
            json = jsonMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            json = "{}";
            e.printStackTrace();
        }
        return json;
    }

    public static BatchInfo getBatch(Logger logger, String batchId, String serverUrl, String apikey) throws Exception {
        BatchInfo batchInfo;
        HttpClient httpClient = createClient(logger);
        try {
            String url = String.format("%s/api/sessions/batches/%s/bypointerid?apikey=%s", serverUrl, batchId, apikey);
            Request request = httpClient.target(url).request();
            Response response = request.method(HttpMethod.GET, null, null);
            String data = response.getBodyString();
            response.close();
            if (response.getStatusCode() != HttpStatus.SC_OK) {
                logger.log(String.format("Bad status code from getBatch: %d", response.getStatusCode()));
                throw new IOException("Failed getting batch info from the server");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper = objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            batchInfo = objectMapper.readValue(data, BatchInfo.class);
        } finally {
            httpClient.close();
        }
        return batchInfo;
    }
}