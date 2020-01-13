package com.applitools.eyes.selenium;

import com.applitools.eyes.Region;

public class BoundsAndBorders {
    private final Region bounds;
    private final Borders borders;

    public BoundsAndBorders(Region bounds, Borders borders) {
        this.bounds = bounds;
        this.borders = borders;
    }

    public Region getBounds() {
        return bounds;
    }

    public Borders getBorders() {
        return borders;
    }
}
