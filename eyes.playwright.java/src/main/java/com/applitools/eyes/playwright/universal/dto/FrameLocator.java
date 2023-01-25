package com.applitools.eyes.playwright.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FrameLocator {

    private String frameNameOrId;
    private Integer frameIndex;
    private Element frameElement;
    private Selector frameSelector;
    private Selector scrollRootSelector;
    private Element scrollRootElement;

    public Integer getFrameIndex() {
        return this.frameIndex;
    }

    public String getFrameNameOrId() {
        return frameNameOrId;
    }

    public void setFrameNameOrId(String frameNameOrId) {
        this.frameNameOrId = frameNameOrId;
    }

    public void setFrameIndex(Integer frameIndex) {
        this.frameIndex = frameIndex;
    }

    public Element getFrameElement() {
        return frameElement;
    }

    public void setFrameElement(Element frameElement) {
        this.frameElement = frameElement;
    }

    public Selector getFrameSelector() {
        return frameSelector;
    }

    public void setFrameSelector(Selector frameSelector) {
        this.frameSelector = frameSelector;
    }

    public Selector getScrollRootSelector() {
        return scrollRootSelector;
    }

    public void setScrollRootSelector(Selector scrollRootSelector) {
        this.scrollRootSelector = scrollRootSelector;
    }

    public Element getScrollRootElement() {
        return scrollRootElement;
    }

    public void setScrollRootElement(Element scrollRootElement) {
        this.scrollRootElement = scrollRootElement;
    }
}
