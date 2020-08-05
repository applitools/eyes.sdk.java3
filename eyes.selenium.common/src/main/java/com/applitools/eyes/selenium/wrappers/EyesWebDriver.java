package com.applitools.eyes.selenium.wrappers;

import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.List;

public interface EyesWebDriver extends WebDriver, JavascriptExecutor, TakesScreenshot, SearchContext, HasCapabilities {
    RemoteWebDriver getRemoteWebDriver();
    WebElement findElement(By by);
    List<WebElement> findElements(By by);
}
