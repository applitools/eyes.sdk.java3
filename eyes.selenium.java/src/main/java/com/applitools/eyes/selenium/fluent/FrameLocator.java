package com.applitools.eyes.selenium.fluent;

import org.openqa.selenium.By;

public class FrameLocator implements ISeleniumFrameCheckTarget{
    private By frameSelector;
    private String frameNameOrId;
    private Integer frameIndex;

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

    public void setFrameSelector(By frameSelector) {
        this.frameSelector = frameSelector;
    }

    public void setFrameNameOrId(String frameNameOrId) {
        this.frameNameOrId = frameNameOrId;
    }

    public void setFrameIndex(int frameIndex) {
        this.frameIndex = frameIndex;
    }
}
