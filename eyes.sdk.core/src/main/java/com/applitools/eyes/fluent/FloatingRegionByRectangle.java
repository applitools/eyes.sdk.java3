package com.applitools.eyes.fluent;

import com.applitools.eyes.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class FloatingRegionByRectangle implements GetFloatingRegion {
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

    @Override
    public List<FloatingMatchSettings> getRegions(EyesScreenshot screenshot) {
        List<FloatingMatchSettings> value = new ArrayList<>();
        value.add(new FloatingMatchSettings(
                region.getLeft(), region.getTop(), region.getWidth(), region.getHeight(),
                maxUpOffset, maxDownOffset, maxLeftOffset, maxRightOffset));
        return value;
    }
}
