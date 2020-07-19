package com.applitools.eyes.selenium.regionVisibility;

import com.applitools.eyes.Location;
import com.applitools.eyes.Logger;
import com.applitools.eyes.positioning.PositionMemento;
import com.applitools.eyes.positioning.PositionProvider;

/**
 * An implementation of {@link RegionVisibilityStrategy}, which tries to move
 * to the region.
 */
public class MoveToRegionVisibilityStrategy implements
        RegionVisibilityStrategy {

    private static final int VISIBILITY_OFFSET = 100; // Pixels

    private final Logger logger;
    private PositionMemento originalPosition;

    public MoveToRegionVisibilityStrategy(Logger logger) {
        this.logger = logger;
    }

    public void moveToRegion(PositionProvider positionProvider,
                             Location location) {
        logger.verbose("Getting current position state..");
        originalPosition = positionProvider.getState();
        logger.verbose("Done! Setting position..");

        // We set the location to "almost" the location we were asked. This is because sometimes, moving the browser
        // to the specific pixel where the element begins, causes the element to be slightly out of the viewport.
        int dstX = location.getX() - VISIBILITY_OFFSET;
        dstX = dstX < 0 ? 0 : dstX;
        int dstY = location.getY() - VISIBILITY_OFFSET;
        dstY = dstY < 0 ? 0 : dstY;
        positionProvider.setPosition(new Location(dstX, dstY));

        logger.verbose("Done!");
    }

    public void returnToOriginalPosition(PositionProvider positionProvider) {
        logger.verbose("Returning to original position...");
        positionProvider.restoreState(originalPosition);
        logger.verbose("Done!");
    }
}
