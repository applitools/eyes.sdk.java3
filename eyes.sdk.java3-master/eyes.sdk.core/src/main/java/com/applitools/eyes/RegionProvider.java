package com.applitools.eyes;

/**
 * Encapsulates a getRegion "callback" and how the region's coordinates
 * should be used.
 */
public interface RegionProvider {
    /**
     *
     * @return A region with "as is" viewport coordinates.
     */
    public Region getRegion();

    /**
     *
     * @return The type of coordinates on which the region is based.
     */
    public CoordinatesType getCoordinatesType();

}
