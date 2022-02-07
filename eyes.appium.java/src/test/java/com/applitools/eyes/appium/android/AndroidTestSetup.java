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
        capabilities.setCapability("deviceName", "Google Pixel 2");
        capabilities.setCapability("os_version", "9.0");
        capabilities.setCapability("automationName", "UiAutomator2");
        capabilities.setCapability("newCommandTimeout", 300);
    }

    @Override
    protected void initDriver() throws MalformedURLException {
        driver = new AndroidDriver<>(new URL(appiumServerUrl), capabilities);
    }

    @Override
    protected void setAppCapability() {
        // To run locally use https://applitools.jfrog.io/artifactory/Examples/android/1.2/app_android.apk
        capabilities.setCapability("app", "app_android");
    }

    @Override
    protected String getApplicationName() {
        return "Java Appium - Android";
    }
}
