package com.applitools.eyes.selenium;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static com.applitools.eyes.selenium.TestDataProvider.*;

public class TestBrowserStack {

    @DataProvider
    public static Object[][] stitchModeDP() {
        return new Object[][]{{StitchMode.CSS}, {StitchMode.SCROLL}};
    }

    @Test(dataProvider = "stitchModeDP")
    public void TestIE11(StitchMode stitchMode) {

        Eyes eyes = new Eyes();
        TestUtils.setupLogging(eyes);

        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("browser", "IE");
        caps.setCapability("browser_version", "11.0");
        caps.setCapability("os", "Windows");
        caps.setCapability("os_version", "10");
        caps.setCapability("resolution", "1024x768");
        caps.setCapability("browserstack.user", BROWSERSTACK_USERNAME);
        caps.setCapability("browserstack.key", BROWSERSTACK_ACCESS_KEY);

        WebDriver driver = null;
        try {
            driver = new RemoteWebDriver(new URL(BROWSERSTACK_SELENIUM_URL), caps);
            driver.get("https://applitools.github.io/demo/TestPages/FramesTestPage/");
            eyes.setStitchMode(stitchMode);
            eyes.setBatch(TestDataProvider.batchInfo);
            eyes.open(driver, "TestBrowserStack", "TesIE11_" + stitchMode, new RectangleSize(800, 600));
            eyes.check("viewport", Target.window().fully(false).sendDom(false));
            eyes.check("full page", Target.window().fully(true).sendDom(false));
            eyes.close();
        } catch (MalformedURLException e) {
            GeneralUtils.logExceptionStackTrace(eyes.getLogger(), e);
        } finally {
            if (driver != null) {
                driver.quit();
            }
            eyes.abort();
        }
    }

}
