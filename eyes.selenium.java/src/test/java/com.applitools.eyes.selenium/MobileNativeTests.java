package com.applitools.eyes.selenium;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.FileLogger;
import com.applitools.eyes.LogHandler;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.selenium.fluent.Target;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.testng.annotations.Test;

@org.testng.annotations.Test()
public class MobileNativeTests {

    private String logsPath = System.getenv("APPLITOOLS_LOGS_PATH");
    private String appiumServerUrl = System.getenv("SELENIUM_SERVER_URL");
    private static BatchInfo batchInfo = new BatchInfo("Mobile Native Tests");

    private void setupLogging(Eyes eyes, DesiredCapabilities capabilities, String methodName) {
        LogHandler logHandler;
        if (System.getenv("CI") == null && logsPath != null) {
            String path = logsPath + File.separator + "java" + File.separator + methodName;
            logHandler = new FileLogger(path + File.separator + methodName + "_" + capabilities.getPlatform() + ".log", true, true);
            eyes.setDebugScreenshotsPath(path);
            eyes.setDebugScreenshotsPrefix(methodName + "_");
            eyes.setSaveDebugScreenshots(true);
        } else {
            logHandler = new StdoutLogHandler(false);
        }

        capabilities.setCapability("username", System.getenv("SAUCE_USERNAME"));
        capabilities.setCapability("accesskey", System.getenv("SAUCE_ACCESS_KEY"));
        capabilities.setCapability("name", methodName);

        eyes.setLogHandler(logHandler);
        eyes.setBatch(batchInfo);
    }

    @Test
    public void AndroidNativeAppTest1() throws Exception {

        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("deviceName", "Android Emulator");
        capabilities.setCapability("platformVersion", "6.0");
        capabilities.setCapability("app", "http://saucelabs.com/example_files/ContactManager.apk");
        capabilities.setCapability("clearSystemFiles", true);
        capabilities.setCapability("noReset", true);

        Eyes eyes = new Eyes();
        setupLogging(eyes, capabilities, "AndroidNativeAppTest1");

        WebDriver driver = new AndroidDriver(new URL(appiumServerUrl), capabilities);

        try {
            eyes.open(driver, "Mobile Native Tests", "Android Native App 1");
            eyes.checkWindow("Contact list");
            eyes.close();
        } finally {
            driver.quit();
            eyes.abortIfNotClosed();
        }
    }

    @Test
    public void AndroidNativeAppTest2() throws MalformedURLException, InterruptedException {

        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("platformVersion", "7.1");
        capabilities.setCapability("deviceName", "Samsung Galaxy S8 WQHD GoogleAPI Emulator");
        capabilities.setCapability("automationName", "uiautomator2");

        capabilities.setCapability("app", "https://applitools.bintray.com/Examples/app-debug.apk");

        capabilities.setCapability("appPackage", "com.applitoolstest");
        capabilities.setCapability("appActivity", "com.applitoolstest.ScrollActivity");
        capabilities.setCapability("newCommandTimeout", 600);

        Eyes eyes = new Eyes();
        setupLogging(eyes, capabilities,"AndroidNativeAppTest2");

        AndroidDriver<AndroidElement> driver = new AndroidDriver<>(new URL(appiumServerUrl), capabilities);

        try {
            eyes.open(driver, "Mobile Native Tests", "Android Native App 2");
            Thread.sleep(10000);

            MobileElement scrollableElement = driver.findElement(MobileBy.AndroidUIAutomator("new UiSelector().scrollable(true)"));

            eyes.check("Main window with ignore", Target.region(scrollableElement).ignore(scrollableElement));
            eyes.close(false);
        } finally {
            eyes.abortIfNotClosed();
            driver.quit();
        }
    }

    @Test
    public void iOSNativeAppTest() throws Exception {

        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability("platformName", "iOS");
        capabilities.setCapability("deviceName", "iPhone 7 Simulator");
        capabilities.setCapability("platformVersion", "10.0");
        capabilities.setCapability("app", "https://store.applitools.com/download/iOS.TestApp.app.zip");
        capabilities.setCapability("clearSystemFiles", true);
        capabilities.setCapability("noReset", true);

        Eyes eyes = new Eyes();
        setupLogging(eyes, capabilities,"iOSNativeAppTest");

        WebDriver driver = new IOSDriver(new URL(appiumServerUrl), capabilities);

        try {
            eyes.open(driver, "Mobile Native Tests", "iOS Native App");
            eyes.checkWindow("checkWindow");
            eyes.close();
        } finally {
            driver.quit();
            eyes.abortIfNotClosed();
        }
    }
}
