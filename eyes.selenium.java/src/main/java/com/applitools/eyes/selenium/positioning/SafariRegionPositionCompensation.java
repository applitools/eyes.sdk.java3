package com.applitools.eyes.selenium.positioning;

import com.applitools.eyes.Region;

public class SafariRegionPositionCompensation implements RegionPositionCompensation {

    @Override
    public Region compensateRegionPosition(Region region, double pixelRatio) {

        if (pixelRatio == 1.0) {
            return region;
        }

        if (region.getWidth() <= 0 || region.getHeight() <= 0) {
            return Region.EMPTY;
        }

        return region.offset(0, (int) Math.ceil(pixelRatio));
    }

}
