package com.applitools.eyes.selenium.fluent;

import com.applitools.eyes.selenium.ScrollRootElementContainer;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public interface SeleniumCheckTarget extends ScrollRootElementContainer {
    By getTargetSelector();
    WebElement getTargetElement();
    List<FrameLocator> getFrameChain();
}
