package com.applitools.eyes.playwright.fluent;

import com.applitools.eyes.AccessibilityRegionType;
import com.applitools.eyes.playwright.universal.dto.Element;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.microsoft.playwright.ElementHandle;

/**
 * Used internally to represent a ref of an accessibility element
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccessibilityElement extends Element {

    private AccessibilityRegionType accessibilityRegionType;

    public AccessibilityElement(ElementHandle element, AccessibilityRegionType type) {
        super(element);
        this.accessibilityRegionType = type;
    }

    public AccessibilityRegionType getAccessibilityRegionType() {
        return accessibilityRegionType;
    }

    public void setAccessibilityRegionType(AccessibilityRegionType accessibilityRegionType) {
        this.accessibilityRegionType = accessibilityRegionType;
    }
}
