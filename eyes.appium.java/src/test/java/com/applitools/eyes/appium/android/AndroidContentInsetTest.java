package com.applitools.eyes.appium.android;

import com.applitools.eyes.appium.Target;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.config.ContentInset;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class AndroidContentInsetTest extends AndroidTestSetup {
    public static final String USERNAME = System.getenv("SAUCE_USERNAME");
    public static final String ACCESS_KEY = System.getenv("SAUCE_ACCESS_KEY");
    public static final String SL_URL = "https://"+USERNAME+":" + ACCESS_KEY + "@ondemand.saucelabs.com:443/wd/hub";

    @Override
    protected void initDriver() throws MalformedURLException {
        capabilities = new DesiredCapabilities();
        capabilities.setCapability("deviceName","Google Pixel 5 GoogleAPI Emulator");
        capabilities.setCapability("deviceOrientation", "portrait");
        capabilities.setCapability("platformVersion","11.0");
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("automationName", "UiAutomator2");
        capabilities.setCapability("app", "https://applitools.jfrog.io/artifactory/Examples/androidx/helper_lib/1.8.4/app-androidx-debug.apk");
        capabilities.setCapability("newCommandTimeout", 2000);

        driver = new AndroidDriver<>(new URL(SL_URL), capabilities);
    }

    @Test
    public void testContentInset() {

        Configuration config = eyes.getConfiguration();
        config.setContentInset(new ContentInset(30, 0, 20, 0));
        eyes.setConfiguration(config);

        eyes.open(driver, getApplicationName(), "Check ContentInset");
        eyes.check(Target.window().fully(false));
        eyes.close();
    }
}
