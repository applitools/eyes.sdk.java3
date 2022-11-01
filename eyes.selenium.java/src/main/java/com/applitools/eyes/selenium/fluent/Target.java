package com.applitools.eyes.selenium.fluent;

import com.applitools.eyes.Region;
import com.applitools.eyes.TargetPath;
import com.applitools.eyes.selenium.TargetPathLocator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByAll;
import org.openqa.selenium.support.pagefactory.ByChained;

public class Target {

    public static SeleniumCheckSettings window()
    {
        return new SeleniumCheckSettings();
    }

    // targetRegion
    public static SeleniumCheckSettings region(Region region)
    {
        return new SeleniumCheckSettings(region);
    }


    // region locator
    public static SeleniumCheckSettings region(By by) {
        return new SeleniumCheckSettings(TargetPath.region(by));
    }

    // region locator
    public static SeleniumCheckSettings region(ByAll byAll) {
        return new SeleniumCheckSettings(TargetPath.region(byAll));
    }

    // region locator
    public static SeleniumCheckSettings region(ByChained byChained) {
        return new SeleniumCheckSettings(TargetPath.region(byChained));
    }

    // region locator
    public static SeleniumCheckSettings region(WebElement webElement) {
        return new SeleniumCheckSettings(TargetPath.region(webElement));
    }

    // direct implementation
    public static SeleniumCheckSettings region(TargetPathLocator targetPathLocator)
    {
        return new SeleniumCheckSettings(targetPathLocator);
    }


    public static SeleniumCheckSettings frame(By by)
    {
        SeleniumCheckSettings settings = new SeleniumCheckSettings();
        settings = settings.frame(by);
        return settings;
    }

    public static SeleniumCheckSettings frame(String frameNameOrId)
    {
        SeleniumCheckSettings settings = new SeleniumCheckSettings();
        settings = settings.frame(frameNameOrId);
        return settings;
    }

    public static SeleniumCheckSettings frame(int index)
    {
        SeleniumCheckSettings settings = new SeleniumCheckSettings();
        settings = settings.frame(index);
        return settings;
    }

    public static SeleniumCheckSettings frame(WebElement webElement)
    {
        SeleniumCheckSettings settings = new SeleniumCheckSettings();
        settings = settings.frame(webElement);
        return settings;
    }
}
