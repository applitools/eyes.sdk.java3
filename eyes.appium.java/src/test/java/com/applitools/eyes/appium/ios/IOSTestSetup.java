package com.applitools.eyes.appium.ios;

import com.applitools.eyes.appium.TestSetup;
import io.appium.java_client.ios.IOSDriver;

import java.net.MalformedURLException;
import java.net.URL;

public abstract class IOSTestSetup extends TestSetup {

    @Override
    protected void setCapabilities() {
        super.setCapabilities();
        capabilities.setCapability("platformName", "iOS");
        capabilities.setCapability("device", "iPhone 12");
        capabilities.setCapability("os_version", "14");
        capabilities.setCapability("automationName", "XCUITest");
        capabilities.setCapability("newCommandTimeout", 300);
        capabilities.setCapability("fullReset", false);
    }

    @Override
    protected void initDriver() throws MalformedURLException {
        driver = new IOSDriver(new URL(BS_URL), capabilities);
    }

    @Override
    protected void setAppCapability() {
        capabilities.setCapability("app", "app_ios");
    }

    @Override
    protected String getApplicationName() {
        return "Java Appium - IOS";
    }
}
