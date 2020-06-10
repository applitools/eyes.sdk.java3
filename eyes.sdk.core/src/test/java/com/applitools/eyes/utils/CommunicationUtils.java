package com.applitools.eyes.utils;

import com.applitools.eyes.BatchInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class CommunicationUtils {

    public static String getString(String url) {
        return getString(url, null);
    }

    public static String getString(String url, HttpAuth creds) {
        try (CloseableHttpClient httpClient = HttpClients.custom().build()) {
            HttpGet request = new HttpGet(url);
            setCredentials(creds, request);
            HttpResponse httpResponse = httpClient.execute(request);
            HttpEntity entity = httpResponse.getEntity();

            BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
            // Read in all of the post results into a String.
            StringBuilder output = new StringBuilder();
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                output.append(currentLine);
            }

            return  output.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <Tin> void putJson(String url, Tin data, HttpAuth creds) {
        jsonRequest(url, data, creds, new HttpPut());
    }

    public static <Tin> void postJson(String url, Tin data, HttpAuth creds) {
        jsonRequest(url, data, creds, new HttpPost());
    }

    public static <Tin> void jsonRequest(String url, Tin data, HttpAuth creds, HttpEntityEnclosingRequestBase request) {
        try (CloseableHttpClient httpClient = HttpClients.custom().build()) {
            request.setURI(new URI(url));
            setCredentials(creds, request);
            String json = createJsonString(data);
            request.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

            httpClient.execute(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setCredentials(HttpAuth creds, HttpRequestBase request) {
        if (creds != null) {
            request.addHeader(creds.getHeader());
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
        try (CloseableHttpClient httpClient = HttpClients.custom().build()) {
            String url = String.format("%s/api/sessions/batches/%s/bypointerid?apikey=%s", serverUrl, batchId, apikey);
            HttpGet request = new HttpGet(url);

            HttpResponse response = httpClient.execute(request);
            batchInfo = null;
            if (response.getStatusLine().getStatusCode() == 200) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper = objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                byte[] bytes = new byte[0];
                try {
                    bytes = IOUtils.toByteArray(response.getEntity().getContent());
                    String s = new String(bytes, StandardCharsets.UTF_8);
                    System.out.println(s);
                } catch (IOException ignored) {
                }
                batchInfo = objectMapper.readValue(bytes, BatchInfo.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return batchInfo;
    }
}
