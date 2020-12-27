package com.applitools.eyes;

import com.applitools.eyes.fluent.GetRegion;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public interface GetAccessibilityRegion extends GetRegion {
    @JsonIgnore
    List<AccessibilityRegionByRectangle> getRegions(EyesScreenshot screenshot);
}
