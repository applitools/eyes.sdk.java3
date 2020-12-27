package com.applitools.eyes.appium;

import java.util.Map;

public class LastScrollData {
    public int scrollX;
    public int scrollY;
    public int maxScrollX;
    public int maxScrollY;
    public int fromIndex;
    public int toIndex;
    public int itemCount;

    public LastScrollData(Map<String, Long> scrollData) {
        scrollX = scrollData.get("scrollX").intValue();
        scrollY = scrollData.get("scrollY").intValue();
        maxScrollX = scrollData.get("maxScrollX").intValue();
        maxScrollY = scrollData.get("maxScrollY").intValue();
        toIndex = scrollData.get("toIndex").intValue();
        fromIndex = scrollData.get("fromIndex").intValue();
        itemCount = scrollData.get("itemCount").intValue();
    }

    public String toString() {
        return String.format("{scrollX=%s, scrollY=%s, maxScrollX=%s, maxScrollY=%s, toIndex=%s, " +
            "fromIndex=%s, itemCount=%s}", scrollX, scrollY, maxScrollX, maxScrollY, toIndex,
            fromIndex, itemCount);
    }

    public int getScrollX() {
        return scrollX;
    }

    public int getScrollY() {
        return scrollY;
    }

    public int getMaxScrollX() {
        return maxScrollX;
    }

    public int getMaxScrollY() {
        return maxScrollY;
    }

    public int getFromIndex() {
        return fromIndex;
    }

    public int getToIndex() {
        return toIndex;
    }

    public int getItemCount() {
        return itemCount;
    }
}
