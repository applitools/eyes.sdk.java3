package com.applitools.eyes;

import java.awt.*;

public class SubregionForStitching {
    private final Point scrollTo;
    private final Point pastePhysicalLocation;
    private final Rectangle physicalCropArea;
    private final Rectangle logicalCropArea;

    public SubregionForStitching(Point scrollTo, Point pastePhysicalLocation, Rectangle physicalCropArea, Rectangle logicalCropArea) {

        this.scrollTo = scrollTo;
        this.pastePhysicalLocation = pastePhysicalLocation;
        this.physicalCropArea = physicalCropArea;
        this.logicalCropArea = logicalCropArea;
    }

    public Point getScrollTo() {
        return scrollTo;
    }

    public Point getPastePhysicalLocation() {
        return pastePhysicalLocation;
    }

    public Rectangle getPhysicalCropArea() {
        return physicalCropArea;
    }

    public Rectangle getLogicalCropArea() {
        return logicalCropArea;
    }

    @Override
    public String toString() {
        return String.format("scrollTo: (%d, %d) ; pastePhysicalLocation: (%d, %d) ; physicalCropArea: (%d, %d) (%d x %d) ; logicalCropArea: (%d, %d) (%d x %d)",
                scrollTo.x, scrollTo.y,
                pastePhysicalLocation.x, pastePhysicalLocation.y,
                physicalCropArea.x, physicalCropArea.y, physicalCropArea.width, physicalCropArea.height,
                logicalCropArea.x, logicalCropArea.y, logicalCropArea.width, logicalCropArea.height);
    }

    @Override
    public int hashCode() {
        return this.scrollTo.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SubregionForStitching)) {
            return false;
        }
        SubregionForStitching other = (SubregionForStitching) obj;
        return this.scrollTo.equals(other.scrollTo) &&
                this.pastePhysicalLocation.equals(other.pastePhysicalLocation) &&
                this.physicalCropArea.equals(other.physicalCropArea) &&
                this.logicalCropArea.equals(other.logicalCropArea);
    }
}
