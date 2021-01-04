package com.applitools.eyes.appium;

import com.applitools.eyes.LogHandler;
import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.selenium.AsyncClassicRunner;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.utils.GeneralUtils;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.net.MalformedURLException;

public abstract class TestSetup extends ReportingTestSuite implements ITest {

    protected DesiredCapabilities capabilities;
    protected AppiumDriver<MobileElement> driver;
    protected Eyes eyes;
    // To run locally use http://127.0.0.1:4723/wd/hub
    protected String appiumServerUrl = "http://" + GeneralUtils.getEnvString("BROWSERSTACK_USERNAME") + ":" +
            GeneralUtils.getEnvString("BROWSERSTACK_ACCESS_KEY") + "@hub-cloud.browserstack.com/wd/hub";

    @Override
    public String getTestName() {
        return getClass().getName();
    }

    @BeforeClass
    public void beforeClass() {
        super.setGroupName("appium");
        capabilities = new DesiredCapabilities();
        setCapabilities();

        eyes = new Eyes(new AsyncClassicRunner());
        eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));

        LogHandler logHandler = new StdoutLogHandler(TestUtils.verboseLogs);
        eyes.setLogHandler(logHandler);
        eyes.setSaveNewTests(false);
        if (System.getenv("APPLITOOLS_USE_PROXY") != null) {
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
        capabilities.setCapability("browserstack.appium_version", "1.17.0");
        setAppCapability();
    }

    protected abstract void initDriver() throws MalformedURLException;

    protected abstract void setAppCapability();

    protected abstract String getApplicationName();
}
