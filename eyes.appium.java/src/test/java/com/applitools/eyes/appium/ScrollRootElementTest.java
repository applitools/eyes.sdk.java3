package com.applitools.eyes.appium;

import com.applitools.eyes.StdoutLogHandler;
import com.applitools.utils.GeneralUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.MobileBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class ScrollRootElementTest {
    final static String USERNAME = GeneralUtils.getEnvString("SAUCE_USERNAME");
    final static String ACCESS_KEY = GeneralUtils.getEnvString("SAUCE_ACCESS_KEY");
    final static String SL_URL = "https://"+USERNAME+":" + ACCESS_KEY + "@ondemand.us-west-1.saucelabs.com:443/wd/hub";

    @Test
    public void test() throws InterruptedException, MalformedURLException {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("deviceName","Google Pixel 5 GoogleAPI Emulator");
        caps.setCapability("deviceOrientation", "portrait");
        caps.setCapability("platformVersion","11.0");
        caps.setCapability("platformName", "Android");
        caps.setCapability("automationName", "UiAutomator2");
        caps.setCapability("app", "https://applitools.jfrog.io/artifactory/Examples/androidx/1.3.6/app_androidx.apk");
        caps.setCapability("newCommandTimeout", 2000);
        caps.setCapability("appiumVersion", "1.22.0");
        caps.setCapability("name", "Pixel 5 (java) - ScrollRootElement");

        AndroidDriver driver = new AndroidDriver(new URL(SL_URL), caps);
        Eyes eyes = new Eyes();
        eyes.setLogHandler(new StdoutLogHandler(true));
        try {
            eyes.open(driver,"Android Test","ScrollRootElement test");
            //driver.findElementById("btn_recycler_view_in_scroll_view_activity").click();
            driver.findElement(AppiumBy.id("btn_recycler_view_in_scroll_view_activity")).click();
            Thread.sleep(1000);
            eyes.check(Target.window().scrollRootElement(AppiumBy.id("recyclerView")).fully().timeout(0));
            eyes.close();
        } finally {
            driver.quit();
            eyes.abortIfNotClosed();
        }
    }

}
