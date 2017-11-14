package com.applitools.eyes.fluent;

import com.applitools.eyes.EyesBase;
import com.applitools.eyes.Region;

public class IgnoreRegionByRectangle implements GetRegion {
    private Region region;

    public IgnoreRegionByRectangle(Region region) {
        this.region = region;
    }

    @Override
    public Region getRegion(EyesBase eyesBase) {
        return this.region;
    }
}
