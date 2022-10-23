package com.applitools.eyes.appium.android;

import com.applitools.eyes.LazyLoadOptions;
import com.applitools.eyes.appium.Target;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Arrays;

public class AndroidLazyLoadTest extends AndroidTestSetup {

    public static final String USERNAME = System.getenv("SAUCE_USERNAME");
    public static final String ACCESS_KEY = System.getenv("SAUCE_ACCESS_KEY");
    public static final String SL_URL = "https://"+USERNAME+":" + ACCESS_KEY + "@ondemand.saucelabs.com:443/wd/hub";
    public static final int PIXEL_5_OFFSET = 13049;
    public static final int PIXEL_3XL_OFFSET = 13601;


    @Override
    protected void initDriver() throws MalformedURLException {
        capabilities = new DesiredCapabilities();
        capabilities.setCapability("deviceName","Google Pixel 5 GoogleAPI Emulator");
        capabilities.setCapability("deviceOrientation", "portrait");
        capabilities.setCapability("platformVersion","11.0");
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("automationName", "UiAutomator2");
        capabilities.setCapability("app", "https://applitools.jfrog.io/artifactory/Examples/androidx/helper_lib/1.8.6/app-androidx-debug.apk");
        capabilities.setCapability("noReset", false);
        capabilities.setCapability("fullReset", true);
        capabilities.setCapability("newCommandTimeout", 2000);

        driver = new AndroidDriver(new URL(SL_URL), capabilities);
    }

    @Test
    public void testHelperLibOffsetCalculation() throws InterruptedException {
        gestureScrollAndClick();
        String fieldCommand = "offset_async;recycler_view;0;0;0;5000";

        WebElement edt = null;
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(50);
                edt = driver.findElement(By.xpath("//*[@content-desc=\"EyesAppiumHelperEDT\"]"));
                break;
            } catch (Exception e) {
                if(++i >= 3)
                    throw e;
            }
        }

        edt.sendKeys(fieldCommand);
        edt.click();

        do { Thread.sleep(1000); }
        while (edt.getText().equals("WAIT"));

        String value = edt.getText().split(";", 2)[0];
        driver.navigate().back();

        Assert.assertEquals(Integer.parseInt(value), PIXEL_5_OFFSET);

    }

    @Test
    public void testLazyLoad() {
        gestureScrollAndClick();
        eyes.open(driver, getApplicationName(), "Check LazyLoad");
        LazyLoadOptions lazyLoadOptions = new LazyLoadOptions().waitingTime(-PIXEL_5_OFFSET);
        eyes.check(Target.window().fully().lazyLoad(lazyLoadOptions).withName("lazyLoad"));
        eyes.close();
    }

    private void gestureScrollAndClick() {
        // create new pointer action
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        // create new sequence of actions
        Sequence dragNDrop = new Sequence(finger, 1);

        // add pointer movement
        dragNDrop.addAction(finger.createPointerMove(Duration.ofMillis(0),
                PointerInput.Origin.viewport(), 5, 1000));
        // pointer down - left click
        dragNDrop.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        // add pointer movement
        dragNDrop.addAction(finger.createPointerMove(Duration.ofMillis(700),
                PointerInput.Origin.viewport(),5, 100));
        // pointer up - release left click
        dragNDrop.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Arrays.asList(dragNDrop));

        driver.findElement(AppiumBy.id("btn_large_recyclerView_activity")).click();
    }

}
