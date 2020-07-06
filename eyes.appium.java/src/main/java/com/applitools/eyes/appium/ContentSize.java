package com.applitools.eyes.appium;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;

public class ContentSize {

    public int height;
    public int width;
    public int top;
    public int left;
    public int scrollableOffset;
    private AppiumDriver driver;
    public int touchPadding;

    public String toString() {
        return String.format("{height=%s, width=%s, top=%s, left=%s, scrollableOffset=%s, touchPadding=%s}",
            height, width, top, left, scrollableOffset, touchPadding);
    }

    public void setDriver (AppiumDriver driver) {
        this.driver = driver;
    }

    public int getScrollContentHeight() {
        // for android contentSizes, scrollable offset is truly the offset, so to get the entire
        // content height we need to do some addition
        if (driver != null && driver instanceof AndroidDriver) {
            return scrollableOffset + height;
        }

        // for ios, scrollableOffset is already the content height
        return scrollableOffset;
    }

}
