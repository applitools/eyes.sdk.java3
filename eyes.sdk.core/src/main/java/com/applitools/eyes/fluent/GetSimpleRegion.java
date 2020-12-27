package com.applitools.eyes.fluent;

import com.applitools.eyes.EyesScreenshot;
import com.applitools.eyes.Region;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public interface GetSimpleRegion extends GetRegion {
    @JsonIgnore
    List<Region> getRegions(EyesScreenshot screenshot);
}
