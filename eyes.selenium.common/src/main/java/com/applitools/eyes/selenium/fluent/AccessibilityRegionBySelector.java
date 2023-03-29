package com.applitools.eyes.selenium.fluent;

import com.applitools.eyes.AccessibilityRegionType;
import com.applitools.eyes.fluent.GetRegion;
import com.applitools.eyes.fluent.IGetAccessibilityRegionType;
import com.applitools.eyes.serializers.BySerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openqa.selenium.By;

public class AccessibilityRegionBySelector implements GetRegion, IGetAccessibilityRegionType {

    private final AccessibilityRegionType regionType;
    @JsonSerialize(using = BySerializer.class)
    private final By selector;

    public AccessibilityRegionBySelector(By selector, AccessibilityRegionType regionType) {
        this.selector = selector;
        this.regionType = regionType;
        //selector.criteria
    }

    @Override
    public AccessibilityRegionType getAccessibilityRegionType() {
        return this.regionType;
    }

    public By getSelector() {
        return selector;
    }
}
