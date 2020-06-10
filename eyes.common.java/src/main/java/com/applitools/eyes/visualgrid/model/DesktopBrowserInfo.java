package com.applitools.eyes.visualgrid.model;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.selenium.BrowserType;

public class DesktopBrowserInfo implements IRenderingBrowserInfo {

    private final RenderBrowserInfo renderBrowserInfo;

    public DesktopBrowserInfo(RectangleSize viewportSize, BrowserType browserType, String baselineEnvName) {
        renderBrowserInfo = new RenderBrowserInfo(viewportSize, browserType, baselineEnvName);
    }

    public DesktopBrowserInfo(RectangleSize viewportSize, BrowserType browserType) {
        renderBrowserInfo = new RenderBrowserInfo(viewportSize, browserType);
    }

    public DesktopBrowserInfo(int width, int height) {
        renderBrowserInfo = new RenderBrowserInfo(width, height);
    }

    public DesktopBrowserInfo(int width, int height, BrowserType browserType, String baselineEnvName) {
        renderBrowserInfo = new RenderBrowserInfo(width, height, browserType, baselineEnvName);
    }

    public DesktopBrowserInfo(int width, int height, BrowserType browserType) {
        renderBrowserInfo = new RenderBrowserInfo(width, height, browserType);
    }

    public RenderBrowserInfo getRenderBrowserInfo() {
        return renderBrowserInfo;
    }
}
