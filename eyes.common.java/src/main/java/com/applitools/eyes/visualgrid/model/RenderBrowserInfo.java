package com.applitools.eyes.visualgrid.model;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.selenium.BrowserType;

public class RenderBrowserInfo extends DesktopBrowserInfo {
    public RenderBrowserInfo(RectangleSize viewportSize, BrowserType browserType, String baselineEnvName) {
        super(viewportSize, browserType, baselineEnvName);
    }

    public RenderBrowserInfo(RectangleSize viewportSize, BrowserType browserType) {
        super(viewportSize, browserType);
    }

    public RenderBrowserInfo(EmulationBaseInfo emulationInfo, String baselineEnvName) {
        super(emulationInfo, baselineEnvName);
    }

    public RenderBrowserInfo(int width, int height) {
        super(width, height);
    }

    public RenderBrowserInfo(EmulationBaseInfo emulationInfo) {
        super(emulationInfo);
    }

    public RenderBrowserInfo(int width, int height, BrowserType browserType, String baselineEnvName) {
        super(width, height, browserType, baselineEnvName);
    }

    public RenderBrowserInfo(int width, int height, BrowserType browserType) {
        super(width, height, browserType);
    }
}
