package com.applitools.eyes.images.utils;

import com.applitools.connectivity.RestClient;
import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.*;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.images.Eyes;
import com.applitools.eyes.metadata.SessionResults;
import com.applitools.utils.ClassVersionGetter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class TestSetup {

    public final static boolean runOnCI = System.getenv("CI") != null;
    public final static boolean verboseLogs = !runOnCI || "true".equalsIgnoreCase(System.getenv("APPLITOOLS_VERBOSE_LOGS"));

    protected Eyes eyes;
    private static final String TEST_SUITE_NAME = "Eyes Image SDK";
    protected static final BatchInfo batch = new BatchInfo(TEST_SUITE_NAME);

    @BeforeMethod
    public void beforeClass() {
        eyes = new Eyes();
        eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));

        LogHandler logHandler = new StdoutLogHandler(verboseLogs);
        eyes.setLogHandler(logHandler);
        eyes.setSaveNewTests(false);
        eyes.setBatch(batch);

        if (System.getenv("APPLITOOLS_USE_PROXY") != null) {
            eyes.setProxy(new ProxySettings("http://127.0.0.1", 8888));
        }
    }

    @AfterClass
    public void afterClass() {
        eyes.abortIfNotClosed();
    }

    protected SessionResults getTestInfo(TestResults results) {
        SessionResults sessionResults = null;
        try {
            sessionResults = getSessionResults(eyes.getApiKey(), results);
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail("Exception appeared while getting session results");
        }
        return sessionResults;
    }

    protected String getApplicationName() {
        return "Eyes Images SDK";
    }

    private static SessionResults getSessionResults(String apiKey, TestResults results) throws java.io.IOException {
        String apiSessionUrl = results.getApiUrls().getSession();
        URI apiSessionUri = UriBuilder.fromUri(apiSessionUrl)
                .queryParam("format", "json")
                .queryParam("AccessToken", results.getSecretToken())
                .queryParam("apiKey", apiKey)
                .build();

        RestClient client = new RestClient(new Logger(new StdoutLogHandler()), apiSessionUri, ServerConnector.DEFAULT_CLIENT_TIMEOUT);
        client.setAgentId(ClassVersionGetter.CURRENT_VERSION);
        if (System.getenv("APPLITOOLS_USE_PROXY") != null) {
            client.setProxy(new ProxySettings("http://127.0.0.1", 8888));
        }

        String srStr = client.sendHttpRequest(apiSessionUri.toString(), HttpMethod.GET, MediaType.APPLICATION_JSON).getBodyString();
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        return jsonMapper.readValue(srStr, SessionResults.class);
    }
}
