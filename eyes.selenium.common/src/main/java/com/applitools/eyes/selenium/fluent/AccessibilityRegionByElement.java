package com.applitools.eyes.selenium.fluent;

import com.applitools.eyes.AccessibilityRegionType;
import com.applitools.eyes.fluent.GetRegion;
import com.applitools.eyes.fluent.IGetAccessibilityRegionType;
import com.applitools.eyes.serializers.WebElementSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openqa.selenium.WebElement;

public class AccessibilityRegionByElement implements GetRegion, IGetAccessibilityRegionType {

    protected final AccessibilityRegionType regionType;
    @JsonSerialize(using = WebElementSerializer.class)
    protected final WebElement element;

    public AccessibilityRegionByElement(WebElement element, AccessibilityRegionType regionType) {
        this.element = element;
        this.regionType = regionType;
    }

    @Override
    public AccessibilityRegionType getAccessibilityRegionType() {
        return regionType;
    }


    @JsonProperty("element")
    public WebElement getElement() {
        return element;
    }

}
