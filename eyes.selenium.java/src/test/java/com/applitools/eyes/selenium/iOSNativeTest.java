package com.applitools.eyes.selenium;

import com.applitools.eyes.LogHandler;
import com.applitools.eyes.ServerConnector;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.utils.TestUtils;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;

import static com.applitools.eyes.selenium.TestDataProvider.*;

public class iOSNativeTest {

    public static void main(String[] args) throws Exception {

        // Set desired capabilities.
        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability("platformName", "iOS");
        capabilities.setCapability("deviceName", "iPhone 7 Simulator");
        capabilities.setCapability("platformVersion", "10.3");
        capabilities.setCapability("app", "https://store.applitools.com/download/iOS.TestApp.app.zip");
        capabilities.setCapability("clearSystemFiles", true);
        capabilities.setCapability("noReset", true);

        capabilities.setCapability("username", SAUCE_USERNAME);
        capabilities.setCapability("accesskey", SAUCE_ACCESS_KEY);

        WebDriver driver = new IOSDriver(new URL(SAUCE_SELENIUM_URL), capabilities);

        // Initialize the VisualGridEyes SDK and set your private API key.
        Eyes eyes = new Eyes();
        eyes.setServerConnector(new ServerConnector());
        LogHandler logHandler = new StdoutLogHandler(TestUtils.verboseLogs);
        eyes.setLogHandler(logHandler);
        eyes.setSaveDebugScreenshots(true);
//        VisualGridEyes.setForceFullPageScreenshot(true);
        try {

            // Start the test.
            eyes.open(driver, "Contacts!", "My first Appium native Java test!");

            // Visual validation.
            eyes.checkWindow("Contact list!");

            // End the test.
            eyes.close();

        } finally {

            // Close the app.
            driver.quit();

            // If the test was aborted before VisualGridEyes.close was called, ends the test as aborted.
            eyes.abortIfNotClosed();
        }
    }
}
