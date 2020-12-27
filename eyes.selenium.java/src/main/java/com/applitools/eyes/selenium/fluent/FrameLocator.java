package com.applitools.eyes.selenium.fluent;

import com.applitools.eyes.serializers.BySerializer;
import com.applitools.eyes.serializers.WebElementSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FrameLocator implements ISeleniumFrameCheckTarget {
    @JsonSerialize(using = BySerializer.class)
    private By frameSelector;
    private String frameNameOrId;
    private Integer frameIndex;
    @JsonSerialize(using = WebElementSerializer.class)
    private WebElement frameReference;
    @JsonSerialize(using = BySerializer.class)
    private By scrollRootSelector;
    @JsonSerialize(using = WebElementSerializer.class)
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

    public void setScrollRootElement(WebElement scrollRootElement) {
        this.scrollRootElement = scrollRootElement;
    }

    public void setScrollRootSelector(By scrollRootSelector) {
        this.scrollRootSelector = scrollRootSelector;
    }

    @Override
    public WebElement getScrollRootElement() {
        return scrollRootElement;
    }

    @Override
    public By getScrollRootSelector() {
        return scrollRootSelector;
    }
}