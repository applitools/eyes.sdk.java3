package com.applitools.eyes.appium.android;

import com.applitools.eyes.appium.TestSetup;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Arrays;

public abstract class AndroidTestSetup extends TestSetup {

    @Override
    public void setCapabilities() {
        super.setCapabilities();
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("appium:automationName", "UiAutomator2");
        capabilities.setCapability("appium:newCommandTimeout", 2000);
    }

    @Override
    protected void initDriver() throws MalformedURLException {
        driver = new AndroidDriver(new URL(SL_URL), capabilities);
    }

    @Override
    protected void setAppCapability() {
        capabilities.setCapability("appium:app", "https://applitools.jfrog.io/artifactory/Examples/androidx/helper_lib/1.8.6/app-androidx-debug.apk");
    }

    @Override
    protected void setDeviceCapability() {
        capabilities.setCapability("appium:deviceName", "Google Pixel 5 GoogleAPI Emulator");
    }

    @Override
    protected void setPlatformVersionCapability() {
        capabilities.setCapability("appium:platformVersion", "11.0");
    }

    @Override
    protected String getApplicationName() {
        return "Java Appium - Android";
    }

    public void gestureScrollAndClick() {
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