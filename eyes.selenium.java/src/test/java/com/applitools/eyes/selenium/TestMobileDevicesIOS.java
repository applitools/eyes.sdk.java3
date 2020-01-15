package com.applitools.eyes.selenium;

import com.applitools.eyes.utils.TestUtils;
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
        List<Object[]> devices = new ArrayList<Object[]>(Arrays.asList(new Object[][]{
//                {"iPad Pro (9.7 inch) Simulator", "12.0", ScreenOrientation.LANDSCAPE, false},
//                {"iPhone XR Simulator", "12.2", ScreenOrientation.PORTRAIT, true}
                {"iPad Pro (10.5 inch) Simulator", "11.0", ScreenOrientation.LANDSCAPE, true}
        }));
        if (TestUtils.runOnCI) {
            devices.addAll(Arrays.asList(new Object[][]{
                    {"iPad Air 2 Simulator", "10.3", ScreenOrientation.LANDSCAPE, true},
                    {"iPad Air 2 Simulator", "12.0", ScreenOrientation.LANDSCAPE, true},
                    {"iPad Air 2 Simulator", "11.3", ScreenOrientation.LANDSCAPE, true},
                    {"iPad Pro (12.9 inch) (2nd generation) Simulator", "11.0", ScreenOrientation.LANDSCAPE, true},
                    {"iPad Pro (10.5 inch) Simulator", "11.0", ScreenOrientation.LANDSCAPE, true},
                    {"iPad (5th generation) Simulator", "11.0", ScreenOrientation.PORTRAIT, true},
                    {"iPad Air 2 Simulator", "10.3", ScreenOrientation.PORTRAIT, true},
                    {"iPad Air 2 Simulator", "11.0", ScreenOrientation.PORTRAIT, true},
                    {"iPad Pro (12.9 inch) (2nd generation) Simulator", "11.0", ScreenOrientation.PORTRAIT, true},
                    {"iPad Pro (10.5 inch) Simulator", "11.0", ScreenOrientation.PORTRAIT, true},
                    {"iPhone XS Simulator", "12.2", ScreenOrientation.LANDSCAPE, true},
                    {"iPhone 11 Pro Simulator", "13.0", ScreenOrientation.LANDSCAPE, true},
                    {"iPhone XS Max Simulator", "12.2", ScreenOrientation.LANDSCAPE, true},
                    {"iPhone 11 Pro Max Simulator", "13.0", ScreenOrientation.LANDSCAPE, true},
                    {"iPhone XR Simulator", "12.2", ScreenOrientation.LANDSCAPE, true},
                    {"iPhone 11 Simulator", "13.0", ScreenOrientation.LANDSCAPE, true},
                    {"iPhone 6 Plus Simulator", "11.0", ScreenOrientation.LANDSCAPE, true},
                    {"iPhone 7 Simulator", "10.3", ScreenOrientation.LANDSCAPE, true},
                    {"iPhone 7 Plus Simulator", "10.3", ScreenOrientation.LANDSCAPE, true},
                    {"iPhone 5s Simulator", "10.3", ScreenOrientation.LANDSCAPE, true},
                    {"iPhone XS Simulator", "12.2", ScreenOrientation.PORTRAIT, true},
                    {"iPhone XS Max Simulator", "12.2", ScreenOrientation.PORTRAIT, true},
                    {"iPhone XR Simulator", "12.2", ScreenOrientation.PORTRAIT, true},
                    {"iPhone 6 Plus Simulator", "11.0", ScreenOrientation.PORTRAIT, true},
                    {"iPhone 7 Simulator", "10.3", ScreenOrientation.PORTRAIT, true},
                    {"iPhone 5s Simulator", "10.3", ScreenOrientation.PORTRAIT, true}
            }));
        }
        devices = addPageType(devices);
        return devices.toArray(new Object[0][]);
    }

    @Factory(dataProvider = "IOSDevices")
    public TestMobileDevicesIOS(String deviceName, String platformVersion, ScreenOrientation deviceOrientation, boolean fully, String page) {
        super(deviceName, platformVersion, deviceOrientation, fully, page);
    }

    @Test
    public void TestIOSSafariCrop_SauceLabs(){
        initEyes(deviceName, platformVersion, deviceOrientation, fully, "iOS", "Safari", this.page);
    }
}