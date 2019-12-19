package com.applitools.eyes.selenium;

import org.openqa.selenium.ScreenOrientation;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

public class TestMobileDevicesIOS extends TestMobileDevices {
    @Factory(dataProvider = "IOSDevices")
    public TestMobileDevicesIOS(String deviceName, String platformVersion, ScreenOrientation deviceOrientation, boolean fully, String page) {
        super(deviceName, platformVersion, deviceOrientation, fully, page);
    }

    @Test
    public void TestIOSSafariCrop_SauceLabs(){
        initEyes(deviceName, platformVersion, deviceOrientation, fully, "iOS", "Safari", this.page);
    }
}