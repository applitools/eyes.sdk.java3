package com.applitools.eyes.visualgrid.model;

public abstract class EmulationBaseInfo {

    ScreenOrientation screenOrientation;

    EmulationBaseInfo(ScreenOrientation screenOrientation) {
        this.screenOrientation = screenOrientation;
    }

    public ScreenOrientation getScreenOrientation() {
        return screenOrientation;
    }

    public void setScreenOrientation(ScreenOrientation screenOrientation) {
        this.screenOrientation = screenOrientation;
    }

    public abstract String getDeviceName();
}
