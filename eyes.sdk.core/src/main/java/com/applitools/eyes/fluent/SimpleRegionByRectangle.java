package com.applitools.eyes.fluent;

import com.applitools.eyes.Padding;
import com.applitools.eyes.Region;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SimpleRegionByRectangle implements GetRegion {
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

    public Padding getPadding() { return this.padding; }
}
