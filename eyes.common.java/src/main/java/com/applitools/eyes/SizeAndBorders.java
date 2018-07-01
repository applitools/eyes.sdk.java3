package com.applitools.eyes;

public class SizeAndBorders {

    private RectangleSize size;
    private RectangularMargins borders;

    public SizeAndBorders(RectangleSize size, RectangularMargins borders){
        this.size = size;
        this.borders = borders;
    }

    public RectangleSize getSize() {
        return size;
    }

    public RectangularMargins getBorders() {
        return borders;
    }
}
