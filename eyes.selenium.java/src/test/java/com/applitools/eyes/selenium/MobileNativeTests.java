package com.applitools.eyes.selenium;

import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.config.Feature;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumTestUtils;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static com.applitools.eyes.selenium.TestDataProvider.*;

public class MobileNativeTests extends ReportingTestSuite {

    public MobileNativeTests() {
        super.setGroupName("selenium");
    }

    private void setCapabilities(DesiredCapabilities capabilities, String methodName) {
        capabilities.setCapability("username", SAUCE_USERNAME);
        capabilities.setCapability("accesskey", SAUCE_ACCESS_KEY);
        capabilities.setCapability("name", methodName);
    }

    private Eyes initEyes(DesiredCapabilities capabilities) {
        Eyes eyes = new Eyes();
        String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
        SeleniumTestUtils.setupLogging(eyes, testName);
        setCapabilities(capabilities, testName);
        eyes.setBatch(TestDataProvider.batchInfo);
        eyes.setSaveNewTests(false);
        return eyes;
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

        Eyes eyes = initEyes(capabilities);

        WebDriver driver = new AndroidDriver(new URL(SAUCE_SELENIUM_URL), capabilities);

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

        capabilities.setCapability("app", "https://applitools.jfrog.io/artifactory/Examples/app-debug.apk");

        capabilities.setCapability("appPackage", "com.applitoolstest");
        capabilities.setCapability("appActivity", "com.applitoolstest.ScrollActivity");
        capabilities.setCapability("newCommandTimeout", 600);

        Eyes eyes = initEyes(capabilities);

        AndroidDriver<AndroidElement> driver = new AndroidDriver<>(new URL(SAUCE_SELENIUM_URL), capabilities);

        try {
            eyes.open(driver, "Mobile Native Tests", "Android Native App 2");
            Thread.sleep(10000);

            MobileElement scrollableElement = driver.findElement(MobileBy.AndroidUIAutomator("new UiSelector().scrollable(true)"));

            eyes.check("Main window with ignore", Target.region(scrollableElement).ignore(scrollableElement));
            eyes.close(false);
        } finally {
            driver.quit();
            eyes.abortIfNotClosed();
        }
    }

    @Test
    public void TestOptimizeMobileInfoFeatureAndroid() throws MalformedURLException {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("deviceName", "Samsung Galaxy S7 GoogleAPI Emulator");
        capabilities.setCapability("platformVersion", "8.0");
        capabilities.setCapability("app", "http://saucelabs.com/example_files/ContactManager.apk");
        capabilities.setCapability("clearSystemFiles", true);
        capabilities.setCapability("noReset", true);

        Eyes eyes = initEyes(capabilities);
        Configuration configuration = eyes.getConfiguration();
        configuration.setFeatures(Feature.USE_PREDEFINED_DEVICE_INFO);
        eyes.setConfiguration(configuration);

        AndroidDriver driver = new AndroidDriver(new URL(SAUCE_SELENIUM_URL), capabilities);
        try {
            eyes.open(driver, "Mobile Native Tests", "Test Optimize Mobile Info Feature Android");
            eyes.checkWindow("Contact list");
            eyes.close();
        } finally {
            driver.quit();
            eyes.abortIfNotClosed();
        }

        Assert.assertEquals(eyes.getDevicePixelRatio(), 4.0);
    }

    @Test
    public void TestOptimizeMobileInfoFeatureIos() throws Exception {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("deviceName", "iPhone 8 Plus Simulator");
        caps.setCapability("deviceOrientation", "portrait");
        caps.setCapability("platformVersion", "13.0");
        caps.setCapability("platformName", "iOS");
        caps.setCapability("app", "https://applitools.jfrog.io/artifactory/Examples/HelloWorldiOS_1_0.zip");

        Eyes eyes = initEyes(caps);
        Configuration configuration = eyes.getConfiguration();
        configuration.setFeatures(Feature.USE_PREDEFINED_DEVICE_INFO);
        // Overriding device name for getting the right pixel ratio
        configuration.setDeviceInfo("iPhone XR");
        eyes.setConfiguration(configuration);

        WebDriver driver = new IOSDriver(new URL(SAUCE_SELENIUM_URL), caps);
        try {
            eyes.open(driver, "Mobile Native Tests", "Test Optimize Mobile Info Feature Ios");
            eyes.checkWindow();
            eyes.close();
        } finally {
            driver.quit();
            eyes.abortIfNotClosed();
        }

        // The pixel ratio of iPhone XR
        Assert.assertEquals(eyes.getDevicePixelRatio(), 2.0);
    }
}
