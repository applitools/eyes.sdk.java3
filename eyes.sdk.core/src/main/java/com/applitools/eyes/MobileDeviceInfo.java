package com.applitools.eyes;

public class MobileDeviceInfo {
    private double pixelRatio;
    private Region viewportRect;

    public double getPixelRatio() {
        return pixelRatio;
    }

    public void setPixelRatio(double pixelRatio) {
        this.pixelRatio = pixelRatio;
    }

    public Region getViewportRect() {
        return viewportRect;
    }

    public void setViewportRect(Region viewportRect) {
        this.viewportRect = viewportRect;
    }
}
