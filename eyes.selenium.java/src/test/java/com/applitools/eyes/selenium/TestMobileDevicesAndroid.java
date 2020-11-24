package com.applitools.eyes.selenium;

import org.openqa.selenium.ScreenOrientation;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

public class TestMobileDevicesAndroid extends TestMobileDevices {

    @DataProvider(name = "androidDevices", parallel = true)
    public static Object[][] androidDevices() {
        List<Object[]> devices = Arrays.asList(new Object[][]{
                {"Android Emulator", "8.0", ScreenOrientation.PORTRAIT},
                {"Android Emulator", "8.0", ScreenOrientation.LANDSCAPE}
        });
        devices = addPageType(devices);
        return devices.toArray(new Object[0][]);
    }

    @Factory(dataProvider = "androidDevices")
    public TestMobileDevicesAndroid(String deviceName, String platformVersion, ScreenOrientation screenOrientation, String page) {
        super(deviceName, platformVersion, screenOrientation, page);
        super.addSuiteArg("deviceName", deviceName);
        super.addSuiteArg("platformVersion", platformVersion);
        super.addSuiteArg("screenOrientation", screenOrientation);
        super.addSuiteArg("page", page);
    }

    @Test
    public void TestAndroidStitch(){
        initEyes(deviceName, platformVersion, deviceOrientation, "Android", "Chrome", page);
    }
}
