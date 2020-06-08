package com.applitools.eyes.visualgrid.model;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.selenium.BrowserType;

public class DesktopBrowserInfo implements IRenderingBrowserInfo {

    private RectangleSize viewportSize;
    private BrowserType browserType;
    private final String platform = "linux";
    private EmulationBaseInfo emulationInfo;
    private IosDeviceInfo iosDeviceInfo;
    private final String sizeMode = "full-page";
    private String baselineEnvName;


    public DesktopBrowserInfo(RectangleSize viewportSize, BrowserType browserType, String baselineEnvName) {
        this.viewportSize = viewportSize;
        this.browserType = browserType;
        this.baselineEnvName = baselineEnvName;
    }

    public DesktopBrowserInfo(RectangleSize viewportSize, BrowserType browserType) {
        this.viewportSize = viewportSize;
        this.browserType = browserType;
    }

    public DesktopBrowserInfo(EmulationBaseInfo emulationInfo, String baselineEnvName) {
        this.emulationInfo = emulationInfo;
        this.baselineEnvName = baselineEnvName;
        this.browserType = BrowserType.CHROME;
    }

    public DesktopBrowserInfo(int width, int height) {
        this(new RectangleSize(width, height), BrowserType.CHROME, null);
    }

    public DesktopBrowserInfo(EmulationBaseInfo emulationInfo) {
        this.emulationInfo = emulationInfo;
        this.browserType = BrowserType.CHROME;
    }

    public DesktopBrowserInfo(int width, int height, BrowserType browserType, String baselineEnvName) {
        this(new RectangleSize(width, height), browserType, baselineEnvName);
    }

    public DesktopBrowserInfo(int width, int height, BrowserType browserType) {
        this(new RectangleSize(width, height), browserType, null);
    }

    public DesktopBrowserInfo(IosDeviceInfo deviceInfo) {
        this.iosDeviceInfo = deviceInfo;
        this.browserType = BrowserType.SAFARI;
    }

    public DesktopBrowserInfo(IosDeviceInfo deviceInfo, String baselineEnvName) {
        this.iosDeviceInfo = deviceInfo;
        this.baselineEnvName = baselineEnvName;
        this.browserType = BrowserType.SAFARI;
    }

    public int getWidth() {
        if (viewportSize != null) {
            return viewportSize.getWidth();
        }
        return 0;
    }

    public int getHeight() {
        if (viewportSize != null) {
            return viewportSize.getHeight();
        }
        return 0;
    }

    public RectangleSize getViewportSize() {
        return viewportSize;
    }

    public BrowserType getBrowserType() {
        return this.browserType;
    }

    public String getPlatform() {
        if (iosDeviceInfo != null) {
            return "ios";
        }

        if (browserType != null) {
            switch (this.browserType) {
                case CHROME:
                case CHROME_ONE_VERSION_BACK:
                case CHROME_TWO_VERSIONS_BACK:
                case FIREFOX:
                case FIREFOX_ONE_VERSION_BACK:
                case FIREFOX_TWO_VERSIONS_BACK:
                    return "linux";
                case SAFARI:
                case SAFARI_ONE_VERSION_BACK:
                case SAFARI_TWO_VERSIONS_BACK:
                    return "mac os x";
                case IE_10:
                case IE_11:
                case EDGE:
                case EDGE_LEGACY:
                case EDGE_CHROMIUM:
                case EDGE_CHROMIUM_ONE_VERSION_BACK:
                    return "windows";
            }
        }
        return "linux";
    }

    public EmulationBaseInfo getEmulationInfo() {
        return emulationInfo;
    }

    public IosDeviceInfo getIosDeviceInfo() {
        return iosDeviceInfo;
    }

    public String getSizeMode() {
        return this.sizeMode;
    }

    @Override
    public String toString() {
        return "RenderBrowserInfo{" +
                "viewportSize=" + viewportSize +
                ", browserType=" + browserType +
                ", platform='" + platform + '\'' +
                ", emulationInfo=" + emulationInfo +
                ", iosDeviceInfo=" + iosDeviceInfo +
                ", sizeMode='" + sizeMode + '\'' +
                '}';
    }

    public String getBaselineEnvName() {
        return baselineEnvName;
    }
}
