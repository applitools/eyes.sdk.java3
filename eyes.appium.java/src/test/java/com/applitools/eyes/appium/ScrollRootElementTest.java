package com.applitools.eyes.appium;

import com.applitools.eyes.StdoutLogHandler;
import io.appium.java_client.MobileBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class ScrollRootElementTest {
    public static final String USERNAME = "applitools-dev";
    public static final String ACCESS_KEY = "7f853c17-24c9-4d8f-a679-9cfde5b43951";
    public static final String URL0 = "https://"+USERNAME+":" + ACCESS_KEY + "@ondemand.saucelabs.com:443/wd/hub";

    @Test
    public void test() throws InterruptedException, MalformedURLException {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("deviceName","Google Pixel 5 GoogleAPI Emulator");
        caps.setCapability("deviceOrientation", "portrait");
        caps.setCapability("platformVersion","11.0");
        caps.setCapability("platformName", "Android");
        caps.setCapability("automationName", "UiAutomator2");
//        caps.setCapability("app", "https://applitools.jfrog.io/artifactory/Examples/androidx/helper_lib/1.8.4/app-androidx-debug.apk");
        caps.setCapability("app", "https://applitools.jfrog.io/artifactory/Examples/androidx/1.3.6/app_androidx.apk");
        caps.setCapability("newCommandTimeout", 2000);
        AndroidDriver driver = new AndroidDriver(new URL(URL0), caps);
        Eyes eyes = new Eyes();
        eyes.setLogHandler(new StdoutLogHandler(true));
        try {
            eyes.open(driver,"Android Test","ScrollRootElement test");
            driver.findElementById("btn_recycler_view_in_scroll_view_activity").click();
            Thread.sleep(1000);
            eyes.check(Target.window().scrollRootElement(MobileBy.id("recyclerView")).fully().timeout(0));
            eyes.close();
        } finally {
            driver.quit();
            eyes.abortIfNotClosed();
        }
    }

}
