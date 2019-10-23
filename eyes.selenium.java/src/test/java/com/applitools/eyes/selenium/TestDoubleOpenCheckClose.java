package com.applitools.eyes.selenium;

import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.TestUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static com.applitools.eyes.selenium.TestDataProvider.*;

public class TestDoubleOpenCheckClose {
    @Test
    public void TestDoubleOpenCheckCloseOnSauceLabs() throws MalformedURLException {
        Eyes eyes = new Eyes();
        TestUtils.setupLogging(eyes);
        eyes.setSendDom(false);
        eyes.setBatch(TestDataProvider.batchInfo);

        //*************
        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability("name", "Open Check Close X2 (A)");
        capabilities.setCapability("appiumVersion", "1.9.1");
        capabilities.setCapability("deviceName", "Samsung Galaxy Tab S3 GoogleAPI Emulator");
        capabilities.setCapability("deviceOrientation", "landscape");
        capabilities.setCapability("platformVersion", "8.1");
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("browserName", "Chrome");
        capabilities.setCapability("username", SAUCE_USERNAME);
        capabilities.setCapability("accessKey", SAUCE_ACCESS_KEY);

        WebDriver driver = new RemoteWebDriver(new URL(SAUCE_SELENIUM_URL), capabilities);
        //*************

        try
        {
            eyes.open(driver, "Applitools", "Open Check Close X2 SauceLabs (A)");
            driver.get("https://www.applitools.com");
            eyes.check(Target.window().sendDom(false));
            eyes.close(false);
        }
        finally
        {
            // Close the browser.
            driver.quit();

            // If the test was aborted before eyes.Close was called, ends the test as aborted.
            eyes.abort();
        }

        //*************
        DesiredCapabilities capabilities2 = new DesiredCapabilities();

        capabilities2.setCapability("name", "Open Check Close X2 (B)");
        capabilities2.setCapability("username",SAUCE_USERNAME);
        capabilities2.setCapability("accessKey", SAUCE_ACCESS_KEY);
      //capabilities2.setCapability("extendedDebugging", true);
        capabilities2.setCapability("appiumVersion", "1.12.1");
        capabilities2.setCapability("deviceName", "iPad Simulator");
        capabilities2.setCapability("deviceOrientation", "portrait");
        capabilities2.setCapability("platformVersion", "12.2");
        capabilities2.setCapability("platformName", "iOS");
        capabilities2.setCapability("browserName", "Safari");

        driver = new RemoteWebDriver(new URL(SAUCE_SELENIUM_URL), capabilities2);

        try
        {
            eyes.open(driver, "Applitools", "Open Check Close X2 SauceLabs (B)");
            driver.get("https://www.google.com");
            eyes.check(Target.window().sendDom(false));
            eyes.close(false);
        }
        finally
        {
            // Close the browser.
            driver.quit();

            // If the test was aborted before eyes.Close was called, ends the test as aborted.
            eyes.abort();
        }
    }
}
