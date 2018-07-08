package com.applitools.eyes;

import com.applitools.utils.ArgumentGuard;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a region.
 */
@JsonIgnoreProperties({"location", "empty", "middleOffset", "size", "sizeEmpty", "subRegions"})
public class Region {
    private static Logger logger = new Logger();
    @JsonProperty("left")
    private int left;
    @JsonProperty("top")
    private int top;
    @JsonProperty("width")
    private int width;
    @JsonProperty("height")
    private int height;
    @JsonProperty("coordinatesType")
    private CoordinatesType coordinatesType;

    public static final Region EMPTY = new Region(0, 0, 0, 0, CoordinatesType.SCREENSHOT_AS_IS);

    public static void initLogger(Logger logger) {
        Region.logger = logger;
    }

    protected void makeEmpty() {
        left = EMPTY.getLeft();
        top = EMPTY.getTop();
        width = EMPTY.getWidth();
        height = EMPTY.getHeight();
        this.coordinatesType = EMPTY.getCoordinatesType();
    }

    @JsonCreator()
    public Region(@JsonProperty("left") int left,
                  @JsonProperty("top") int top,
                  @JsonProperty("width") int width,
                  @JsonProperty("height") int height) {
        this(left, top, width, height, CoordinatesType.SCREENSHOT_AS_IS);
    }

    public Region(int left, int top, int width, int height, CoordinatesType coordinatesType) {
        ArgumentGuard.greaterThanOrEqualToZero(width, "width");
        ArgumentGuard.greaterThanOrEqualToZero(height, "height");

        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
        this.coordinatesType = coordinatesType;
    }

    /**
     * @return true if the region is empty, false otherwise.
     */
    public boolean isEmpty() {
        return this.getLeft() == EMPTY.getLeft()
                && this.getTop() == EMPTY.getTop()
                && this.getWidth() == EMPTY.getWidth()
                && this.getHeight() == EMPTY.getHeight();
    }

