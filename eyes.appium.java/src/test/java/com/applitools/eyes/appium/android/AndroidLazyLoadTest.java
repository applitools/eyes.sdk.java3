package com.applitools.eyes.appium.android;

import com.applitools.eyes.LazyLoadOptions;
import com.applitools.eyes.appium.Target;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

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
        capabilities.setCapability("app", "https://applitools.jfrog.io/artifactory/Examples/androidx/helper_lib/1.8.4/app-androidx-debug.apk");
        capabilities.setCapability("newCommandTimeout", 2000);

        driver = new AndroidDriver<>(new URL(SL_URL), capabilities);
    }

    @Test
    public void testLazyLoad() {
        eyes.open(driver, getApplicationName(), "Check LazyLoad");
        scrollAndClickBtn();
        LazyLoadOptions lazyLoadOptions = new LazyLoadOptions().pageHeight(30000).waitingTime(3500);
        eyes.check(Target.window().fully().waitBeforeCapture(3500).lazyLoad(lazyLoadOptions).withName("lazyLoad"));
        eyes.close();

    }

    public void scrollAndClickBtn() {
        TouchAction scrollAction = new TouchAction(driver);

        scrollAction.press(new PointOption().withCoordinates(5, 1000)).waitAction(new WaitOptions().withDuration(Duration.ofMillis(1500)));
        scrollAction.moveTo(new PointOption().withCoordinates(5, 100));
        scrollAction.cancel();
        driver.performTouchAction(scrollAction);
        driver.findElementById("btn_large_recyclerView_activity").click();
    }
}
