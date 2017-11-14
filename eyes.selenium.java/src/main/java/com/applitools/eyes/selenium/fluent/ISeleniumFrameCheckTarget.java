package com.applitools.eyes.selenium.fluent;

import org.openqa.selenium.By;

public interface ISeleniumFrameCheckTarget {
    Integer getFrameIndex();
    String getFrameNameOrId();
    By getFrameSelector();
}
