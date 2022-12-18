package com.applitools.eyes.appium.android;

import com.applitools.eyes.appium.Target;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class TestNestedScrollView extends AndroidTestSetup {

    @Test
    public void testNestedScrollView() throws InterruptedException {
        driver.findElement(AppiumBy.id("com.applitools.app_androidx:id/btn_recycler_view_nested_collapsing")).click();
        Thread.sleep(1000);

        eyes.open(driver,getApplicationName(),"TestNestedScrollView");
        eyes.check(Target.window().scrollRootElement(AppiumBy.id("nested_scroll_view")).fully());
        eyes.close();
    }

    @Override
    protected void setAppCapability() {
        capabilities.setCapability("appium:app", "/Users/idos/Downloads/app-androidx-debug_helper_v1.8.9.apk");
    }

    @Override
    public void setCapabilities() {
        super.setCapabilities();
        capabilities.setCapability("appium:settings[allowInvisibleElements]", true);
    }

    @Override
    protected void initDriver() throws MalformedURLException {
        driver = new AndroidDriver(new URL("http://0.0.0.0:4723/wd/hub"), capabilities);
    }
}
