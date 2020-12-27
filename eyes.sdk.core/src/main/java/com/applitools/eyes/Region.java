package com.applitools.eyes;

import com.applitools.utils.ArgumentGuard;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a region.
 */
@JsonIgnoreProperties({"location", "empty", "middleOffset", "size", "sizeEmpty", "subRegions", "area", "negativeLocation", "right", "bottom"})
public class Region implements IRegion {
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

    /**
     * The constant EMPTY.
     */
    public static final Region EMPTY = new Region(0, 0, 0, 0, CoordinatesType.SCREENSHOT_AS_IS);

    /**
     * Make empty.
     */
    protected void makeEmpty() {
        left = EMPTY.getLeft();
        top = EMPTY.getTop();
        width = EMPTY.getWidth();
        height = EMPTY.getHeight();
        this.coordinatesType = EMPTY.getCoordinatesType();
    }

    /**
     * Instantiates a new Region.
     * @param left   the left
     * @param top    the top
     * @param width  the width
     * @param height the height
     */
    @JsonCreator()
    public Region(@JsonProperty("left") int left,
                  @JsonProperty("top") int top,
                  @JsonProperty("width") int width,
                  @JsonProperty("height") int height) {
        this(left, top, width, height, CoordinatesType.SCREENSHOT_AS_IS);
    }

    /**
     * Instantiates a new Region.
     * @param left            the left
     * @param top             the top
     * @param width           the width
     * @param height          the height
     * @param coordinatesType the coordinates type
     */
    public Region(int left, int top, int width, int height, CoordinatesType coordinatesType) {
        ArgumentGuard.greaterThanOrEqualToZero(width, "width");
        ArgumentGuard.greaterThanOrEqualToZero(height, "height");

        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
        this.coordinatesType = coordinatesType;
    }

    public Region(Rectangle rectangle) {
        this(rectangle.x, rectangle.y, rectangle.width, rectangle.height, CoordinatesType.SCREENSHOT_AS_IS);
    }

    /**
     * Is empty boolean.
     * @return true if the region is empty, false otherwise.
     */
    public boolean isEmpty() {
        return this.getLeft() == EMPTY.getLeft()
                && this.getTop() == EMPTY.getTop()
                && this.getWidth() == EMPTY.getWidth()
                && this.getHeight() == EMPTY.getHeight();
    }

    /**
     * Is size empty boolean.
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

    /**
     * Instantiates a new Region.
     * @param location the location
     * @param size     the size
     */
    public Region(Location location, RectangleSize size) {
        this(location, size, CoordinatesType.SCREENSHOT_AS_IS);
    }

    /**
     * Instantiates a new Region.
     * @param location        the location
     * @param size            the size
     * @param coordinatesType the coordinates type
     */
    public Region(Location location, RectangleSize size, CoordinatesType coordinatesType) {
        ArgumentGuard.notNull(size, "size");

        this.left = location.getX();
        this.top = location.getY();
        this.width = size.getWidth();
        this.height = size.getHeight();
        this.coordinatesType = coordinatesType;
    }

    /**
     * Instantiates a new Region.
     * @param other the other
     */
    public Region(IRegion other) {
        ArgumentGuard.notNull(other, "other");

        left = other.getLeft();
        top = other.getTop();
        width = other.getWidth();
        height = other.getHeight();
        coordinatesType = other.getCoordinatesType();
    }

    /**
     * Gets location.
     * @return The (left, top) position of the current region.
     */
    public Location getLocation() {
        return new Location(left, top);
    }

