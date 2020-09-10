package com.applitools.eyes;

public class MobileDeviceInfo {
    private double pixelRatio;
    private Region viewportRect;
    private String[] aliases;

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

    public String[] getAliases() {
        return aliases;
    }

    public void setAliases(String[] aliases) {
        this.aliases = aliases;
    }
}
