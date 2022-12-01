package com.applitools.eyes.appium;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.LogHandler;
import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.utils.GeneralUtils;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.MutableCapabilities;
import org.testng.ITest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

import java.net.MalformedURLException;

public abstract class TestSetup extends ReportingTestSuite implements ITest {

    protected MutableCapabilities capabilities;
    protected AppiumDriver driver;
    protected Eyes eyes;
    protected BatchInfo batch;

    // To run locally use http://127.0.0.1:4723/wd/hub
    protected String BS_URL = "http://" + GeneralUtils.getEnvString("BROWSERSTACK_USERNAME") + ":" +
            GeneralUtils.getEnvString("BROWSERSTACK_ACCESS_KEY") + "@hub-cloud.browserstack.com/wd/hub";

    private final String USERNAME = GeneralUtils.getEnvString("SAUCE_USERNAME");
    private final String ACCESS_KEY = GeneralUtils.getEnvString("SAUCE_ACCESS_KEY");
    protected final String SL_URL = "https://"+USERNAME+":" + ACCESS_KEY + "@ondemand.us-west-1.saucelabs.com:443/wd/hub";

    @BeforeSuite
    public void beforeSuite() {
        batch = new BatchInfo(getApplicationName());
    }

    @Override
    public String getTestName() {
        return getClass().getName();
    }

    @BeforeClass
    public void beforeClass() {
        super.setGroupName("appium");
        capabilities = new MutableCapabilities();
        setCapabilities();

        eyes = new Eyes();
        eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));
        eyes.setBatch(batch);

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
        if (driver != null)
            driver.quit();

        // If the test was aborted before eyes.close was called, ends the test as aborted.
        eyes.abortIfNotClosed();
    }

    protected void setCapabilities() {
        setDeviceCapability();
        setPlatformVersionCapability();
        setSauceCapabilities();
        setAppCapability();
    }

    protected void setSauceCapabilities() {
        MutableCapabilities sauceOptions = new MutableCapabilities();
        sauceOptions.setCapability("appiumVersion", "1.22.1");
        sauceOptions.setCapability("name", "Java Appium");
        sauceOptions.setCapability("idleTimeout", 300);
        capabilities.setCapability("sauce:options", sauceOptions);
    }

    protected abstract void initDriver() throws MalformedURLException;

    protected abstract void setAppCapability();

    protected abstract void setDeviceCapability();

    protected abstract void setPlatformVersionCapability();

    protected abstract String getApplicationName();
}