package com.applitools.eyes.appium.android;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.appium.Eyes;
import com.applitools.eyes.appium.Target;
import com.applitools.utils.GeneralUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.bys.builder.ByAll;
import io.appium.java_client.pagefactory.bys.builder.ByChained;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class AndroidTestByAll {

    final static String USERNAME = GeneralUtils.getEnvString("SAUCE_USERNAME");
    final static String ACCESS_KEY = GeneralUtils.getEnvString("SAUCE_ACCESS_KEY");
    final static String SL_URL = "https://"+USERNAME+":" + ACCESS_KEY + "@ondemand.us-west-1.saucelabs.com:443/wd/hub";

    private AndroidDriver driver;
    private Eyes eyes;

    @BeforeTest
    public void before() throws MalformedURLException {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("deviceName","Google Pixel 5 GoogleAPI Emulator");
        caps.setCapability("deviceOrientation", "portrait");
        caps.setCapability("platformVersion","11.0");
        caps.setCapability("platformName", "Android");
        caps.setCapability("automationName", "UiAutomator2");
        caps.setCapability("app", "https://applitools.jfrog.io/artifactory/Examples/androidx/1.3.6/app_androidx.apk");
        caps.setCapability("newCommandTimeout", 2000);

        driver = new AndroidDriver(new URL(SL_URL), caps);

        eyes = new Eyes();
        eyes.setLogHandler(new StdoutLogHandler(true));
        eyes.setBatch(new BatchInfo("Android ByAll ByChained"));
    }

    @AfterTest
    public void teardown() {
        if (driver != null)
            driver.quit();

        eyes.abortIfNotClosed();
    }

    @Test
    public void testAppiumByAll() {
        eyes.open(driver,"Android Test","TestAppiumByAll");

        ByAll byAll = new ByAll(
                new By[]{
                        AppiumBy.xpath("some_xpath"),
                        AppiumBy.id("btn_recycler_view_in_scroll_view_activity"),
                        AppiumBy.androidUIAutomator("some_ui_automator_text")
                }
        );

        eyes.check(Target.region(byAll).fully(false));
        eyes.close();
    }

    @Test
    public void testAppiumByChained() {
        eyes.open(driver,"Android Test","TestAppiumByChained");

        ByChained byChained = new ByChained(
                new By[]{
                        AppiumBy.id("android:id/content"),
                        AppiumBy.id("com.applitools.app_androidx:id/btn_web_view_dialog_activity"),
                }
        );

        eyes.check(Target.region(byChained));
        eyes.close();
    }
}
