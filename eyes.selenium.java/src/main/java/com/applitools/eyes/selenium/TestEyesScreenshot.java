package com.applitools.eyes.selenium;

import com.applitools.eyes.*;

import java.awt.image.BufferedImage;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

public class TestEyesScreenshot extends EyesScreenshot {

    private static final BufferedImage testBitmap = new BufferedImage(100, 100, TYPE_INT_RGB);
    private static final Logger logger = new Logger();

    public TestEyesScreenshot() {
        super(logger, testBitmap);
    }

    @Override
    public EyesScreenshot getSubScreenshot(Region region, boolean throwIfClipped) {
        return null;
    }

    @Override
    public Location convertLocation(Location location, CoordinatesType from, CoordinatesType to) {
        return null;
    }

    @Override
    public Location getLocationInScreenshot(Location location, CoordinatesType coordinatesType) throws OutOfBoundsException {
        return null;
    }

    @Override
    public Region getIntersectedRegion(Region region, CoordinatesType coordinatesType) {
        return null;
    }
}
