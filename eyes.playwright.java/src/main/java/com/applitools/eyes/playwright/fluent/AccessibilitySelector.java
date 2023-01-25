package com.applitools.eyes.playwright.fluent;

import com.applitools.eyes.AccessibilityRegionType;
import com.applitools.eyes.playwright.universal.dto.Selector;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.microsoft.playwright.Locator;

/**
 * Used internally to represent a ref of an accessibility selector
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccessibilitySelector extends Selector {

    private AccessibilityRegionType accessibilityRegionType;

    public AccessibilitySelector(String selector, AccessibilityRegionType type) {
        super(selector);
        this.accessibilityRegionType = type;
    }

    public AccessibilitySelector(Locator locator, AccessibilityRegionType type) {
        super(locator);
        this.accessibilityRegionType = type;
    }

    public AccessibilityRegionType getAccessibilityRegionType() {
        return accessibilityRegionType;
    }

    public void setAccessibilityRegionType(AccessibilityRegionType accessibilityRegionType) {
        this.accessibilityRegionType = accessibilityRegionType;
    }
}
