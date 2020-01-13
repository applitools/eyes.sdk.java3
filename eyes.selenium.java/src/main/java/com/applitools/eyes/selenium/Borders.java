package com.applitools.eyes.selenium;

public class Borders {
    private int left;
    private int top;
    private int right;
    private int bottom;

    public Borders(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public Borders() {
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

    public int getHorizontal() {
        return left + right;
    }

    public int getVertical() {
        return top + bottom;
    }
}
