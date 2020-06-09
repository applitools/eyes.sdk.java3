package com.applitools.eyes.visualgrid.model;

import com.applitools.eyes.*;

public class MutableRegion extends Region {

    public MutableRegion(int left, int top, int width, int height) {
        super(left, top, width, height);
    }

    public MutableRegion(int left, int top, int width, int height, CoordinatesType coordinatesType) {
        super(left, top, width, height, coordinatesType);
    }

    public MutableRegion(Location location, RectangleSize size) {
        super(location, size);
    }

    public MutableRegion(Location location, RectangleSize size, CoordinatesType coordinatesType) {
        super(location, size, coordinatesType);
    }

    public MutableRegion(IRegion region) {
        super(region);
    }

    public Region offset(int x, int y) {
        this.setLeft(this.getLeft() + x);
        this.setTop(this.getTop() + y);
        return this;
    }

}
