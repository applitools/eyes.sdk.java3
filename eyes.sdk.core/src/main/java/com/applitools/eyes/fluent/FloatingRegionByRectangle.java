package com.applitools.eyes.fluent;

import com.applitools.eyes.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class FloatingRegionByRectangle implements GetRegion {
    private final Region region;
    private final int maxUpOffset;
    private final int maxDownOffset;
    private final int maxLeftOffset;
    private final int maxRightOffset;

    public FloatingRegionByRectangle(Region region, int maxUpOffset, int maxDownOffset, int maxLeftOffset, int maxRightOffset) {
        this.region = region;
        this.maxUpOffset = maxUpOffset;
        this.maxDownOffset = maxDownOffset;
        this.maxLeftOffset = maxLeftOffset;
        this.maxRightOffset = maxRightOffset;
    }

    @JsonProperty("region")
    public Region getRegion() {
        return region;
    }

    public int getMaxUpOffset() {
        return maxUpOffset;
    }

    public int getMaxDownOffset() {
        return maxDownOffset;
    }

    public int getMaxLeftOffset() {
        return maxLeftOffset;
    }

    public int getMaxRightOffset() {
        return maxRightOffset;
    }
}
