package com.applitools.eyes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sun.javafx.beans.IDProperty;

import java.util.Arrays;
import java.util.List;

public class AccessibilityRegionByRectangle implements IGetAccessibilityRegion{
    @JsonInclude
    private int left;
    @JsonInclude
    private int top;
    @JsonInclude
    private int width;
    @JsonInclude
    private int height;
    @JsonInclude
    private AccessibilityRegionType type;

    @JsonIgnore
    private Region region;

    public AccessibilityRegionByRectangle() {
    }

    public AccessibilityRegionByRectangle(int left, int top, int width, int height, AccessibilityRegionType regionType) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
        this.type = regionType;
    }

    public AccessibilityRegionByRectangle(Region region, AccessibilityRegionType regionType) {
        this.type = regionType;
        this.setRegion(region);
    }

    public Region getRegion() {
        return new Region(left, top, width, height);
    }

    public void setRegion(Region region) {
        this.region = region;
        this.left = region.getLeft();
        this.top = region.getTop();
        this.width = region.getWidth();
        this.height = region.getHeight();
    }


    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public AccessibilityRegionType getType() {
        return type;
    }

    @Override
    public List<AccessibilityRegionByRectangle> getRegions(IDriverProvider eyesBase, EyesScreenshot screenshot) {
        return Arrays.asList(this);
    }

    @Override
    public String toString() {
        return "AccessibilityRegionByRectangle{" +
                "left=" + left +
                ", top=" + top +
                ", width=" + width +
                ", height=" + height +
                ", type=" + type +
                '}';
    }

    @Override
    public int hashCode() {
        return left * 30000 + top * 2000 + width * 500 + height;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (!(other instanceof AccessibilityRegionByRectangle)) {
            return false;
        }
        AccessibilityRegionByRectangle otherRegion = (AccessibilityRegionByRectangle) other;

        boolean result =
                otherRegion.width == width &&
                        otherRegion.height == height &&
                        otherRegion.left == left &&
                        otherRegion.top == top &&
                        otherRegion.type == type;
        return result;
    }
}
