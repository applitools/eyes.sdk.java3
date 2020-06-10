package com.applitools.eyes.utils;

import com.applitools.connectivity.ServerConnector;
import com.applitools.connectivity.api.HttpClient;
import com.applitools.connectivity.api.HttpClientImpl;
import com.applitools.connectivity.api.Request;
import com.applitools.connectivity.api.Response;
import com.applitools.eyes.BatchInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;

public class CommunicationUtils {

    private static HttpClient createClient() {
        return new HttpClientImpl(ServerConnector.DEFAULT_CLIENT_TIMEOUT, null);
    }

    public static <Tin> void putJson(String url, Tin data, HttpAuth creds) {
        jsonRequest(url, data, creds, HttpMethod.PUT);
    }

    public static <Tin> void postJson(String url, Tin data, HttpAuth creds) {
        jsonRequest(url, data, creds, HttpMethod.POST);
    }

    public static <Tin> void jsonRequest(String url, Tin data, HttpAuth creds, String httpMethod) {
        HttpClient httpClient = createClient();
        Response response = null;
        try {
            Request request = httpClient.target(url).request();
            setCredentials(creds, request);
            String json = createJsonString(data);
            response = request.method(httpMethod, json, MediaType.APPLICATION_JSON);
            if (response.getStatusCode() != HttpStatus.SC_OK) {
                System.out.println(String.format("Test report failed. Status: %d %s. Body: %s",
                        response.getStatusCode(), response.getStatusPhrase(), response.readEntity(String.class)));
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    public static BatchInfo getBatch(String batchId, String serverUrl, String apikey) {
        BatchInfo batchInfo = null;
        HttpClient httpClient = createClient();
        try {
            String url = String.format("%s/api/sessions/batches/%s/bypointerid?apikey=%s", serverUrl, batchId, apikey);
            Request request = httpClient.target(url).request();
            Response response = request.method(HttpMethod.GET, null, null);
            String data = response.readEntity(String.class);
            response.close();
            batchInfo = null;
            if (response.getStatusCode() == 200) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper = objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                batchInfo = objectMapper.readValue(data, BatchInfo.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.close();
        }
        return batchInfo;
    }
}