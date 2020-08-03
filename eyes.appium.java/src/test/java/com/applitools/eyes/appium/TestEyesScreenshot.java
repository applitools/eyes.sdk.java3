package com.applitools.eyes.appium;

import com.applitools.eyes.*;

import java.awt.image.BufferedImage;

public class TestEyesScreenshot extends EyesScreenshot {

    public TestEyesScreenshot(Logger logger, BufferedImage image) {
        super(logger, image);
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
