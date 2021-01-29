package com.applitools.eyes.appium.android;

import com.applitools.eyes.appium.TestSetup;
import io.appium.java_client.android.AndroidDriver;

import java.net.MalformedURLException;
import java.net.URL;

public abstract class AndroidTestSetup extends TestSetup {

    protected static final String BASIC_APP = "https://applitools.bintray.com/Examples/android/1.3/app-debug.apk";
    protected static final String ANDROIDX_APP = "https://applitools.bintray.com/Examples/androidx/1.2.0/app_androidx.apk";

    @Override
    public void setCapabilities() {
        super.setCapabilities();
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("deviceName", "Samsung Galaxy S9 WQHD GoogleAPI Emulator");
        capabilities.setCapability("platformVersion", "9.0");
        capabilities.setCapability("automationName", "UiAutomator2");
        capabilities.setCapability("newCommandTimeout", 300);
    }

    @Override
    protected void initDriver() throws MalformedURLException {
        driver = new AndroidDriver<>(new URL(SAUCE_URL), capabilities);
    }

    @Override
    protected void setAppCapability() {
        // To run locally use https://applitools.bintray.com/Examples/android/1.2/app_android.apk
        capabilities.setCapability("app", BASIC_APP);
    }

    @Override
    protected String getApplicationName() {
        return "Java Appium - Android";
    }
}
