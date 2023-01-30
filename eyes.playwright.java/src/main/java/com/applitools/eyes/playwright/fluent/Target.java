package com.applitools.eyes.playwright.fluent;

import com.applitools.eyes.Region;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Locator;

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
        return new PlaywrightCheckSettings().region(region);
    }

    /**
     * Specify the target as a region.
     *
     * @param selector the locator.
     * @return the check settings
     */
    public static PlaywrightCheckSettings region(String selector) {
        return new PlaywrightCheckSettings().region(selector);
    }

    /**
     * Specify the target as a region.
     *
     * @param locator the locator.
     * @return the check settings
     */
    public static PlaywrightCheckSettings region(Locator locator) {
        return new PlaywrightCheckSettings().region(locator);
    }

    /**
     * Specify the target as a region.
     *
     * @param element  the element.
     * @return the check settings
     */
    public static PlaywrightCheckSettings region(ElementHandle element) {
        return new PlaywrightCheckSettings().region(element);
    }

    /**
     * Specify the target as a frame.
     *
     * @param frameNameOrId the locator of the frame.
     * @return the check settings
     */
    public static PlaywrightCheckSettings frame(String frameNameOrId) {
        return new PlaywrightCheckSettings().frame(frameNameOrId);
    }

    /**
     * Specify the target as a frame.
     *
     * @param frameIndex  the index of the frame.
     * @return the check settings
     */
    public static PlaywrightCheckSettings frame(Integer frameIndex) {
        return new PlaywrightCheckSettings().frame(frameIndex);
    }

    /**
     * Specify the target as a frame.
     *
     * @param locator the frame locator.
     * @return the check settings
     */
    public static PlaywrightCheckSettings frame(Locator locator) {
        return new PlaywrightCheckSettings().frame(locator);
    }

    /**
     * Specify the target as a frame.
     *
     * @param frameElement  the frame element.
     * @return the check settings
     */
    public static PlaywrightCheckSettings frame(ElementHandle frameElement) {
        return new PlaywrightCheckSettings().frame(frameElement);
    }
}
