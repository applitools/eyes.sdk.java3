package com.applitools.eyes.visualgrid.model.deviceinfo;

public class IosDeviceInfo implements DeviceInfo {

    private final IosDeviceName name;
    private final IosScreenOrientation screenOrientation;

    public IosDeviceInfo(IosDeviceName name) {
        this.name = name;
        this.screenOrientation = IosScreenOrientation.PORTRAIT;
    }

    public IosDeviceInfo(IosDeviceName name, IosScreenOrientation screenOrientation) {
        this.name = name;
        this.screenOrientation = screenOrientation;
    }

    public IosDeviceName getName() {
        return name;
    }

    public IosScreenOrientation getScreenOrientation() {
        return screenOrientation;
    }

    @Override
    public String toString() {
        return "IosDeviceInfo{" +
                "deviceName=" + name +
                ", screenOrientation=" + screenOrientation +
                '}';
    }
}
