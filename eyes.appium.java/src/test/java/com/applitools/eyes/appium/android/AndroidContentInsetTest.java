package com.applitools.eyes.appium.android;

import com.applitools.eyes.appium.Target;
import com.applitools.eyes.config.ContentInset;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class AndroidContentInsetTest extends AndroidTestSetup {
    public static final String USERNAME = System.getenv("APPLITOOLS_SAUCE_LABS_USER");
    public static final String ACCESS_KEY = System.getenv("APPLITOOLS_SAUCE_LABS_KEY");
    public static final String URL = "https://"+USERNAME+":" + ACCESS_KEY + "@ondemand.saucelabs.com:443/wd/hub";

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

        driver = new AndroidDriver<>(new URL(URL), capabilities);
    }

    @Test
    public void testDefault() {

        eyes.getConfiguration().setContentInset(new ContentInset());

        eyes.open(driver, getApplicationName(), "Check Default ContentInset");
        eyes.check(Target.window());
        eyes.close();
    }

    @Test
    public void testAssigned() {

        eyes.getConfiguration().setContentInset(new ContentInset(10, 20, 30, 30));

        eyes.open(driver, getApplicationName(), "Check Assigned ContentInset");
        eyes.check(Target.window());
        eyes.close();
    }
}
