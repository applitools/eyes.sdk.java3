package com.applitools.eyes.selenium.config;

import com.applitools.eyes.selenium.StitchMode;

public class Configuration extends com.applitools.eyes.config.Configuration {
    private static final int DEFAULT_WAIT_BEFORE_SCREENSHOTS = 100;

    private boolean forceFullPageScreenshot;
    private int waitBeforeScreenshots = DEFAULT_WAIT_BEFORE_SCREENSHOTS;;
    private StitchMode stitchMode= StitchMode.SCROLL;;
    private boolean hideScrollbars = true;
    private boolean hideCaret = true;

    public boolean getForceFullPageScreenshot() {
        return forceFullPageScreenshot;
    }

    public void setForceFullPageScreenshot(boolean forceFullPageScreenshot) {
        this.forceFullPageScreenshot = forceFullPageScreenshot;
    }

    public int getWaitBeforeScreenshots() {
        return waitBeforeScreenshots;
    }

    public void setWaitBeforeScreenshots(int waitBeforeScreenshots) {
        if (waitBeforeScreenshots <= 0) {
            this.waitBeforeScreenshots = DEFAULT_WAIT_BEFORE_SCREENSHOTS;
        } else {
            this.waitBeforeScreenshots = waitBeforeScreenshots;
        }
    }

    public StitchMode getStitchMode() {
        return stitchMode;
    }

    public void setStitchMode(StitchMode stitchMode) {
        this.stitchMode = stitchMode;
    }

    public boolean getHideScrollbars() {
        return hideScrollbars;
    }

    public void setHideScrollbars(boolean hideScrollbars) {
        this.hideScrollbars = hideScrollbars;
    }

    public boolean getHideCaret() {
        return hideCaret;
    }

    public void setHideCaret(boolean hideCaret) {
        this.hideCaret = hideCaret;
    }
}
