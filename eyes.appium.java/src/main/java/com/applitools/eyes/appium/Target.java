package com.applitools.eyes.appium;

import com.applitools.eyes.Region;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class Target {
    public static AppiumCheckSettings window() {
        return new AppiumCheckSettings();
    }

    public static AppiumCheckSettings region(Region region) {
        return new AppiumCheckSettings(region);
    }

    public static AppiumCheckSettings region(By by) {
        return new AppiumCheckSettings(by);
    }

    public static AppiumCheckSettings region(WebElement webElement) {
        return new AppiumCheckSettings(webElement);
    }

    public static AppiumCheckSettings webview() {
        AppiumCheckSettings settings = new AppiumCheckSettings();
        settings = settings.webview(true);
        return settings;
    }

    public static AppiumCheckSettings webview(Boolean isWebView) {
        AppiumCheckSettings settings = new AppiumCheckSettings();
        settings = settings.webview(isWebView);
        return settings;
    }

    public static AppiumCheckSettings webview(String webView) {
        AppiumCheckSettings settings = new AppiumCheckSettings();
        settings = settings.webview(webView);
        return settings;
    }
}
