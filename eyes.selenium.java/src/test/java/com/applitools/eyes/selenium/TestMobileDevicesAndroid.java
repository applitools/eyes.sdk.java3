package com.applitools.eyes.selenium;

import org.openqa.selenium.ScreenOrientation;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

public class TestMobileDevicesAndroid extends TestMobileDevices {
    @Factory(dataProvider = "androidDevices")
    public TestMobileDevicesAndroid(String deviceName, String platformVersion, ScreenOrientation deviceOrientation, String page) {
        super(deviceName, platformVersion, deviceOrientation, page);
    }

    @Test
    public void TestAndroidStitch(){
        initEyes(deviceName, platformVersion, deviceOrientation, "Android", "Chrome", page);
    }
}
