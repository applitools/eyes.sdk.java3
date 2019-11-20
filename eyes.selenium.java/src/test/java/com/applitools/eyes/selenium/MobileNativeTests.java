package com.applitools.eyes.selenium;

import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.TestUtils;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static com.applitools.eyes.selenium.TestDataProvider.*;

@Test()
public class MobileNativeTests {

    private void setCapabilities(Eyes eyes, DesiredCapabilities capabilities, String methodName) {
        capabilities.setCapability("username", SAUCE_USERNAME);
        capabilities.setCapability("accesskey", SAUCE_ACCESS_KEY);
        capabilities.setCapability("name", methodName);
    }

    private Eyes initEyes(DesiredCapabilities capabilities) {
        Eyes eyes = new Eyes();
        String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
        TestUtils.setupLogging(eyes, testName);
        setCapabilities(eyes, capabilities, testName);
        eyes.setBatch(TestDataProvider.batchInfo);
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

        capabilities.setCapability("app", "https://applitools.bintray.com/Examples/app-debug.apk");

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
    public void iOSNativeAppTest() throws Exception {

        DesiredCapabilities caps = DesiredCapabilities.iphone();
        caps.setCapability("appiumVersion", "1.13.0");
        caps.setCapability("deviceName", "iPhone XS Simulator");
        caps.setCapability("deviceOrientation", "portrait");
        caps.setCapability("platformVersion", "12.2");
        caps.setCapability("platformName", "iOS");
        caps.setCapability("browserName", "");
        caps.setCapability("app", "https://applitools.bintray.com/Examples/HelloWorldiOS_1_0.zip");

        Eyes eyes = initEyes(caps);
        eyes.setLogHandler(new StdoutLogHandler());
        eyes.setBatch(TestDataProvider.batchInfo);


        WebDriver driver = null;
        try {
            driver = new IOSDriver(new URL(SAUCE_SELENIUM_URL), caps);
            eyes.open(driver, "Mobile Native Tests", "iOS Native App");
            eyes.checkWindow("checkWindow");
            eyes.close();
        } finally {
            if (driver != null) {
                driver.quit();
            }

            eyes.abortIfNotClosed();
        }
    }
}
