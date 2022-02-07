package com.applitools.eyes.appium;

import com.applitools.eyes.*;
import com.applitools.eyes.exceptions.CoordinatesTypeConversionException;
import com.applitools.utils.ImageUtils;

import java.awt.image.BufferedImage;

public class EyesAppiumScreenshot extends EyesScreenshot {

    private final EyesAppiumDriver driver;

    public EyesAppiumScreenshot(Logger logger, EyesAppiumDriver driver, BufferedImage image) {
        super(logger, image);
        this.driver = driver;
    }

    @Override
    public Location getLocationInScreenshot(Location location, CoordinatesType coordinatesType) throws OutOfBoundsException {
        return location;
    }

    @Override
    public Region getIntersectedRegion(Region region, CoordinatesType resultCoordinatesType) {
        if (region.isSizeEmpty()) {
            return new Region(region);
        }

        CoordinatesType originalCoordinatesType = region.getCoordinatesType();
        if (!originalCoordinatesType.equals(CoordinatesType.SCREENSHOT_AS_IS)) {
            throw new CoordinatesTypeConversionException( String.format("Unknown coordinates type: '%s'", originalCoordinatesType));
        }

        Region intersectedRegion = convertRegionLocation(region, originalCoordinatesType, CoordinatesType.SCREENSHOT_AS_IS);
        intersectedRegion.intersect(new Region(0, 0, image.getWidth(), image.getHeight()));

        // If the intersection is empty we don't want to convert the coordinates.
        if (intersectedRegion.isSizeEmpty()) {
            return intersectedRegion;
        }

        // Converting the result to the required coordinates type.
        intersectedRegion = convertRegionLocation(intersectedRegion, CoordinatesType.SCREENSHOT_AS_IS, resultCoordinatesType);
        return intersectedRegion;
    }

    @Override
    public EyesAppiumScreenshot getSubScreenshot(Region region, boolean throwIfClipped) {
        BufferedImage subImage = ImageUtils.getImagePart(image, region);
        return new EyesAppiumScreenshot(logger, driver, subImage);
    }

    @Override
    public Location convertLocation(Location location, CoordinatesType from, CoordinatesType to) {
        return location;
    }
}
