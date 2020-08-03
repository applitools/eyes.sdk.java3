package com.applitools.eyes.selenium;

import com.applitools.eyes.positioning.PositionMemento;

public class NullPositionMemento extends PositionMemento {
    @Override
    public int getX() {
        return 0;
    }

    @Override
    public int getY() {
        return 0;
    }
}
