package com.applitools.eyes.fluent;

import com.applitools.eyes.EyesScreenshot;
import com.applitools.eyes.Region;

import java.util.ArrayList;
import java.util.List;

public class SimpleRegionByRectangle implements GetSimpleRegion {
    private final Region region;

    public SimpleRegionByRectangle(Region region) {
        this.region = region;
    }

    @Override
    public List<Region> getRegions( EyesScreenshot screenshot) {
        List<Region> value = new ArrayList<>();
        value.add(this.region);
        return value;
    }
}
