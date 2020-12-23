package com.applitools.eyes.visualgrid.model;

import com.applitools.eyes.RectangleSize;

public abstract class EmulationBaseInfo {

    ScreenOrientation screenOrientation;
    RectangleSize size = null;

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
