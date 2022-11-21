package com.applitools.eyes.appium.android;

import com.applitools.eyes.appium.TestSetup;
import io.appium.java_client.android.AndroidDriver;

import java.net.MalformedURLException;
import java.net.URL;

public abstract class AndroidTestSetup extends TestSetup {

    @Override
    public void setCapabilities() {
        super.setCapabilities();
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("appium:deviceName", "Google Pixel 5 GoogleAPI Emulator");
        capabilities.setCapability("appium:platformVersion", "11.0");
        capabilities.setCapability("appium:automationName", "UiAutomator2");
        capabilities.setCapability("appium:newCommandTimeout", 2000);
    }

    @Override
    protected void initDriver() throws MalformedURLException {
        System.out.println("starting android driver");
        System.out.println("capabilities: " + capabilities);
        System.out.println("url: " + SL_URL);
        driver = new AndroidDriver(new URL(SL_URL), capabilities);
        System.out.println("android driver started successfully");
    }

    @Override
    protected void setAppCapability() {
        capabilities.setCapability("appium:app", "https://applitools.jfrog.io/artifactory/Examples/androidx/helper_lib/1.8.6/app-androidx-debug.apk");
    }

    @Override
    protected String getApplicationName() {
        return "Java Appium - Android";
    }
}