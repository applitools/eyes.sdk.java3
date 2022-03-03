package com.applitools.eyes.selenium.fluent;

import com.applitools.eyes.selenium.CheckState;
import com.applitools.eyes.selenium.TargetPathLocator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public interface ISeleniumCheckTarget extends IScrollRootElementContainer, ImplicitInitiation {
//    By getTargetSelector();
//    WebElement getTargetElement();
    TargetPathLocator getTargetPathLocator();
    List<FrameLocator> getFrameChain();
    CheckState getState();
    void setState(CheckState state);
}
