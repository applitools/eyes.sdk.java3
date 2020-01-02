package com.applitools.eyes.selenium.capture;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.Region;

public class NullSizeAdjuster implements ISizeAdjuster {

    private static ISizeAdjuster instance = new NullSizeAdjuster();
    public static ISizeAdjuster getInstance() {return instance;}

    @Override
    public Region adjustRegion(Region inputRegion, RectangleSize deviceLogicalViewportSize) {
        return inputRegion;
    }
}
