package com.applitools.eyes.selenium.positioning;

import com.applitools.eyes.Region;

public class NullRegionPositionCompensation implements RegionPositionCompensation {

    @Override
    public Region compensateRegionPosition(Region region, double pixelRatio) {
        return region;
    }
}
