package com.applitools.eyes.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.List;

public interface EyesWebDriver {
    RemoteWebDriver getRemoteWebDriver();
    WebElement findElement(By by);
    List<WebElement> findElements(By by);
}
