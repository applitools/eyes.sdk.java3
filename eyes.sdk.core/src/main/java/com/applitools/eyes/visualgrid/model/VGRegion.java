package com.applitools.eyes.visualgrid.model;

import com.applitools.eyes.*;

public class VGRegion implements IRegion{

    public int x;
    public int y;
    public int width;
    public int height;
    public String error = null;

    public Location Location() {
        return new Location(x, y);
    }


    @Override
    public int getLeft() {
        return x;
    }

    public int getRight() {
        return x + width;
    }

    @Override
    public int getTop() {
        return y;
    }

    public int getBottom() {
        return y + height;
    }

    @Override
    public RectangleSize getSize() {
        return new RectangleSize(width, height);
    }

    @Override
    public int getArea() {
        return width * height;
    }

    public Region getRegion() {
        return new Region(x, y, width, height);
    }

    @Override
    public Location getLocation() {
        return new Location(x, y);
    }

    @Override
    public Region offset(int dx, int dy) {
        Region r = getRegion();
        r.offset(x, y);
        return new Region(r);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public CoordinatesType getCoordinatesType() {
        return null;
    }


    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return error == null ? "(" + x + "," + y + ") [" + width + "x" + height + "]" : "Error: {" + error + "}";
    }


}
