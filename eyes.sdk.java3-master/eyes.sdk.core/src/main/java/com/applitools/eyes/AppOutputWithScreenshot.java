/*
 * Applitools SDK for Selenium integration.
 */
package com.applitools.eyes;

/**
 * A container for a AppOutput along with the screenshot used for
 * creating it. (We specifically avoid inheritance so we don't have to deal
 * with serialization issues).
 */
public class AppOutputWithScreenshot {
    private final AppOutput appOutput;
    private final EyesScreenshot screenshot;

    public AppOutputWithScreenshot(AppOutput appOutput,
            EyesScreenshot screenshot) {
        this.appOutput = appOutput;
        this.screenshot = screenshot;
    }

    public AppOutput getAppOutput() {
        return appOutput;
    }

    public EyesScreenshot getScreenshot() {
        return screenshot;
    }
}
