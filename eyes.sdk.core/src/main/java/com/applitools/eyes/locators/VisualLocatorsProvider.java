package com.applitools.eyes.locators;

import com.applitools.eyes.Region;

import java.util.List;
import java.util.Map;

public interface VisualLocatorsProvider {
    Map<String, List<Region>> getLocators(VisualLocatorSettings visualLocatorSettings);
}
