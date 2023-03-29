package com.applitools.eyes.images.utils;

import com.applitools.eyes.*;
import com.applitools.eyes.images.Eyes;
import com.applitools.eyes.metadata.SessionResults;
import com.applitools.eyes.utils.ReportingTestSuite;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;

import static com.applitools.eyes.utils.TestUtils.getSessionResults;

public class TestSetup extends ReportingTestSuite {

    public final static boolean runOnCI = System.getenv("CI") != null;
    public final static boolean verboseLogs = !runOnCI || "true".equalsIgnoreCase(System.getenv("APPLITOOLS_VERBOSE_LOGS"));

    protected Eyes eyes;
    private static final String TEST_SUITE_NAME = "Eyes Image SDK";
    protected static final BatchInfo batch = new BatchInfo(TEST_SUITE_NAME);

    @BeforeMethod
    public void beforeClass() {
        super.setGroupName("images");
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

}
