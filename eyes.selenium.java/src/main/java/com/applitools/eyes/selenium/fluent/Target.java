package com.applitools.eyes.selenium.fluent;

import com.applitools.eyes.Region;
import com.applitools.eyes.TargetPath;
import com.applitools.eyes.selenium.TargetPathLocator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class Target {

    /**
     * Specify the target as window.
     *
     * @return the check settings
     */
    public static SeleniumCheckSettings window()
    {
        return new SeleniumCheckSettings();
    }

    /**
     * Specify the target as a region.
     *
     * @param region the region to capture.
     * @return the check settings
     */
    public static SeleniumCheckSettings region(Region region)
    {
        return new SeleniumCheckSettings(region);
    }

    /**
     * Specify the target as a region.
     *
     * @param by the By selector of the region to capture.
     * @return the check settings
     */
    public static SeleniumCheckSettings region(By by) {
        return new SeleniumCheckSettings(TargetPath.region(by));
    }

    /**
     * Specify the target as a region.
     *
     * @param webElement the WebElement of the region to capture.
     * @return the check settings
     */
    public static SeleniumCheckSettings region(WebElement webElement) {
        return new SeleniumCheckSettings(TargetPath.region(webElement));
    }

    /**
     * Specify the target as a region. (direct implementation)
     *
     * @param targetPathLocator the target path locator to capture.
     * @return the check settings
     */
    public static SeleniumCheckSettings region(TargetPathLocator targetPathLocator)
    {
        return new SeleniumCheckSettings(targetPathLocator);
    }


    /**
     * Specify the target as a frame.
     *
     * @param by the By selector of the frame.
     * @return the check settings
     */
    public static SeleniumCheckSettings frame(By by)
    {
        SeleniumCheckSettings settings = new SeleniumCheckSettings();
        settings = settings.frame(by);
        return settings;
    }

    /**
     * Specify the target as a frame.
     *
     * @param frameNameOrId the frame's name or id.
     * @return the check settings
     */
    public static SeleniumCheckSettings frame(String frameNameOrId)
    {
        SeleniumCheckSettings settings = new SeleniumCheckSettings();
        settings = settings.frame(frameNameOrId);
        return settings;
    }

    /**
     * Specify the target as a frame.
     *
     * @param index the index of the frame.
     * @return the check settings
     */
    public static SeleniumCheckSettings frame(int index)
    {
        SeleniumCheckSettings settings = new SeleniumCheckSettings();
        settings = settings.frame(index);
        return settings;
    }

    /**
     * Specify the target as a frame.
     *
     * @param webElement WebElement representation of the frame.
     * @return the check settings
     */
    public static SeleniumCheckSettings frame(WebElement webElement)
    {
        SeleniumCheckSettings settings = new SeleniumCheckSettings();
        settings = settings.frame(webElement);
        return settings;
    }
}
