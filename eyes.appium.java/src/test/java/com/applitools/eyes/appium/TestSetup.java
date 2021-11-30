package com.applitools.eyes.appium;

import com.applitools.eyes.FileLogger;
import com.applitools.eyes.LogHandler;
import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.logging.TraceLevel;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.utils.GeneralUtils;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.File;
import java.net.MalformedURLException;

import static com.applitools.eyes.utils.TestUtils.logsPath;

public abstract class TestSetup extends ReportingTestSuite implements ITest {

    protected DesiredCapabilities capabilities;
    protected AppiumDriver driver;
    protected Eyes eyes;
    // To run locally use http://127.0.0.1:4723/wd/hub
    protected String appiumServerUrl = "http://" + GeneralUtils.getEnvString("BROWSERSTACK_USERNAME") + ":" +
            GeneralUtils.getEnvString("BROWSERSTACK_ACCESS_KEY") + "@hub-cloud.browserstack.com/wd/hub";

    public void setupLogging(Eyes eyes) {
        String testName = getTestName();
        String[] parts = testName.split("\\.");
        setupLogging(eyes, parts[parts.length - 1]);
    }

    public static void setupLogging(Eyes eyes, String methodName) {
        LogHandler logHandler;
        if (!TestUtils.runOnCI && logsPath != null) {
            String path = TestUtils.initLogPath(methodName);
            logHandler = new FileLogger(path + File.separator + methodName + ".log", false, true);
            eyes.setDebugScreenshotsPath(path);
            eyes.setDebugScreenshotsPrefix(methodName + "_");
            eyes.setSaveDebugScreenshots(true);
        } else {
            logHandler = new StdoutLogHandler(TraceLevel.Warn);
        }
        eyes.setLogHandler(logHandler);
    }

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
        eyes.setApiKey(GeneralUtils.getEnvString("APPLITOOLS_API_KEY"));
        setupLogging(eyes);
        eyes.setSaveNewTests(false);
        if (GeneralUtils.getEnvString("APPLITOOLS_USE_PROXY") != null) {
            eyes.setProxy(new ProxySettings("http://127.0.0.1", 8888));
        }

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
        capabilities.setCapability("browserstack.appium_version", "1.21.0");
        setAppCapability();
    }

    protected abstract void initDriver() throws MalformedURLException;

    protected abstract void setAppCapability();

    protected abstract String getApplicationName();
}
