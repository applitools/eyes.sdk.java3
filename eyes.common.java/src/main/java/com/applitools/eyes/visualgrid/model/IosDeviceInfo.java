package com.applitools.eyes.visualgrid.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IosDeviceInfo implements IRenderingBrowserInfo {

    @JsonProperty("name")
    private final IosDeviceName deviceName;
    private final IosScreenOrientation screenOrientation;

    public IosDeviceInfo(IosDeviceName deviceName) {
        this.deviceName = deviceName;
        this.screenOrientation = IosScreenOrientation.PORTRAIT;
    }

    public IosDeviceInfo(IosDeviceName deviceName, IosScreenOrientation screenOrientation) {
        this.deviceName = deviceName;
        this.screenOrientation = screenOrientation;
    }

    public IosDeviceName getDeviceName() {
        return deviceName;
    }

    public IosScreenOrientation getScreenOrientation() {
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
