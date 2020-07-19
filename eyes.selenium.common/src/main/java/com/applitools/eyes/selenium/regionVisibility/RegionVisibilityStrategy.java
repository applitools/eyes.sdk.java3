package com.applitools.eyes.selenium.regionVisibility;

import com.applitools.eyes.Location;
import com.applitools.eyes.positioning.PositionProvider;

/**
 * Encapsulates implementations for providing region visibility during
 * checkRegion.
 */
public interface RegionVisibilityStrategy {
    void moveToRegion(PositionProvider positionProvider, Location location);
    void returnToOriginalPosition(PositionProvider positionProvider);
}
