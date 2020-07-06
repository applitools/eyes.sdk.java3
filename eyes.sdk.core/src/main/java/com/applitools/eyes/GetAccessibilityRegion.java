package com.applitools.eyes;

import com.applitools.eyes.fluent.GetRegion;

import java.util.List;

public interface GetAccessibilityRegion extends GetRegion {
    List<AccessibilityRegionByRectangle> getRegions(EyesScreenshot screenshot);
}
