package com.applitools.eyes.visualgrid.model;

public class EmulationDevice extends EmulationBaseInfo{

    private int width;
    private int height;
    private double deviceScaleFactor;

    public EmulationDevice(int width, int height, double deviceScaleFactor, ScreenOrientation screenOrientation) {
        super(screenOrientation);
        this.width = width;
        this.height = height;
        this.deviceScaleFactor = deviceScaleFactor;
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

    public double getDeviceScaleFactor() {
        return deviceScaleFactor;
    }

    public void setDeviceScaleFactor(float deviceScaleFactor) {
        this.deviceScaleFactor = deviceScaleFactor;
    }

    @Override
    public String getDeviceName() {
        return null;
    }
}
