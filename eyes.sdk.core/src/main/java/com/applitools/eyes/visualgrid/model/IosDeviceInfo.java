package com.applitools.eyes.visualgrid.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IosDeviceInfo implements IRenderingBrowserInfo {

    @JsonProperty("name")
    private final IosDeviceName deviceName;
    private final ScreenOrientation screenOrientation;

    public IosDeviceInfo(IosDeviceName deviceName) {
        this.deviceName = deviceName;
        this.screenOrientation = ScreenOrientation.PORTRAIT;
    }

    public IosDeviceInfo(IosDeviceName deviceName, ScreenOrientation screenOrientation) {
        this.deviceName = deviceName;
        this.screenOrientation = screenOrientation;
    }

    public String getDeviceName() {
        return deviceName.getName();
    }

    public ScreenOrientation getScreenOrientation() {
        return screenOrientation;
    }

    @Override
    public String toString() {
        return "IosDeviceInfo{" +
                "deviceName=" + deviceName +
                ", screenOrientation=" + screenOrientation +
                '}';
    }
}
