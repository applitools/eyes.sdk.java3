package com.applitools.eyes.selenium;

import com.applitools.eyes.Location;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.Region;
import com.applitools.eyes.positioning.PositionProvider;
import org.openqa.selenium.WebElement;

public class CheckState {
    private WebElement targetElementInternal;
    private boolean stitchContent;
    private PositionProvider stitchPositionProvider;
    private Region effectiveViewport = Region.EMPTY;
    private Region fullRegion = Region.EMPTY;
    private PositionProvider originPositionProvider = new NullPositionProvider();
    private RectangleSize stitchOffset = RectangleSize.EMPTY;
    private Location originalLocation;

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

    public Region getEffectiveViewport() {
        return effectiveViewport;
    }

    public void setEffectiveViewport(Region effectiveViewport) {
        this.effectiveViewport = effectiveViewport;
    }

    public Region getFullRegion() {
        return fullRegion;
    }

    public void setFullRegion(Region fullRegion) {
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

    public Location getOriginalLocation() {
        return originalLocation;
    }

    public void setOriginalLocation(Location originalLocation) {
        this.originalLocation = originalLocation;
    }
}
