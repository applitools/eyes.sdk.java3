package com.applitools.eyes.selenium.capture;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.Region;

public interface ISizeAdjuster {
    Region adjustRegion(Region inputRegion, RectangleSize deviceLogicalViewportSize);
}
