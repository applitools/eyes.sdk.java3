package com.applitools.eyes.appium.android;

import com.applitools.eyes.LazyLoadOptions;
import com.applitools.eyes.appium.Target;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

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

        driver = new AndroidDriver<>(new URL(SL_URL), capabilities);
    }

    @Test
    public void testHelperLibOffsetCalculation() throws InterruptedException {
        scrollAndClickBtn();
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
        scrollAndClickBtn();
        eyes.open(driver, getApplicationName(), "Check LazyLoad");
        LazyLoadOptions lazyLoadOptions = new LazyLoadOptions().waitingTime(-PIXEL_5_OFFSET);
        eyes.check(Target.window().fully().lazyLoad(lazyLoadOptions).withName("lazyLoad"));
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
