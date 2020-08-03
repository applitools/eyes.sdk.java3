package com.applitools.eyes.selenium;

import com.applitools.eyes.Location;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.positioning.PositionMemento;
import com.applitools.eyes.positioning.PositionProvider;

public class NullPositionProvider implements PositionProvider {
    @Override
    public Location getCurrentPosition() {
        return Location.ZERO;
    }

    @Override
    public Location setPosition(Location location) {
        return location;
    }

    @Override
    public RectangleSize getEntireSize() {
        return RectangleSize.EMPTY;
    }

    @Override
    public PositionMemento getState() {
        return new NullPositionMemento();
    }

    @Override
    public void restoreState(PositionMemento state) {

    }
}
