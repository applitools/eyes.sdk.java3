package com.applitools.eyes;

import java.util.List;

public interface IGetAccessibilityRegion {
    List<AccessibilityRegionByRectangle> getRegions(IDriverProvider eyesBase, EyesScreenshot screenshot);

}