    /**
     * @return true if the region's size is 0, false otherwise.
     */
    public boolean isSizeEmpty() {
        return this.getWidth() <= 0 || this.getHeight() <= 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof Region)) {
            return false;
        }
        Region other = (Region) obj;

        return (this.getLeft() == other.getLeft())
                && (this.getTop() == other.getTop())
                && (this.getWidth() == other.getWidth())
                && (this.getHeight() == other.getHeight());
    }

    @Override
    public int hashCode() {
        return (left + top + width + height);
    }

    public Region(Location location, RectangleSize size) {
        this(location, size, CoordinatesType.SCREENSHOT_AS_IS);
    }

    public Region(Location location, RectangleSize size, CoordinatesType coordinatesType) {
        ArgumentGuard.notNull(location, "location");
        ArgumentGuard.notNull(size, "size");

        this.left = location.getX();
        this.top = location.getY();
        this.width = size.getWidth();
        this.height = size.getHeight();
        this.coordinatesType = coordinatesType;
    }

    public Region(Region other) {
        ArgumentGuard.notNull(other, "other");

        left = other.getLeft();
        top = other.getTop();
        width = other.getWidth();
        height = other.getHeight();
        coordinatesType = other.getCoordinatesType();
    }

    /**
     * @return The (top,left) position of the current region.
     */
    public Location getLocation() {
        return new Location(left, top);
    }

    /**
     * Get an offset region.
     * @param dx The X axis offset.
     * @param dy The Y axis offset.
     * @return A region with an offset location.
     */
    public Region offset(int dx, int dy) {
        return new Region(getLocation().offset(dx, dy), getSize(), getCoordinatesType());
    }

    /**
     * Get a region which is a scaled version of the current region.
     * IMPORTANT: This also scales the LOCATION(!!) of the region (not just its size).
     * @param scaleRatio The ratio by which to scale the region.
     * @return A new region which is a scaled version of the current region.
     */
    public Region scale(double scaleRatio) {
        return new Region(getLocation().scale(scaleRatio), getSize().scale(scaleRatio), getCoordinatesType());
    }

    /**
     * @return The size of the region.
     */
    public RectangleSize getSize() {
        return new RectangleSize(width, height);
    }

    /**
     * @return The region's coordinate type.
     */
    public CoordinatesType getCoordinatesType() {
        return this.coordinatesType;
    }

    public void setCoordinatesType(CoordinatesType value) {
        this.coordinatesType = value;
    }

    /**
     * @param size The updated size of the region.
     */
    public void setSize(RectangleSize size) {
        width = size.getWidth();
        height = size.getHeight();
    }

    /**
     * Set the (top,left) position of the current region
     * @param location The (top,left) position to set.
     */
    public void setLocation(Location location) {
        ArgumentGuard.notNull(location, "location");
        left = location.getX();
        top = location.getY();
    }

    /**
     * @param containerRegion The region to divide into sub-regions.
     * @param subRegionSize   The maximum size of each sub-region.
     * @return The sub-regions composing the current region. If subRegionSize
     * is equal or greater than the current region,  only a single region is
     * returned.
     */
    private static Iterable<Region> getSubRegionsWithFixedSize(
            Region containerRegion, RectangleSize subRegionSize) {
        ArgumentGuard.notNull(containerRegion, "containerRegion");
        ArgumentGuard.notNull(subRegionSize, "subRegionSize");

        List<Region> subRegions = new LinkedList<>();

        int subRegionWidth = subRegionSize.getWidth();
        int subRegionHeight = subRegionSize.getHeight();

        ArgumentGuard.greaterThanZero(subRegionWidth, "subRegionSize width");
        ArgumentGuard.greaterThanZero(subRegionHeight, "subRegionSize height");

        // Normalizing.
        if (subRegionWidth > containerRegion.width) {
            subRegionWidth = containerRegion.width;
        }
        if (subRegionHeight > containerRegion.height) {
            subRegionHeight = containerRegion.height;
        }

        // If the requested size is greater or equal to the entire region size,
        // we return a copy of the region.
        if (subRegionWidth == containerRegion.width &&
                subRegionHeight == containerRegion.height) {
            subRegions.add(new Region(containerRegion));
            return subRegions;
        }

        int currentTop = containerRegion.top;
        int bottom = containerRegion.top + containerRegion.height - 1;
        int right = containerRegion.left + containerRegion.width - 1;

        CoordinatesType currentType = containerRegion.getCoordinatesType();

        while (currentTop <= bottom) {

            if (currentTop + subRegionHeight > bottom) {
                currentTop = (bottom - subRegionHeight) + 1;
            }

            int currentLeft = containerRegion.left;
            while (currentLeft <= right) {
                if (currentLeft + subRegionWidth > right) {
                    currentLeft = (right - subRegionWidth) + 1;
                }

                subRegions.add(new Region(currentLeft, currentTop,
                        subRegionWidth, subRegionHeight, currentType));

                currentLeft += subRegionWidth;
            }
            currentTop += subRegionHeight;
        }
        return subRegions;
    }

    /**
     * @param containerRegion  The region to divide into sub-regions.
     * @param maxSubRegionSize The maximum size of each sub-region (some
     *                         regions might be smaller).
     * @return The sub-regions composing the current region. If
     * maxSubRegionSize is equal or greater than the current region,
     * only a single region is returned.
     */
    private static Iterable<Region> getSubRegionsWithVaryingSize(
            Region containerRegion, RectangleSize maxSubRegionSize) {
        ArgumentGuard.notNull(containerRegion, "containerRegion");
        ArgumentGuard.notNull(maxSubRegionSize, "maxSubRegionSize");
        ArgumentGuard.greaterThanZero(maxSubRegionSize.getWidth(),
                "maxSubRegionSize.getWidth()");
        ArgumentGuard.greaterThanZero(maxSubRegionSize.getHeight(),
                "maxSubRegionSize.getHeight()");

        List<Region> subRegions = new LinkedList<>();

        int currentTop = containerRegion.top;
        int bottom = containerRegion.top + containerRegion.height;
        int right = containerRegion.left + containerRegion.width;

        CoordinatesType currentType = containerRegion.getCoordinatesType();

        while (currentTop < bottom) {

            int currentBottom = currentTop + maxSubRegionSize.getHeight();
            if (currentBottom > bottom) {
                currentBottom = bottom;
            }

            int currentLeft = containerRegion.left;
            while (currentLeft < right) {
                int currentRight = currentLeft + maxSubRegionSize.getWidth();
                if (currentRight > right) {
                    currentRight = right;
                }

                int currentHeight = currentBottom - currentTop;
                int currentWidth = currentRight - currentLeft;

                subRegions.add(new Region(currentLeft, currentTop,
                        currentWidth, currentHeight, currentType));

                currentLeft += maxSubRegionSize.getWidth();
            }
            currentTop += maxSubRegionSize.getHeight();
        }
        return subRegions;
    }

    /**
     * Returns a list of sub-regions which compose the current region.
     * @param subRegionSize The default sub-region size to use.
     * @param isFixedSize   If {@code false}, then sub-regions might have a
     *                      size which is smaller then {@code subRegionSize}
     *                      (thus there will be no overlap of regions).
     *                      Otherwise, all sub-regions will have the same
     *                      size, but sub-regions might overlap.
     * @return The sub-regions composing the current region. If {@code
     * subRegionSize} is equal or greater than the current region,
     * only a single region is returned.
     */
    public Iterable<Region> getSubRegions(RectangleSize subRegionSize,
                                          boolean isFixedSize) {
        if (isFixedSize) {
            return getSubRegionsWithFixedSize(this, subRegionSize);
        }

        return getSubRegionsWithVaryingSize(this, subRegionSize);
    }

    /**
     * See {@link #getSubRegions(RectangleSize, boolean)}.
     * {@code isFixedSize} defaults to {@code false}.
     */
    public Iterable<Region> getSubRegions(RectangleSize subRegionSize) {
        return getSubRegions(subRegionSize, false);
    }

    /**
     * Check if a region is contained within the current region.
     * @param other The region to check if it is contained within the current
     *              region.
     * @return True if {@code other} is contained within the current region,
     * false otherwise.
     */
    @SuppressWarnings("UnusedDeclaration")
    public boolean contains(Region other) {
        int right = left + width;
        int otherRight = other.getLeft() + other.getWidth();

        int bottom = top + height;
        int otherBottom = other.getTop() + other.getHeight();

        return top <= other.getTop() && left <= other.getLeft()
                && bottom >= otherBottom && right >= otherRight;
    }

    /**
     * Check if a specified location is contained within this region.
     * <p>
     * @param location The location to test.
     * @return True if the location is contained within this region,
     * false otherwise.
     */
    public boolean contains(Location location) {
        return location.getX() >= left
                && location.getX() <= (left + width)
                && location.getY() >= top
                && location.getY() <= (top + height);
    }

    /**
     * Check if a region is intersected with the current region.
     * @param other The region to check intersection with.
     * @return True if the regions are intersected, false otherwise.
     */
    public boolean isIntersected(Region other) {
        int right = left + width;
        int bottom = top + height;

        int otherLeft = other.getLeft();
        int otherTop = other.getTop();
        int otherRight = otherLeft + other.getWidth();
        int otherBottom = otherTop + other.getHeight();

        return (((left <= otherLeft && otherLeft <= right)
                || (otherLeft <= left && left <= otherRight))
                && ((top <= otherTop && otherTop <= bottom)
                || (otherTop <= top && top <= otherBottom)));
    }

    /**
     * Replaces this region with the intersection of itself and {@code other}
     * @param other The region with which to intersect.
     */
    public void intersect(Region other) {

        logger.verbose(String.format("intersecting this region (%s) with %s ...", this, other));

        // If there's no intersection set this as the Empty region.
        if (!isIntersected(other)) {
            makeEmpty();
            return;
        }

        // The regions intersect. So let's first find the left & top values
        int otherLeft = other.getLeft();
        int otherTop = other.getTop();

        int intersectionLeft = (left >= otherLeft) ? left : otherLeft;
        int intersectionTop = (top >= otherTop) ? top : otherTop;

        // Now the width and height of the intersect
        int right = left + width;
        int otherRight = otherLeft + other.getWidth();
        int intersectionRight = (right <= otherRight) ? right : otherRight;
        int intersectionWidth = intersectionRight - intersectionLeft;

        int bottom = top + height;
        int otherBottom = otherTop + other.getHeight();
        int intersectionBottom = (bottom <= otherBottom) ? bottom : otherBottom;
        int intersectionHeight = intersectionBottom - intersectionTop;

        left = intersectionLeft;
        top = intersectionTop;
        width = intersectionWidth;
        height = intersectionHeight;

    }


    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }


    public void setLeft(int value) {
        left = value;
    }

    public void setTop(int value) {
        top = value;
    }

    public void setWidth(int value) {
        width = value;
    }

    public void setHeight(int value) {
        height = value;
    }

    public Location getMiddleOffset() {
        int middleX = width / 2;
        int middleY = height / 2;

        return new Location(middleX, middleY);
    }

    public Region expandToContain(Region region) {
        int left = Math.min(this.left, region.left);
        int top = Math.min(this.top, region.top);

        int thisRight = this.left + this.width;
        int otherRight = region.left + region.width;
        int maxRight = Math.max(thisRight, otherRight);
        int width = maxRight - left;

        int thisBottom = this.top + this.height;
        int otherBottom = region.top + region.height;
        int maxBottom = Math.max(thisBottom, otherBottom);
        int height = maxBottom - top;

        return new Region(left, top, width, height);
    }

    @Override
    public String toString() {
        return "(" + left + ", " + top + ") " + width + "x" + height + ", " + coordinatesType;
    }
}
