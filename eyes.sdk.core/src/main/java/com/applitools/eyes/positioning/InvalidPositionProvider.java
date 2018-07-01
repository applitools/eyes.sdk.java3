package com.applitools.eyes.positioning;

import com.applitools.eyes.Location;
import com.applitools.eyes.RectangleSize;

/**
 * An implementation of {@link PositionProvider} which throws an exception
 * for every method. Can be used as a placeholder until an actual
 * implementation is set.
 */
public class InvalidPositionProvider implements PositionProvider {
    public Location getCurrentPosition() {
        throw new IllegalStateException(
                "This class does not implement methods!");
    }

    public Location setPosition(Location location) {
        throw new IllegalStateException(
                "This class does not implement methods!");
    }

    public RectangleSize getEntireSize() {
        throw new IllegalStateException(
                "This class does not implement methods!");
    }

    public PositionMemento getState() {
        throw new IllegalStateException(
                "This class does not implement methods!");
    }

    public void restoreState(PositionMemento state) {
        throw new IllegalStateException(
                "This class does not implement methods!");
    }
}
