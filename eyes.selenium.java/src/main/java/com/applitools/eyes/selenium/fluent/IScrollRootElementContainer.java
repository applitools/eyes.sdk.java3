package com.applitools.eyes.selenium.fluent;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public interface IScrollRootElementContainer {
    WebElement getScrollRootElement();
    By getScrollRootSelector();
}
