package com.applitools.eyes.selenium.fluent;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public interface ISeleniumCheckTarget {
    By getTargetSelector();
    WebElement getTargetElement();
    List<FrameLocator> getFrameChain();
}
