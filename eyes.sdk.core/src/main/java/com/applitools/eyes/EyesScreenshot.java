/*
 * Applitools SDK for Java.
 */
package com.applitools.eyes;

import com.applitools.utils.ArgumentGuard;

import java.awt.image.BufferedImage;

/**
 * Base class for handling screenshots.
 */
public abstract class EyesScreenshot {
    protected final BufferedImage image;
    protected final Logger logger;

    protected String domUrl;

    public EyesScreenshot(Logger logger, BufferedImage image) {
        ArgumentGuard.notNull(logger, "logger");
        ArgumentGuard.notNull(image, "image");
        this.image = image;
        this.logger = logger;
    }

    /**
     * @return The screenshot image.
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Gets dom url.
     *
     * @return the dom url
     */
    public String getDomUrl() {
        return domUrl;
    }

    /**
     * Sets dom url.
     *
     * @param domUrl the dom url
     */
    public void setDomUrl(String domUrl) {
        this.domUrl = domUrl;
    }

    /**
     * Returns a part of the screenshot based on the given region.
     * @param region          The region for which we should get the sub screenshot.
     * @param throwIfClipped  Throw an EyesException if the region is not
     *                        fully contained in the screenshot.
     * @return A screenshot instance containing the given region.
     */
    public abstract EyesScreenshot getSubScreenshot(Region region, boolean throwIfClipped);

    /**
     * Converts a location's coordinates with the {@code from} coordinates type
     * to the {@code to} coordinates type.
     * @param location The location which coordinates needs to be converted.
     * @param from     The current coordinates type for {@code location}.
     * @param to       The target coordinates type for {@code location}.
     * @return A new location which is the transformation of {@code location} to
     * the {@code to} coordinates type.
     */
    public abstract Location convertLocation(Location location,
                                             CoordinatesType from,
                                             CoordinatesType to);

    /**
     * Calculates the location in the screenshot of the location given as
     * parameter.
     * @param location        The location as coordinates inside the current frame.
     * @param coordinatesType The coordinates type of {@code location}.
     * @return The corresponding location inside the screenshot,
     * in screenshot as-is coordinates type.
     * @throws com.applitools.eyes.OutOfBoundsException If the location is
     *                                                  not inside the frame's region in the screenshot.
     */
    public abstract Location getLocationInScreenshot(Location location,
                                                     CoordinatesType coordinatesType) throws OutOfBoundsException;

    /**
     * Get the intersection of the given region with the screenshot.
     * @param region          The region to intersect.
     * @param coordinatesType The coordinates type of {@code region}.
     * @return The intersected region, in {@code coordinatesType} coordinates.
     */
    public abstract Region getIntersectedRegion(Region region,
                                       CoordinatesType coordinatesType);
    /**
     * Converts a region's location coordinates with the {@code from}
     * coordinates type to the {@code to} coordinates type.
     * @param region The region which location's coordinates needs to be converted.
     * @param from   The current coordinates type for {@code region}.
     * @param to     The target coordinates type for {@code region}.
     * @return A new region which is the transformation of {@code region} to
     * the {@code to} coordinates type.
     */
    public Region convertRegionLocation(Region region,
                                        CoordinatesType from,
                                        CoordinatesType to) {
        ArgumentGuard.notNull(region, "region");

        if (region.isSizeEmpty()) {
            return new Region(region);
        }

        ArgumentGuard.notNull(from, "from");
        ArgumentGuard.notNull(to, "to");

        Location originalLocation = region.getLocation();
        logger.verbose("original location: " + originalLocation);
        Location updatedLocation = convertLocation(originalLocation, from, to);
        logger.verbose("updated location: " + updatedLocation);

        return new Region(updatedLocation, region.getSize());
    }
}