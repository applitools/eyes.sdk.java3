package com.applitools.eyes.playwright.fluent;

import com.applitools.eyes.AccessibilityRegionType;
import com.applitools.eyes.playwright.universal.dto.Selector;

public class AccessibilitySelector extends Selector {

    private AccessibilityRegionType accessibilityRegionType;

    public AccessibilitySelector(String selector, AccessibilityRegionType type) {
        super(selector);
        this.accessibilityRegionType = type;
    }

    public AccessibilityRegionType getAccessibilityRegionType() {
        return accessibilityRegionType;
    }

    public void setAccessibilityRegionType(AccessibilityRegionType accessibilityRegionType) {
        this.accessibilityRegionType = accessibilityRegionType;
    }
}
