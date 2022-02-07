package com.applitools.eyes.appium.capture;

import com.applitools.eyes.appium.EyesAppiumDriver;
import com.applitools.eyes.capture.ImageProvider;

public abstract class MobileImageProvider implements ImageProvider {

    protected boolean captureStatusBar = false;
    protected Boolean wasCapturedStatusBar = null;

    protected final EyesAppiumDriver driver;

    public MobileImageProvider(EyesAppiumDriver driver) {
        this.driver = driver;
    }

    public void setCaptureStatusBar(boolean captureStatusBar) {
        this.captureStatusBar = captureStatusBar;
        if (wasCapturedStatusBar == null) {
            wasCapturedStatusBar = captureStatusBar;
        }
    }

    public boolean wasCaptureStatusBar() {
        return wasCapturedStatusBar;
    }

    public void reset() {
        wasCapturedStatusBar = null;
    }
}
