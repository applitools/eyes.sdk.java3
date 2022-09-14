package com.applitools.eyes.fluent;

import com.applitools.eyes.EyesScreenshot;
import com.applitools.eyes.Padding;
import com.applitools.eyes.Region;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class SimpleRegionByRectangle implements GetSimpleRegion {
    private final Region region;
    private final Padding padding;

    public SimpleRegionByRectangle(Region region) {
        this.region = region;
        this.padding = new Padding();
    }

    public SimpleRegionByRectangle(Region region, Padding padding) {
        this.region = region;
        this.padding = padding;
    }

    @JsonProperty("region")
    public Region getRegion() {
        return region;
    }

    @Override
    public List<Region> getRegions( EyesScreenshot screenshot) {
        List<Region> value = new ArrayList<>();
        region.addPadding(padding);
        value.add(this.region);
        return value;
    }

    public Padding getPadding() { return this.padding; }
}
