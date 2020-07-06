package com.applitools.eyes.selenium.positioning;

import com.applitools.eyes.Region;

public interface RegionPositionCompensation {
    Region compensateRegionPosition(Region region, double pixelRatio);
}
