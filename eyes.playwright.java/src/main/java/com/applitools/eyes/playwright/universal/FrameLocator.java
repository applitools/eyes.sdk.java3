package com.applitools.eyes.playwright.universal;

import com.applitools.eyes.playwright.serializer.FrameSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.microsoft.playwright.Frame;

public class FrameLocator {
    private String frameLocator;
    private Integer frameIndex;
    @JsonSerialize(using = FrameSerializer.class)
    private Frame frameReference;

    public Integer getFrameIndex() {
        return frameIndex;
    }

    public String getFrameLocator() {
        return frameLocator;
    }

    public Frame getFrameReference() {
        return frameReference;
    }

    public void setFrameLocator(String frameLocator) {
        this.frameLocator = frameLocator;
    }

    public void setFrameIndex(int frameIndex) {
        this.frameIndex = frameIndex;
    }

    public void setFrameReference(Frame frameReference){
        this.frameReference = frameReference;
    }

}
