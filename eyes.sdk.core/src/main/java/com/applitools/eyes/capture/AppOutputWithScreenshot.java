/*
 * Applitools SDK for Selenium integration.
 */
package com.applitools.eyes.capture;

import com.applitools.eyes.AppOutput;
import com.applitools.eyes.EyesScreenshot;
import com.applitools.eyes.Location;
import com.applitools.eyes.fluent.ICheckSettingsInternal;

/**
 * A container for a AppOutput along with the screenshot used for creating it.
 * (We specifically avoid inheritance so we don't have to deal with serialization issues).
 */
public class AppOutputWithScreenshot {
    private final AppOutput appOutput;
    private final EyesScreenshot screenshot;
    private final Location location;

    public AppOutputWithScreenshot(AppOutput appOutput, EyesScreenshot screenshot, Location location) {
        this.appOutput = appOutput;
        this.screenshot = screenshot;
        this.location = location;
    }

    public AppOutput getAppOutput() {
        return appOutput;
    }

    public EyesScreenshot getScreenshot() {
        return screenshot;
    }

    public Location getLocation() {
        return location;
    }
}
