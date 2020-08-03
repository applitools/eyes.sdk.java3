package com.applitools.eyes.selenium;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.positioning.PositionProvider;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;

public class CheckState {
    private WebElement targetElementInternal;
    private boolean stitchContent;
    private PositionProvider stitchPositionProvider;
    private Rectangle effectiveViewport;
    private Rectangle fullRegion;
    private PositionProvider originPositionProvider = new NullPositionProvider();
    private RectangleSize stitchOffset;

    public WebElement getTargetElementInternal() {
        return targetElementInternal;
    }

    public void setTargetElementInternal(WebElement targetElementInternal) {
        this.targetElementInternal = targetElementInternal;
    }

    public boolean isStitchContent() {
        return stitchContent;
    }

    public void setStitchContent(boolean stitchContent) {
        this.stitchContent = stitchContent;
    }

    public PositionProvider getStitchPositionProvider() {
        return stitchPositionProvider;
    }

    public void setStitchPositionProvider(PositionProvider stitchPositionProvider) {
        this.stitchPositionProvider = stitchPositionProvider;
    }

    public Rectangle getEffectiveViewport() {
        return effectiveViewport;
    }

    public void setEffectiveViewport(Rectangle effectiveViewport) {
        this.effectiveViewport = effectiveViewport;
    }

    public Rectangle getFullRegion() {
        return fullRegion;
    }

    public void setFullRegion(Rectangle fullRegion) {
        this.fullRegion = fullRegion;
    }

    public PositionProvider getOriginPositionProvider() {
        return originPositionProvider;
    }

    public void setOriginPositionProvider(PositionProvider originPositionProvider) {
        this.originPositionProvider = originPositionProvider;
    }

    public RectangleSize getStitchOffset() {
        return stitchOffset;
    }

    public void setStitchOffset(RectangleSize stitchOffset) {
        this.stitchOffset = stitchOffset;
    }
}
