package com.applitools.eyes.playwright.universal.dto;

import com.applitools.eyes.fluent.GetRegion;
import com.applitools.eyes.universal.dto.TAccessibilityRegion;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccessibilityRegionBySelector extends TAccessibilityRegion implements GetRegion {

    private Selector region;

    public Selector getRegion() {
        return region;
    }

    public void setRegion(Selector region) {
        this.region = region;
    }

}
