package com.applitools.eyes.selenium;

import com.applitools.eyes.Borders;
import com.applitools.eyes.RectangleSize;

public class SizeAndBorders {

    private RectangleSize size;
    private Borders borders;

    public SizeAndBorders(int width, int height, int left, int top, int right, int bottom) {
        size = new RectangleSize(width, height);
        borders = new Borders(left, top, right, bottom);
    }

    public RectangleSize getSize() {
        return size;
    }

    public Borders getBorders() {
        return borders;
    }
}