    /**
     * Gets negative location.
     * @return The (-left, -top) position of the current region.
     */
    public Location getNegativeLocation() {
        return new Location(-left, -top);
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
     * Get an offset region.
     * @param location the amount by which to offset.
     * @return A region with an offset location.
     */
    public Region offset(Location location) {
        return new Region(getLocation().offset(location), getSize(), getCoordinatesType());
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
     * Gets size.
     * @return The size of the region.
     */
    public RectangleSize getSize() {
        return new RectangleSize(width, height);
    }

    /**
     * Gets coordinates type.
     * @return The region's coordinate type.
     */
    public CoordinatesType getCoordinatesType() {
        return this.coordinatesType;
    }

    /**
     * Sets coordinates type.
     * @param value the value
     */
    public void setCoordinatesType(CoordinatesType value) {
        this.coordinatesType = value;
    }

    /**
     * Sets size.
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
     * @param isFixedSize   If {@code false}, then sub-regions might have a                      size which is smaller then {@code subRegionSize}                      (thus there will be no overlap of regions).                      Otherwise, all sub-regions will have the same                      size, but sub-regions might overlap.
     * @return The sub-regions composing the current region. If {@code
     * subRegionSize} is equal or greater than the current region, only a single region is returned.
     */
    public Iterable<Region> getSubRegions(RectangleSize subRegionSize,
                                          boolean isFixedSize) {
        if (isFixedSize) {
            return getSubRegionsWithFixedSize(this, subRegionSize);
        }

        return getSubRegionsWithVaryingSize(this, subRegionSize);
    }

    /**
     * Gets sub regions.
     * @param subRegionSize the sub region size
     * @return the sub regions
     * @see #getSubRegions(RectangleSize, boolean) #getSubRegions(RectangleSize, boolean). {@code isFixedSize} defaults to {@code false}.
     */
    public Iterable<Region> getSubRegions(RectangleSize subRegionSize) {
        return getSubRegions(subRegionSize, false);
    }

    /**
     * Check if a region is contained within the current region.
     * @param other The region to check if it is contained within the current              region.
     * @return True if {@code other} is contained within the current region, false otherwise.
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
     * @return True if the location is contained within this region, false otherwise.
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
    @SuppressWarnings("WeakerAccess")
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
        Region intersected = getIntersected(other);
        this.setLocation(intersected.getLocation());
        this.setSize(intersected.getSize());
    }

    public Region getIntersected(Region other) {

        // If there's no intersection set this as the Empty region.
        if (!isIntersected(other)) {
            return Region.EMPTY;
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

        return new Region(intersectionLeft, intersectionTop, intersectionWidth, intersectionHeight, coordinatesType);
    }

    /**
     * Gets left.
     * @return the left
     */
    public int getLeft() {
        return left;
    }

    /**
     * Gets top.
     * @return the top
     */
    public int getTop() {
        return top;
    }

    /**
     * Gets width.
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets height.
     * @return the height
     */
    public int getHeight() {
        return height;
    }


    /**
     * Sets left.
     * @param value the value
     */
    public void setLeft(int value) {
        left = value;
    }

    /**
     * Sets top.
     * @param value the value
     */
    public void setTop(int value) {
        top = value;
    }


    public int getBottom() {
        return top + height;
    }

    public int getRight() {
        return left + width;
    }

    /**
     * Sets width.
     * @param value the value
     */
    public void setWidth(int value) {
        width = value;
    }

    /**
     * Sets height.
     * @param value the value
     */
    public void setHeight(int value) {
        height = value;
    }

    /**
     * Gets middle offset.
     * @return the middle offset
     */
    public Location getMiddleOffset() {
        int middleX = width / 2;
        int middleY = height / 2;

        return new Location(middleX, middleY);
    }

    /**
     * Expand to contain region.
     * @param region the region
     * @return the region
     */
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

    public int getArea() {
        return this.getWidth() * this.getHeight();
    }

    @JsonProperty("x")
    public void setX(int x) {
        this.left = x;
    }

    @JsonProperty("y")
    public void setY(int y) {
        this.top = y;
    }

    public SubregionForStitching[] getSubRegions(RectangleSize maxSubRegionSize, int logicalOverlap, double l2pScaleRatio, Rectangle physicalRectInScreenshot, Logger logger) {
        List<SubregionForStitching> subRegions = new ArrayList<>();

        int doubleLogicalOverlap = logicalOverlap * 2;
        int physicalOverlap = (int) Math.round(doubleLogicalOverlap * l2pScaleRatio);

        boolean needVScroll = (this.height * l2pScaleRatio) > physicalRectInScreenshot.getHeight();
        boolean needHScroll = (this.width * l2pScaleRatio) > physicalRectInScreenshot.getWidth();

        int scrollY = 0;
        int currentTop = 0;
        int currentLogicalHeight = maxSubRegionSize.getHeight();

        int deltaY = currentLogicalHeight - doubleLogicalOverlap;

        boolean isTopEdge = true;
        boolean isBottomEdge = false;

        while (!isBottomEdge) {
            int currentScrollTop = scrollY + maxSubRegionSize.getHeight();
            if (currentScrollTop >= this.height) {
                if (!isTopEdge) {
                    scrollY = height - currentLogicalHeight;
                    currentLogicalHeight = height - currentTop;
                    currentTop = height - currentLogicalHeight - doubleLogicalOverlap - logicalOverlap;
                } else {
                    currentLogicalHeight = height - currentTop;
                }
                isBottomEdge = true;
            }

            int scrollX = 0;
            int currentLeft = 0;
            int currentLogicalWidth = maxSubRegionSize.getWidth();

            int deltaX = currentLogicalWidth - doubleLogicalOverlap;

            boolean isLeftEdge = true;
            boolean isRightEdge = false;

            while (!isRightEdge) {
                int currentScrollRight = scrollX + maxSubRegionSize.getWidth();
                if (currentScrollRight >= width) {
                    if (!isLeftEdge) {
                        scrollX = width - currentLogicalWidth;
                        currentLogicalWidth = width - currentLeft;
                        currentLeft = width - currentLogicalWidth - doubleLogicalOverlap - logicalOverlap;
                    } else {
                        currentLogicalWidth = width - currentLeft;
                    }
                    isRightEdge = true;
                }

                Rectangle physicalCropArea = new Rectangle(physicalRectInScreenshot);
                Rectangle logicalCropArea = new Rectangle(0, 0, currentLogicalWidth, currentLogicalHeight);
                Point pastePoint = new Point(currentLeft, currentTop);

                // handle horizontal
                if (isRightEdge) {
                    int physicalWidth = (int) Math.round(currentLogicalWidth * l2pScaleRatio);
                    physicalCropArea.x = physicalRectInScreenshot.x + physicalRectInScreenshot.width - physicalWidth;
                    physicalCropArea.width = physicalWidth;
                }


                if (!isLeftEdge) {
                    logicalCropArea.x += logicalOverlap;
                    logicalCropArea.width -= logicalOverlap;
                }

                if (isRightEdge && !isLeftEdge) {
                    // If scrolled to the right edge, make sure the left part is still inside physical viewport.
                    int newX = physicalCropArea.x - (physicalOverlap * 2);
                    if (newX >= physicalRectInScreenshot.x) // everything is okay
                    {
                        physicalCropArea.x -= physicalOverlap * 2;
                        physicalCropArea.width += physicalOverlap * 2;
                        logicalCropArea.width += doubleLogicalOverlap * 2;
                    } else // Oops, overshoot. We need to correct the width and left position.
                    {
                        int pDelta = physicalRectInScreenshot.x - newX;
                        int lDelta = (int) Math.round(pDelta / l2pScaleRatio);
                        physicalCropArea.x = physicalRectInScreenshot.x;
                        physicalCropArea.width += (physicalOverlap * 2) - pDelta;
                        logicalCropArea.width += (doubleLogicalOverlap * 2) - lDelta;
                        pastePoint.x += lDelta;
                    }
                }

                // handle vertical
                if (isBottomEdge) {
                    int physicalHeight = (int) Math.round(currentLogicalHeight * l2pScaleRatio);
                    physicalCropArea.y = physicalRectInScreenshot.y + physicalRectInScreenshot.height - physicalHeight;
                    physicalCropArea.height = physicalHeight;
                }

                if (!isTopEdge) {
                    logicalCropArea.y += logicalOverlap;
                    logicalCropArea.height -= logicalOverlap;
                }

                if (isBottomEdge && !isTopEdge) {
                    // If scrolled to the bottom edge, make sure the top part is still inside physical viewport.
                    int newY = physicalCropArea.y - (physicalOverlap * 2);
                    if (newY >= physicalRectInScreenshot.y) // everything is okay
                    {
                        physicalCropArea.y -= physicalOverlap * 2;
                        physicalCropArea.height += physicalOverlap * 2;
                        logicalCropArea.height += doubleLogicalOverlap * 2;
                    } else // Oops, overshoot. We need to correct the height and top position.
                    {
                        int pDelta = physicalRectInScreenshot.y - newY;
                        int lDelta = (int) Math.round(pDelta / l2pScaleRatio);
                        physicalCropArea.y = physicalRectInScreenshot.y;
                        physicalCropArea.height += (physicalOverlap * 2) - pDelta;
                        logicalCropArea.height += (doubleLogicalOverlap * 2) - lDelta;
                        pastePoint.y += lDelta;
                    }
                }

                SubregionForStitching subregion = new SubregionForStitching(
                        new Point(scrollX, scrollY),
                        new Point(pastePoint),
                        new Rectangle(physicalCropArea),
                        new Rectangle(logicalCropArea)
                );

                subRegions.add(subregion);

                currentLeft += deltaX;
                scrollX += deltaX;

                if (needHScroll && isLeftEdge) {
                    currentLeft += logicalOverlap;
                }
                isLeftEdge = false;
            }

            currentTop += deltaY;
            scrollY += deltaY;

            if (needVScroll && isTopEdge) {
                currentTop += logicalOverlap;
            }
            isTopEdge = false;
        }

        return subRegions.toArray(new SubregionForStitching[0]);
    }

}
