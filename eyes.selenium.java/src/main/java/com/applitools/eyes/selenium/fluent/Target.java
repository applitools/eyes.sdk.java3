package com.applitools.eyes.selenium.fluent;

import com.applitools.eyes.Region;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class Target {

    public static SeleniumCheckSettings window()
    {
        return new SeleniumCheckSettings();
    }

    public static SeleniumCheckSettings region(Region region)
    {
        return new SeleniumCheckSettings(region);
    }

    public static SeleniumCheckSettings region(By by)
    {
        return new SeleniumCheckSettings(by);
    }

    public static SeleniumCheckSettings region(WebElement webElement)
    {
        return new SeleniumCheckSettings(webElement);
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
