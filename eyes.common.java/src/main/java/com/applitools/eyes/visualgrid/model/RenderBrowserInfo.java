package com.applitools.eyes.visualgrid.model;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.selenium.BrowserType;

public class RenderBrowserInfo {

    private RectangleSize viewportSize;
    private BrowserType browserType;
    private final String platform = "linux";
    private EmulationBaseInfo emulationInfo;
    private final String sizeMode = "full-page";
    private String baselineEnvName;


    public RenderBrowserInfo(RectangleSize viewportSize, BrowserType browserType, String baselineEnvName) {
        this.viewportSize = viewportSize;
        this.browserType = browserType;
        this.baselineEnvName = baselineEnvName;
    }

    public RenderBrowserInfo(RectangleSize viewportSize, BrowserType browserType) {
        this.viewportSize = viewportSize;
        this.browserType = browserType;
    }

    public RenderBrowserInfo(EmulationBaseInfo emulationInfo, String baselineEnvName) {
        this.emulationInfo = emulationInfo;
        this.baselineEnvName = baselineEnvName;
    }

    public RenderBrowserInfo(int width, int height) {
        this(new RectangleSize(width, height), BrowserType.CHROME, null);
    }
    public RenderBrowserInfo(EmulationBaseInfo emulationInfo) {
        this.emulationInfo = emulationInfo;
    }

    public RenderBrowserInfo(int width, int height, BrowserType browserType, String baselineEnvName) {
        this(new RectangleSize(width, height), browserType, baselineEnvName);
    }

    public RenderBrowserInfo(int width, int height, BrowserType browserType) {
        this(new RectangleSize(width, height), browserType, null);
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

    public String getBrowserType() {
        if (browserType != null) {
            switch (this.browserType) {
                case CHROME:
                    return "chrome";
                case FIREFOX:
                    return "firefox";
                case IE_10:
                    return "ie10";
                case IE_11:
                    return "ie";
                case EDGE:
                    return "edge";
            }
        }
        return "chrome";
    }

    public String getPlatform() {
        if (browserType != null) {
            switch (this.browserType) {
                case CHROME:
                case FIREFOX:
                    return "linux";
                case IE_10:
                case IE_11:
                case EDGE:
                    return "windows";
            }
        }
        return "linux";
    }

    public EmulationBaseInfo getEmulationInfo() {
        return emulationInfo;
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
                ", sizeMode='" + sizeMode + '\'' +
                '}';
    }

    public String getBaselineEnvName() {
        return baselineEnvName;
    }
}
