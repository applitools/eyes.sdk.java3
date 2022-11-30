package com.applitools.eyes.appium.android;

import com.applitools.eyes.appium.Target;
import io.appium.java_client.AppiumBy;
import org.testng.annotations.Test;

public class TestAndroidContentSizeFallback extends AndroidTestSetup {

    @Test
    public void shouldNotFailOnTypeErrorWhenNoHelperLib() throws InterruptedException {
        driver.findElement(AppiumBy.id("btn_recycler_view_nested_collapsing")).click();
        Thread.sleep(3000);

        eyes.open(driver, getApplicationName(), "shouldNotFailOnTypeErrorWhenNoHelperLib");
        eyes.check(Target.region(AppiumBy.id("card_view")).scrollRootElement(AppiumBy.id("recyclerView")).fully(false));
        eyes.close(false);
    }

    @Override
    protected void setAppCapability() {
        // app-androidx-debug_v4.16.2_no_helper.apk from Sauce storage
        capabilities.setCapability("appium:app", "https://applitools.jfrog.io/artifactory/Examples/androidx/no_helper_lib/4.16.2/app.apk");
    }

    @Override
    protected void setDeviceCapability() {
        capabilities.setCapability("appium:deviceName","Google Pixel 3a XL GoogleAPI Emulator");
    }

    @Override
    protected void setPlatformVersionCapability() {
        capabilities.setCapability("appium:platformVersion","10.0");
    }
}