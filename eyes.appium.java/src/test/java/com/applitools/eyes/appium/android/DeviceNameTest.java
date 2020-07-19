package com.applitools.eyes.appium.android;

import org.testng.annotations.Test;

// TODO: change to unit test
public class DeviceNameTest extends AndroidTestSetup {

    @Test
    public void testDeviceName() {
        eyes.setScrollToRegion(false);
        eyes.open(driver, getApplicationName(), "Test device name");
        eyes.checkWindow();
        eyes.close();
    }
}
