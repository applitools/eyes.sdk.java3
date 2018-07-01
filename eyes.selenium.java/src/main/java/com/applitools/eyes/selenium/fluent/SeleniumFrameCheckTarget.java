package com.applitools.eyes.selenium.fluent;

import com.applitools.eyes.selenium.ScrollRootElementContainer;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public interface SeleniumFrameCheckTarget extends ScrollRootElementContainer {
    Integer getFrameIndex();
    String getFrameNameOrId();
    By getFrameSelector();
    WebElement getFrameReference();
}
