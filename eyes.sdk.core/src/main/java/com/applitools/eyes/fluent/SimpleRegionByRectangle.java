package com.applitools.eyes.fluent;

import com.applitools.eyes.EyesScreenshot;
import com.applitools.eyes.Region;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class SimpleRegionByRectangle implements GetSimpleRegion {
    private final Region region;

    public SimpleRegionByRectangle(Region region) {
        this.region = region;
    }

    @JsonProperty("region")
    public Region getRegion() {
        return region;
    }

    @Override
    public List<Region> getRegions( EyesScreenshot screenshot) {
        List<Region> value = new ArrayList<>();
        value.add(this.region);
        return value;
    }
}
