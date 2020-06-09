package com.applitools.eyes.visualgrid.model;

public class ChromeEmulationInfo extends EmulationBaseInfo implements IRenderingBrowserInfo {

    private DeviceName deviceName;

    public ChromeEmulationInfo(DeviceName deviceName, ScreenOrientation screenOrientation) {
        super(screenOrientation);
        this.deviceName = deviceName;
    }

    public String getDeviceName() {
        return deviceName.getName();
    }

    public void setDeviceName(DeviceName deviceName) {
        this.deviceName = deviceName;
    }

    public ScreenOrientation getScreenOrientation() {
        return screenOrientation;
    }

    public void setScreenOrientation(ScreenOrientation screenOrientation) {
        this.screenOrientation = screenOrientation;
    }

    @Override
    public String toString() {
        return "ChromeEmulationInfo{" +
                "deviceName=" + deviceName +
                ", screenOrientation=" + screenOrientation +
                '}';
    }
}
