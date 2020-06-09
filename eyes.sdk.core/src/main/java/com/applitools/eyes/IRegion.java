package com.applitools.eyes;

public interface IRegion {

    int getLeft();

    int getTop();

    RectangleSize getSize();

    int getArea();

    Location getLocation();

    Region offset(int dx, int dy);

    int getWidth();

    int getHeight();

    CoordinatesType getCoordinatesType();
}
