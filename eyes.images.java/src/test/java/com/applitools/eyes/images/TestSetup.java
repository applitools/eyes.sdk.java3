package com.applitools.eyes.images;

import com.applitools.eyes.*;
import com.applitools.eyes.metadata.SessionResults;
import com.applitools.eyes.utils.TestUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class TestSetup {

    protected Eyes eyes;
    private static final String TEST_SUITE_NAME = "Eyes Image SDK";

    @BeforeClass
    public void beforeClass() {
        eyes = new Eyes();
        eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));

        LogHandler logHandler = new StdoutLogHandler(TestUtils.verboseLogs);
        eyes.setLogHandler(logHandler);
        eyes.setSaveNewTests(false);
        eyes.setBatch(new BatchInfo(TEST_SUITE_NAME));

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
            sessionResults = TestUtils.getSessionResults(eyes.getApiKey(), results);
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail("Exception appeared while getting session results");
        }
        return sessionResults;
    }

    protected String getApplicationName() {
        return "Eyes Images SDK";
    }
}
