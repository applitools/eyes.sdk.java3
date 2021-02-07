package com.applitools.eyes.locators;

import java.util.Objects;

public class TextRegion {
    private int x;
    private int y;
    private int width;
    private int height;
    private String text;

    public TextRegion() {}

    public TextRegion(int x, int y, int width, int height, String text) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextRegion region = (TextRegion) o;
        return x == region.x && y == region.y && width == region.width && height == region.height && Objects.equals(text, region.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, width, height, text);
    }
}
