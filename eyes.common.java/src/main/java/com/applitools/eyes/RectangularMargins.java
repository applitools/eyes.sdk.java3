package com.applitools.eyes;

public class RectangularMargins {
    private int left, top, right, bottom;

    public static final RectangularMargins EMPTY = new RectangularMargins(0,0,0,0);

    public RectangularMargins(int left, int top, int right, int bottom){
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public int getRight() {
        return right;
    }

    public int getBottom() {
        return bottom;
    }
}
