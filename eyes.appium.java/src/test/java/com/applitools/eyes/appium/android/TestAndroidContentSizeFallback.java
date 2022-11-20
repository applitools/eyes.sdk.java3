package com.applitools.eyes.appium.android;

import com.applitools.eyes.appium.Target;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.MutableCapabilities;
import org.testng.annotations.Test;

public class TestAndroidContentSizeFallback extends AndroidTestSetup {

    @Override
    public void setCapabilities() {
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("appium:platformVersion","10.0");
        capabilities.setCapability("appium:deviceName","Google Pixel 3a XL GoogleAPI Emulator");
        capabilities.setCapability("appium:automationName", "UiAutomator2");
        capabilities.setCapability("appium:newCommandTimeout", 2000);
        setAppCapability();

        MutableCapabilities sauceOptions = new MutableCapabilities();
        sauceOptions.setCapability("appiumVersion", "1.22.1");
        sauceOptions.setCapability("deviceOrientation", "PORTRAIT");
        sauceOptions.setCapability("name", "Java - Android");
        sauceOptions.setCapability("idleTimeout", 300);
        capabilities.setCapability("sauce:options", sauceOptions);
    }

    @Override
    protected void setAppCapability() {
        // app-androidx-debug_v4.16.2_no_helper.apk from Sauce storage
        capabilities.setCapability("appium:app", "storage:filename=app-androidx-debug_v4.16.2_no_helper.apk");
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
