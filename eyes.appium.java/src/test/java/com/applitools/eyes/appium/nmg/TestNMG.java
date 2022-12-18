package com.applitools.eyes.appium.nmg;

import com.applitools.eyes.appium.Eyes;
import com.applitools.eyes.visualgrid.model.*;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import io.appium.java_client.android.AndroidDriver;
import org.testng.annotations.Test;

public class TestNMG {

    private Eyes eyes;
    private AndroidDriver driver;

    @Test
    public void test() {
        eyes = new Eyes(new VisualGridRunner());

        eyes.setConfiguration(eyes.getConfiguration()
                .addMobileDevice(new IosDeviceInfo(IosDeviceName.iPhone_7))
                .addMobileDevice(new AndroidDeviceInfo(AndroidDeviceName.Galaxy_Note_9))
                );
    }
}
