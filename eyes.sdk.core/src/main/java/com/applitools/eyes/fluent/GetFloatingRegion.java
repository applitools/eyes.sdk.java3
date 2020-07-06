package com.applitools.eyes.fluent;

import com.applitools.eyes.EyesScreenshot;
import com.applitools.eyes.FloatingMatchSettings;

import java.util.List;

public interface GetFloatingRegion extends GetRegion {
    List<FloatingMatchSettings> getRegions(EyesScreenshot screenshot);
}
