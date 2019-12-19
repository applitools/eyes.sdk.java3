package com.applitools.eyes.selenium;

import org.openqa.selenium.ScreenOrientation;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

public class TestMobileDevicesAndroid extends TestMobileDevices {
    @Factory(dataProvider = "androidDevices")
    public TestMobileDevicesAndroid(String deviceName, String platformVersion, ScreenOrientation deviceOrientation, boolean fully, String page) {
        super(deviceName, platformVersion, deviceOrientation, fully, page);
    }

    @Test
    public void TestAndroid_SauceLabs(){
        initEyes(deviceName, platformVersion, deviceOrientation, fully, "Android", "Chrome", page);
    }
}
