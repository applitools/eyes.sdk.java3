package com.applitools.eyes.selenium.regionVisibility;

import com.applitools.eyes.Location;
import com.applitools.eyes.Logger;
import com.applitools.eyes.positioning.PositionProvider;

/**
 * An implementation of {@link RegionVisibilityStrategy} which does nothing.
 */
public class NopRegionVisibilityStrategy implements RegionVisibilityStrategy {

    private final Logger logger;

    public NopRegionVisibilityStrategy(Logger logger) {
        this.logger = logger;
    }

    public void moveToRegion(PositionProvider positionProvider,
                             Location location) {
        logger.verbose("Ignored (no op).");
    }

    public void returnToOriginalPosition(PositionProvider positionProvider) {
        logger.verbose("Ignored (no op).");
    }
}
