package com.applitools.eyes.visualgrid.model;

import com.applitools.eyes.RectangleSize;

public class DeviceSize {
    private RectangleSize portrait;
    private RectangleSize landscapeLeft;
    private RectangleSize landscapeRight;

    public RectangleSize getPortrait() {
        return portrait;
    }

    public void setPortrait(RectangleSize portrait) {
        this.portrait = portrait;
    }

    public RectangleSize getLandscapeLeft() {
        return landscapeLeft;
    }

    public void setLandscapeLeft(RectangleSize landscapeLeft) {
        this.landscapeLeft = landscapeLeft;
    }

    public RectangleSize getLandscapeRight() {
        return landscapeRight;
    }

    public void setLandscapeRight(RectangleSize landscapeRight) {
        this.landscapeRight = landscapeRight;
    }
}
