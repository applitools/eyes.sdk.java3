package com.applitools.eyes.config;

public class ContentInset {

    private int top;
    private int left;
    private int bottom;
    private int right;

    public ContentInset() {
        this.top = 0;
        this.left = 0;
        this.bottom = 0;
        this.right = 0;
    }

    public ContentInset(int top, int left, int bottom, int right) {
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }
}
