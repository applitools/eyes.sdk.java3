package com.applitools.eyes.appium.android;

import com.applitools.eyes.appium.Eyes;
import com.applitools.eyes.appium.Target;
import com.applitools.utils.GeneralUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.*;

import java.net.MalformedURLException;
import java.net.URL;

public class TestAndroidContentSizeFallback {

    final static String USERNAME = GeneralUtils.getEnvString("SAUCE_USERNAME");
    final static String ACCESS_KEY = GeneralUtils.getEnvString("SAUCE_ACCESS_KEY");
    final static String SL_URL = "https://"+USERNAME+":" + ACCESS_KEY + "@ondemand.us-west-1.saucelabs.com:443/wd/hub";

    private Eyes eyes;
    private AndroidDriver driver;

    @BeforeMethod
    public void setup() throws MalformedURLException {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("deviceName","Google Pixel 3a XL GoogleAPI Emulator");
        caps.setCapability("deviceOrientation", "portrait");
        caps.setCapability("platformVersion","10.0");
        caps.setCapability("extendedDebugging", true);
        caps.setCapability("platformName", "Android");
        caps.setCapability("automationName", "UiAutomator2");
        caps.setCapability("app", "storage:0743f3dd-abd2-4efc-a249-aee12e083c7f"); // app-androidx-debug_v4.16.2_no_helper.apk from Sauce storage
        caps.setCapability("appiumVersion", "1.22.0");
        caps.setCapability("name", "Pixel 3a XL (java) - 'type' error");
        caps.setCapability("newCommandTimeout", 2000);

        driver = new AndroidDriver(new URL(SL_URL), caps);

        eyes = new Eyes();
        eyes.setApiKey(GeneralUtils.getEnvString("APPLITOOLS_API_KEY"));
    }

    @AfterMethod
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void shouldNotFailOnTypeErrorWhenNoHelperLib() throws InterruptedException {
        driver.findElement(AppiumBy.id("btn_recycler_view_nested_collapsing")).click();

        Thread.sleep(3000);

        eyes.open(driver, "Java Appium - Android", "shouldNotFailOnTypeErrorWhenNoHelperLib");
        eyes.check(Target.region(AppiumBy.id("card_view")).scrollRootElement(AppiumBy.id("recyclerView")).fully(false));
        eyes.close(false);
    }
}
