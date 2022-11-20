package com.applitools.eyes.appium.android;

import com.applitools.eyes.appium.Target;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.MutableCapabilities;
import org.testng.annotations.Test;

public class TestAndroidContentSizeFallback extends AndroidTestSetup {

    @Override
    public void setCapabilities() {
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("deviceName","Google Pixel 3a XL GoogleAPI Emulator");
        capabilities.setCapability("platformVersion","10.0");
        capabilities.setCapability("deviceOrientation", "portrait");
        capabilities.setCapability("extendedDebugging", true);
        capabilities.setCapability("automationName", "UiAutomator2");
        capabilities.setCapability("name", "Pixel 3a XL (java) - 'type' error");
        setAppCapability();

        MutableCapabilities sauceOptions = new MutableCapabilities();
        sauceOptions.setCapability("appiumVersion", "1.22.1");
        sauceOptions.setCapability("name", "Java - Android");
        capabilities.setCapability("sauce:options", sauceOptions);
    }

    @Override
    protected void setAppCapability() {
        // app-androidx-debug_v4.16.2_no_helper.apk from Sauce storage
        capabilities.setCapability("app", "storage:0743f3dd-abd2-4efc-a249-aee12e083c7f");
    }

    @Test
    public void shouldNotFailOnTypeErrorWhenNoHelperLib() throws InterruptedException {
        driver.findElement(AppiumBy.id("btn_recycler_view_nested_collapsing")).click();

        Thread.sleep(3000);

        eyes.open(driver, getApplicationName(), "shouldNotFailOnTypeErrorWhenNoHelperLib");
        eyes.check(Target.region(AppiumBy.id("card_view")).scrollRootElement(AppiumBy.id("recyclerView")).fully(false));
        eyes.close(false);
    }
}
