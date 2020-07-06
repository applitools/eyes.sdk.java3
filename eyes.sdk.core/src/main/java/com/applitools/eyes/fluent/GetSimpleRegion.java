package com.applitools.eyes.fluent;

import com.applitools.eyes.EyesScreenshot;
import com.applitools.eyes.Region;

import java.util.List;

public interface GetSimpleRegion extends GetRegion {
    List<Region> getRegions(EyesScreenshot screenshot);
}
