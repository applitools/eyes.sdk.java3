package com.applitools.eyes.selenium.fluent;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class FrameLocator implements SeleniumFrameCheckTarget {
    private String frameNameOrId;
    private Integer frameIndex;
    private By frameSelector;
    private WebElement frameReference;
    private By scrollRootSelector;
    private WebElement scrollRootElement;

    @Override
    public Integer getFrameIndex() {
        return this.frameIndex;
    }

    @Override
    public String getFrameNameOrId() {
        return this.frameNameOrId;
    }

    @Override
    public By getFrameSelector() {
        return this.frameSelector;
    }

    @Override
    public WebElement getFrameReference() {
        return this.frameReference;
    }

    @Override
    public By getScrollRootSelector() {
        return scrollRootSelector;
    }

    @Override
    public WebElement getScrollRootElement() {
        return scrollRootElement;
    }

    public void setFrameSelector(By frameSelector) {
        this.frameSelector = frameSelector;
    }

    public void setFrameNameOrId(String frameNameOrId) {
        this.frameNameOrId = frameNameOrId;
    }

    public void setFrameIndex(int frameIndex) {
        this.frameIndex = frameIndex;
    }

    public void setFrameReference(WebElement frameReference){
        this.frameReference = frameReference;
    }

    public void setScrollRootSelector(By scrollRootSelector) {
        this.scrollRootSelector = scrollRootSelector;
    }

    public void setScrollRootElement(WebElement scrollRootElement) {
        this.scrollRootElement = scrollRootElement;
    }
}
