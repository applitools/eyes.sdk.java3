package com.applitools.eyes.selenium;

import com.applitools.ICheckSettings;
import com.applitools.eyes.Region;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.TestUtils;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Test;
import java.net.URL;

import static com.applitools.eyes.selenium.TestDataProvider.*;

public class MobileNativeSauceLabsTests {

    @Test
    public void AndroidNativeAppWindowTest() throws Exception {
        Eyes eyes = initEyes();//(capabilities);
        WebDriver driver = new AndroidDriver(new URL(SAUCE_SELENIUM_URL), getAndroidCapabilities());
        try {
            eyes.open(driver, "AndroidNativeApp", "AndroidNativeApp checkWindow");
            eyes.check("Contact list", Target.window().ignore(new Region(0,0,1440,100)));
            eyes.close();
        } finally {
            driver.quit();
            eyes.abortIfNotClosed();
        }
    }

    @Test
    public void AndroidNativeAppRegionTest() throws Exception {
        Eyes eyes = initEyes();
        WebDriver driver = new AndroidDriver(new URL(SAUCE_SELENIUM_URL), getAndroidCapabilities());
        try {
            eyes.open(driver, "AndroidNativeApp", "AndroidNativeApp checkRegionFloating");
            ICheckSettings settings = Target.region(new Region(0, 100, 1400, 2000))
                    .floating(new Region(10, 10, 20, 20), 3, 3, 20, 30);
            eyes.check("Contact list - Region with floating region", settings);
            eyes.close();
        } finally {
            driver.quit();
            eyes.abortIfNotClosed();
        }
    }

    @Test
    public void iOSNativeAppTest() throws Exception {
        Eyes eyes = initEyes();
        WebDriver driver = null;
        try {
            driver = new IOSDriver(new URL(SAUCE_SELENIUM_URL), getIOSCapabilities());
            eyes.open(driver, "iOSNativeApp", "iOSNativeApp checkWindow");
            eyes.check("checkWindow", Target.window().ignore(new Region(0,0,300,100)));
            eyes.close();
        } finally {
            if (driver != null) {
                driver.quit();
            }
            eyes.abortIfNotClosed();
        }
    }

    @Test
    public void iOSNativeAppRegionTest() throws Exception {
        Eyes eyes = initEyes();
        WebDriver driver = null;
        try {
            driver = new IOSDriver(new URL(SAUCE_SELENIUM_URL), getIOSCapabilities());
            eyes.open(driver, "iOSNativeApp", "iOSNativeApp checkRegionFloating");
            ICheckSettings settings = Target.region(new Region(0, 100, 375, 712))
            .floating(new Region(10, 10, 20, 20), 3, 3, 20, 30);
            eyes.check("Fluent - Window with floating region", settings);
            eyes.close();
        } finally {
            if (driver != null) {
                driver.quit();
            }

            eyes.abortIfNotClosed();
        }
    }

    private Eyes initEyes() {
        Eyes eyes = new Eyes();
        String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
        TestUtils.setupLogging(eyes, testName);
        eyes.setBatch(TestDataProvider.batchInfo);
        return eyes;
    }

    private DesiredCapabilities getAndroidCapabilities() {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("deviceName", "Samsung Galaxy S9 WQHD GoogleAPI Emulator");
        capabilities.setCapability("platformVersion", "8.1");
        capabilities.setCapability("app", "http://saucelabs.com/example_files/ContactManager.apk");
        capabilities.setCapability("clearSystemFiles", true);
        capabilities.setCapability("noReset", true);
        capabilities.setCapability("username", SAUCE_USERNAME);
        capabilities.setCapability("accesskey", SAUCE_ACCESS_KEY);
        return capabilities;
    }

    private DesiredCapabilities getIOSCapabilities() {
        DesiredCapabilities capabilities = DesiredCapabilities.iphone();
        capabilities.setCapability("deviceName","iPhone XS Simulator");
        capabilities.setCapability("platformVersion", "12.2");
        capabilities.setCapability("platformName", "iOS");
        capabilities.setCapability("app", "https://applitools.bintray.com/Examples/HelloWorldiOS_1_0.zip");
        capabilities.setCapability("clearSystemFiles", true);
        capabilities.setCapability("noReset", true);
        capabilities.setCapability("NATIVE_APP", true);
        capabilities.setCapability("browserName", "");
        capabilities.setCapability("username", SAUCE_USERNAME);
        capabilities.setCapability("accesskey", SAUCE_ACCESS_KEY);
        return capabilities;
    }
}

