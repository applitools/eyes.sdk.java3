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

    private PositionMemento originalPosition;

    public MoveToRegionVisibilityStrategy() {
    }

    public void moveToRegion(PositionProvider positionProvider,
                             Location location) {
        originalPosition = positionProvider.getState();

        // We set the location to "almost" the location we were asked. This is because sometimes, moving the browser
        // to the specific pixel where the element begins, causes the element to be slightly out of the viewport.
        int dstX = location.getX() - VISIBILITY_OFFSET;
        dstX = Math.max(dstX, 0);
        int dstY = location.getY() - VISIBILITY_OFFSET;
        dstY = Math.max(dstY, 0);
        positionProvider.setPosition(new Location(dstX, dstY));
    }

    public void returnToOriginalPosition(PositionProvider positionProvider) {
        positionProvider.restoreState(originalPosition);
    }
}
