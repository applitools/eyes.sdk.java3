package com.applitools.eyes.appium.android;

import com.applitools.eyes.LazyLoadOptions;
import com.applitools.eyes.appium.Target;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class AndroidLazyLoadTest extends AndroidTestSetup {

    public static final String USERNAME = "applitools-dev";
    public static final String ACCESS_KEY = "7f853c17-24c9-4d8f-a679-9cfde5b43951";
    public static final String SL_URL = "https://"+USERNAME+":" + ACCESS_KEY + "@ondemand.saucelabs.com:443/wd/hub";

    @Override
    protected void initDriver() throws MalformedURLException {
        capabilities = new DesiredCapabilities();
        capabilities.setCapability("deviceName","Google Pixel 5 GoogleAPI Emulator");
        capabilities.setCapability("deviceOrientation", "portrait");
        capabilities.setCapability("platformVersion","11.0");
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("automationName", "UiAutomator2");
        capabilities.setCapability("app", "https://applitools.jfrog.io/artifactory/Examples/androidx/1.3.6/app_androidx.apk");
        capabilities.setCapability("newCommandTimeout", 2000);

        driver = new AndroidDriver<>(new URL(SL_URL), capabilities);
    }

    @Test
    public void testLazyLoad() {

        eyes.open(driver, getApplicationName(), "Check LazyLoad");
        eyes.check(Target.window().lazyLoad());
        eyes.close();
    }

    @Test
    public void testLazyLoadWithDefaultOptions() {

        eyes.open(driver, getApplicationName(), "Check LazyLoad LazyLoadOptions");
        eyes.check(Target.window().lazyLoad(new LazyLoadOptions()));
        eyes.close();
    }

    @Test
    public void testLazyLoadWithCustomOptions() {

        eyes.open(driver, getApplicationName(), "Check LazyLoad Custom LazyLoadOptions");
        eyes.check(Target.window().lazyLoad(new LazyLoadOptions().pageHeight(111).scrollLength(100).waitingTime(1000)));
        eyes.close();
    }
}
