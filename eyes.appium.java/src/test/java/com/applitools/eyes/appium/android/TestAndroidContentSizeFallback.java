package com.applitools.eyes.appium.android;

import com.applitools.eyes.appium.Eyes;
import com.applitools.eyes.appium.Target;
import com.applitools.utils.GeneralUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class TestAndroidContentSizeFallback {

    final static String USERNAME = GeneralUtils.getEnvString("SAUCE_USERNAME");
    final static String ACCESS_KEY = GeneralUtils.getEnvString("SAUCE_ACCESS_KEY");
    final static String SL_URL = "https://"+USERNAME+":" + ACCESS_KEY + "@ondemand.us-west-1.saucelabs.com:443/wd/hub";

    private Eyes eyes;
    private AndroidDriver driver;

    @BeforeTest
    public void setup() throws MalformedURLException {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("deviceName","Google Pixel 5 GoogleAPI Emulator");
        caps.setCapability("deviceOrientation", "portrait");
        caps.setCapability("platformVersion","11.0");
        caps.setCapability("platformName", "Android");
        caps.setCapability("automationName", "UiAutomator2");
        caps.setCapability("app", "/Users/idos/Downloads/app-androidx-debug_v4.16.2_no_helper.apk");
        caps.setCapability("noReset", true);
        caps.setCapability("fullReset", false);
        caps.setCapability("newCommandTimeout", 2000);

        driver = new AndroidDriver(new URL("http://0.0.0.0:4723/wd/hub"), caps);

        eyes = new Eyes();
    }

    @AfterTest
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void test() throws InterruptedException {
        driver.findElement(AppiumBy.id("btn_recycler_view_nested_collapsing")).click();

        Thread.sleep(1000);

        eyes.open(driver, "test type error", "test type error");
        eyes.check(Target.region(AppiumBy.id("card_view")).scrollRootElement(AppiumBy.id("recyclerView")).fully());
        eyes.close(false);
    }
}
