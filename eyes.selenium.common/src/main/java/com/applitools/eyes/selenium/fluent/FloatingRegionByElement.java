package com.applitools.eyes.selenium.fluent;

import com.applitools.eyes.fluent.GetRegion;
import com.applitools.eyes.serializers.WebElementSerializer;
import com.applitools.eyes.visualgrid.model.IGetFloatingRegionOffsets;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openqa.selenium.WebElement;

public class FloatingRegionByElement implements GetRegion, IGetFloatingRegionOffsets {

    @JsonSerialize(using = WebElementSerializer.class)
    protected final WebElement element;
    protected final int maxUpOffset;
    protected final int maxDownOffset;
    protected final int maxLeftOffset;
    protected final int maxRightOffset;
    private String regionId;

    public FloatingRegionByElement(WebElement element, int maxUpOffset, int maxDownOffset, int maxLeftOffset, int maxRightOffset) {

        this.element = element;
        this.maxUpOffset = maxUpOffset;
        this.maxDownOffset = maxDownOffset;
        this.maxLeftOffset = maxLeftOffset;
        this.maxRightOffset = maxRightOffset;
    }

    @JsonProperty("element")
    public WebElement getElement() {
        return element;
    }

    @Override
    public int getMaxLeftOffset() {
        return maxLeftOffset;
    }

    @Override
    public int getMaxUpOffset() {
        return maxUpOffset;
    }

    @Override
    public int getMaxRightOffset() {
        return maxRightOffset;
    }

    @Override
    public int getMaxDownOffset() {
        return maxDownOffset;
    }

    public FloatingRegionByElement regionId(String regionId) {
        this.regionId = regionId;
        return this;
    }

    public String getRegionId() {
        return regionId;
    }
}
