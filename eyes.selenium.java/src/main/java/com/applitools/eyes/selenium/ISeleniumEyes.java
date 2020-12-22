package com.applitools.eyes.selenium;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.visualgrid.services.IEyes;
import org.openqa.selenium.WebDriver;

public interface ISeleniumEyes extends IEyes {

    WebDriver open(WebDriver webDriver);

    WebDriver open(WebDriver driver, String appName, String testName, RectangleSize viewportSize);

    WebDriver getDriver();
}
