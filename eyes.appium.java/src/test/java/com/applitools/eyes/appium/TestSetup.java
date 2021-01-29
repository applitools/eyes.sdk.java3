package com.applitools.eyes.appium;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.LogHandler;
import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.TestUtils;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.net.MalformedURLException;

public abstract class TestSetup extends ReportingTestSuite implements ITest {
    public final static BatchInfo batchInfo = new BatchInfo("Java3 Appium Tests");

    protected DesiredCapabilities capabilities;
    protected AppiumDriver<MobileElement> driver;
    protected Eyes eyes;
    // To run locally use http://127.0.0.1:4723/wd/hub
    public final static String SAUCE_USERNAME = System.getenv("SAUCE_USERNAME");
    public final static String SAUCE_ACCESS_KEY = System.getenv("SAUCE_ACCESS_KEY");
    public final static String SAUCE_URL = String.format("https://%s:%s@ondemand.us-west-1.saucelabs.com:443/wd/hub", SAUCE_USERNAME, SAUCE_ACCESS_KEY);

    @Override
    public String getTestName() {
        return getClass().getName();
    }

    @BeforeClass
    public void beforeClass() {
        super.setGroupName("appium");
        capabilities = new DesiredCapabilities();
        setCapabilities();

        eyes = new Eyes();

        LogHandler logHandler = new StdoutLogHandler(TestUtils.verboseLogs);
        eyes.setLogHandler(logHandler);
        eyes.setSaveNewTests(false);
        if (System.getenv("APPLITOOLS_USE_PROXY") != null) {
            eyes.setProxy(new ProxySettings("http://127.0.0.1", 8888));
        }
        String batchId = System.getenv("APPLITOOLS_BATCH_ID");
        if (batchId != null) {
            batchInfo.setId(batchId);
        }

        eyes.setBatch(batchInfo);

        try {
            initDriver();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public void afterClass() {
        // Close the app.
        driver.quit();

        // If the test was aborted before eyes.close was called, ends the test as aborted.
        eyes.abortIfNotClosed();
    }

    protected void setCapabilities() {
        capabilities.setCapability("browserstack.appium_version", "1.17.0");
        capabilities.setCapability("name", getClass().getName());
        setAppCapability();
    }

    protected abstract void initDriver() throws MalformedURLException;

    protected abstract void setAppCapability();

    protected abstract String getApplicationName();
}
