package com.applitools.eyes.images;

import com.applitools.eyes.*;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.ImageUtils;

import java.awt.image.BufferedImage;

/**
 * Encapsulates a screenshot taken by the images SDK.
 */
public class EyesImagesScreenshot extends EyesScreenshot {

    // The screenshot region in coordinates relative to the "entire screen"
    // (e.g., relative to the default content in case of a web page).
    protected Region bounds;

    /**
     * @param image The screenshot image.
     * @param location The top/left coordinates of the screenshot in context
     *                 relative coordinates type.
     */
    public EyesImagesScreenshot(BufferedImage image, Location location) {
        super(image);
        ArgumentGuard.notNull(location, "location");
        this.bounds = new Region(location,
                new RectangleSize(image.getWidth(), image.getHeight()));
    }

    /**
     * See {@link #EyesImagesScreenshot(BufferedImage, Location)}.
     * {@code location} defaults to {@code (0, 0)}.
     *
     * @param image The screenshot image.
     */
    public EyesImagesScreenshot(BufferedImage image) {
        this(image, new Location(0, 0));
    }

    @Override
    public EyesScreenshot getSubScreenshot(Region region,
                                           CoordinatesType coordinatesType, boolean throwIfClipped) {

        ArgumentGuard.notNull(region, "region");
        ArgumentGuard.notNull(coordinatesType, "coordinatesType");

        // We want to get the sub-screenshot in as-is coordinates type.
        Region subScreenshotRegion = getIntersectedRegion(region,
                coordinatesType, CoordinatesType.SCREENSHOT_AS_IS);

        if (subScreenshotRegion.isEmpty() ||
                (throwIfClipped &&
                        !subScreenshotRegion.getSize().equals(region.getSize()))) {
            throw new OutOfBoundsException(String.format(
                    "Region [%s, (%s)] is out of screenshot bounds [%s]",
                    region, coordinatesType, bounds));
        }

        BufferedImage subScreenshotImage =
                ImageUtils.getImagePart(image, subScreenshotRegion);

        // Notice that we need the bounds-relative coordinates as parameter
        // for new sub-screenshot.
        Region relativeSubScreenshotRegion =
                convertRegionLocation(subScreenshotRegion,
                        CoordinatesType.SCREENSHOT_AS_IS,
                        CoordinatesType.CONTEXT_RELATIVE);

        return new EyesImagesScreenshot(subScreenshotImage,
                relativeSubScreenshotRegion.getLocation());
    }

    @Override
    public Location convertLocation(Location location, CoordinatesType from,
                                       CoordinatesType to) {

        ArgumentGuard.notNull(location, "location");
        ArgumentGuard.notNull(from, "from");
        ArgumentGuard.notNull(to, "to");

        Location result = new Location(location);

        if (from == to) {
            return result;
        }

        switch (from) {
            case SCREENSHOT_AS_IS:
                if (to == CoordinatesType.CONTEXT_RELATIVE) {
                    result.offset(bounds.getLeft(), bounds.getTop());
                } else {
                    throw new CoordinatesTypeConversionException(from, to);
                }
                break;

            case CONTEXT_RELATIVE:
                if (to == CoordinatesType.SCREENSHOT_AS_IS) {
                    result.offset(-bounds.getLeft(), -bounds.getTop());
                } else {
                    throw new CoordinatesTypeConversionException(from, to);
                }
                break;

            default:
                throw new CoordinatesTypeConversionException(from, to);
        }
        return result;
    }

    @Override
    public Location getLocationInScreenshot(Location location,
            CoordinatesType coordinatesType) throws OutOfBoundsException {
        ArgumentGuard.notNull(location, "location");
        ArgumentGuard.notNull(coordinatesType, "coordinatesType");

        location = convertLocation(location, coordinatesType,
                CoordinatesType.CONTEXT_RELATIVE);

        if (!bounds.contains(location)) {
            throw new OutOfBoundsException(String.format(
                    "Location %s ('%s') is not visible in screenshot!", location,
                    coordinatesType));
        }

        return convertLocation(location, CoordinatesType.CONTEXT_RELATIVE,
                CoordinatesType.SCREENSHOT_AS_IS);
    }

    @Override
    public Region getIntersectedRegion(Region region,
            CoordinatesType originalCoordinatesType,
            CoordinatesType resultCoordinatesType) {

        ArgumentGuard.notNull(region, "region");
        ArgumentGuard.notNull(originalCoordinatesType, "coordinatesType");

        if (region.isEmpty()) {
            return new Region(region);
        }

        Region intersectedRegion = convertRegionLocation(region,
                originalCoordinatesType, CoordinatesType.CONTEXT_RELATIVE);

        intersectedRegion.intersect(bounds);

        // If the intersection is empty we don't want to convert the
        // coordinates.
        if (region.isEmpty()) {
            return region;
        }

        // The returned result should be in the coordinatesType given as
        // parameter.
        intersectedRegion.setLocation(
                convertLocation(intersectedRegion.getLocation(),
                        CoordinatesType.CONTEXT_RELATIVE, resultCoordinatesType));

        return intersectedRegion;
    }
}
