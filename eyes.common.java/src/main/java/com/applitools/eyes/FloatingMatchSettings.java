package com.applitools.eyes;

public class FloatingMatchSettings {
    public int top;
    public int left;
    public int width;
    public int height;

    public int maxUpOffset;
    public int maxDownOffset;
    public int maxLeftOffset;
    public int maxRightOffset;

    public FloatingMatchSettings(int left, int top, int width, int height, int maxUpOffset, int maxDownOffset, int maxLeftOffset, int maxRightOffset) {
        this.top = top;
        this.left = left;
        this.width = width;
        this.height = height;

        this.maxUpOffset = maxUpOffset;
        this.maxDownOffset = maxDownOffset;
        this.maxLeftOffset = maxLeftOffset;
        this.maxRightOffset = maxRightOffset;
    }
}
