package com.applitools.eyes.selenium.fluent;

import com.applitools.eyes.selenium.TargetPathLocator;

import java.util.List;

public interface ISeleniumCheckTarget extends IScrollRootElementContainer {
//    By getTargetSelector();
//    WebElement getTargetElement();
    TargetPathLocator getTargetPathLocator();
    List<FrameLocator> getFrameChain();
}
