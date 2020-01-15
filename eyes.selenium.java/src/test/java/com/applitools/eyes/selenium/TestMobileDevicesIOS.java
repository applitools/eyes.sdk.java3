package com.applitools.eyes.selenium;

import org.openqa.selenium.ScreenOrientation;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestMobileDevicesIOS extends TestMobileDevices {

    @DataProvider(name = "IOSDevices", parallel = true)
    public static Object[][] IOSDevices() {
        List<Object[]> devices = new ArrayList<>(Arrays.asList(new Object[][]{
                {"iPad Air 2 Simulator", "10.3", ScreenOrientation.LANDSCAPE},
                {"iPad Air 2 Simulator", "12.0", ScreenOrientation.LANDSCAPE},
                {"iPad Air 2 Simulator", "11.3", ScreenOrientation.LANDSCAPE},
                {"iPad Pro (12.9 inch) (2nd generation) Simulator", "11.0", ScreenOrientation.LANDSCAPE},
                {"iPad Pro (10.5 inch) Simulator", "11.0", ScreenOrientation.LANDSCAPE},
                {"iPad (5th generation) Simulator", "11.0", ScreenOrientation.PORTRAIT},
                {"iPad Air 2 Simulator", "10.3", ScreenOrientation.PORTRAIT},
                {"iPad Air 2 Simulator", "11.0", ScreenOrientation.PORTRAIT},
                {"iPad Pro (12.9 inch) (2nd generation) Simulator", "11.0", ScreenOrientation.PORTRAIT},
                {"iPad Pro (10.5 inch) Simulator", "11.0", ScreenOrientation.PORTRAIT},
                {"iPhone XS Simulator", "12.2", ScreenOrientation.LANDSCAPE},
                {"iPhone 11 Pro Simulator", "13.0", ScreenOrientation.LANDSCAPE},
                {"iPhone XS Max Simulator", "12.2", ScreenOrientation.LANDSCAPE},
                {"iPhone 11 Pro Max Simulator", "13.0", ScreenOrientation.LANDSCAPE},
                {"iPhone XR Simulator", "12.2", ScreenOrientation.LANDSCAPE},
                {"iPhone 11 Simulator", "13.0", ScreenOrientation.LANDSCAPE},
                {"iPhone 6 Plus Simulator", "11.0", ScreenOrientation.LANDSCAPE},
                {"iPhone 7 Simulator", "10.3", ScreenOrientation.LANDSCAPE},
                {"iPhone 7 Plus Simulator", "10.3", ScreenOrientation.LANDSCAPE},
                {"iPhone 5s Simulator", "10.3", ScreenOrientation.LANDSCAPE},
                {"iPhone XS Simulator", "12.2", ScreenOrientation.PORTRAIT},
                {"iPhone XS Max Simulator", "12.2", ScreenOrientation.PORTRAIT},
                {"iPhone XR Simulator", "12.2", ScreenOrientation.PORTRAIT},
                {"iPhone 6 Plus Simulator", "11.0", ScreenOrientation.PORTRAIT},
                {"iPhone 7 Simulator", "10.3", ScreenOrientation.PORTRAIT},
                {"iPhone 5s Simulator", "10.3", ScreenOrientation.PORTRAIT}
        }));
        devices = addPageType(devices);
        return devices.toArray(new Object[0][]);
    }

    @Factory(dataProvider = "IOSDevices")
    public TestMobileDevicesIOS(String deviceName, String platformVersion, ScreenOrientation deviceOrientation, String page) {
        super(deviceName, platformVersion, deviceOrientation, page);
    }

    @Test
    public void TestIOSSafariStitch() {
        initEyes(deviceName, platformVersion, deviceOrientation, "iOS", "Safari", this.page);
    }
}