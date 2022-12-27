package com.applitools.eyes.playwright;

import com.applitools.eyes.Region;
import com.microsoft.playwright.Frame;

public class Target {

    /**
     * Specify the target as window.
     *
     * @return the check settings
     */
    public static PlaywrightCheckSettings window()
    {
        return new PlaywrightCheckSettings();
    }

    /**
     * Specify the target as a region.
     *
     * @param region the region to capture.
     * @return the check settings
     */
    public static PlaywrightCheckSettings region(Region region) {
        return new PlaywrightCheckSettings(region);
    }

    /**
     * Specify the target as a locator.
     *
     * @param locator the locator.
     * @return the check settings
     */
    public static PlaywrightCheckSettings region(String locator) {
        return new PlaywrightCheckSettings(locator);
    }

    /**
     * Specify the target as a frame.
     *
     * @param locator the locator of the frame.
     * @return the check settings
     */
    public static PlaywrightCheckSettings frame(String locator) {
        PlaywrightCheckSettings settings = new PlaywrightCheckSettings();
        settings = settings.frame(locator);
        return settings;
    }

    /**
     * Specify the target as a frame.
     *
     * @param index the index of the frame.
     * @return the check settings
     */
    public static PlaywrightCheckSettings frame(int index) {
        PlaywrightCheckSettings settings = new PlaywrightCheckSettings();
        settings = settings.frame(index);
        return settings;
    }

    /**
     * Specify the target as a frame.
     *
     * @param frame the frame.
     * @return the check settings
     */
    public static PlaywrightCheckSettings frame(Frame frame) {
        PlaywrightCheckSettings settings = new PlaywrightCheckSettings();
        settings = settings.frame(frame);
        return settings;
    }
}
