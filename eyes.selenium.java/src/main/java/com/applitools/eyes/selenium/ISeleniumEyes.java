package com.applitools.eyes.selenium;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResults;
import org.openqa.selenium.WebDriver;

public interface ISeleniumEyes extends IEyes {
    WebDriver open(WebDriver webDriver);
    WebDriver open(WebDriver driver, String appName, String testName, RectangleSize viewportSize);
    TestResults close(boolean throwEx);
    WebDriver getDriver();
    void serverUrl(String serverUrl);
    void apiKey(String apiKey);
}
