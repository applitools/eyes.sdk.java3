package com.applitools.eyes.selenium.fluent;

import com.applitools.eyes.fluent.GetRegion;
import com.applitools.eyes.serializers.BySerializer;
import com.applitools.eyes.visualgrid.model.IGetFloatingRegionOffsets;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openqa.selenium.By;

public class FloatingRegionBySelector implements GetRegion, IGetFloatingRegionOffsets {

    @JsonSerialize(using = BySerializer.class)
    private final By selector;
    private final int maxUpOffset;
    private final int maxDownOffset;
    private final int maxLeftOffset;
    private final int maxRightOffset;
    private String regionId;

    public FloatingRegionBySelector(By regionSelector, int maxUpOffset, int maxDownOffset, int maxLeftOffset, int maxRightOffset) {
        this.selector = regionSelector;
        this.maxUpOffset = maxUpOffset;
        this.maxDownOffset = maxDownOffset;
        this.maxLeftOffset = maxLeftOffset;
        this.maxRightOffset = maxRightOffset;
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

    public By getSelector() {
        return selector;
    }

    public FloatingRegionBySelector regionId(String regionId) {
        this.regionId = regionId;
        return this;
    }

    public String getRegionId() {
        return regionId;
    }
}
