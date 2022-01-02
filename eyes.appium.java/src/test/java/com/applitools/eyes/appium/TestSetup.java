package com.applitools.eyes.appium;

import com.applitools.eyes.LogHandler;
import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.net.MalformedURLException;
import java.time.Duration;
import java.util.Collections;

public abstract class TestSetup extends ReportingTestSuite implements ITest {

    protected DesiredCapabilities capabilities;
    protected RemoteWebDriver driver;
    protected Eyes eyes;
    // To run locally use http://127.0.0.1:4723/wd/hub
    protected String appiumServerUrl = "http://" + GeneralUtils.getEnvString("BROWSERSTACK_USERNAME") + ":" +
            GeneralUtils.getEnvString("BROWSERSTACK_ACCESS_KEY") + "@hub-cloud.browserstack.com/wd/hub";
//    protected String appiumServerUrl = "https://" + GeneralUtils.getEnvString("SAUCE_USERNAME") + ":" +
//            GeneralUtils.getEnvString("SAUCE_ACCESS_KEY") + "@ondemand.saucelabs.com:443/wd/hub";
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
        capabilities.setCapability("browserstack.appium_version", "1.21.0");
        setAppCapability();
    }

    protected abstract void initDriver() throws MalformedURLException;

    protected abstract void setAppCapability();

    protected abstract String getApplicationName();

    protected void scrollTo(int startX, int startY, int endX, int endY) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence scrollAction = new Sequence(finger, 1);
        scrollAction.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), startX, startY));
        scrollAction.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        scrollAction.addAction(finger.createPointerMove(Duration.ofMillis(1500), PointerInput.Origin.viewport(), endX, endY));
        scrollAction.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(scrollAction));
    }
}
