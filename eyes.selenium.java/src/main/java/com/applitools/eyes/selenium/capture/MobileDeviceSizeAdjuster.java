package com.applitools.eyes.selenium.capture;

import com.applitools.eyes.IEyesJsExecutor;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.Region;
import com.applitools.eyes.ViewportMetaTag;

public class MobileDeviceSizeAdjuster implements ISizeAdjuster {

    private static final String GetViewportMetaTagContentScript =
            "var meta = document.querySelector('head > meta[name=viewport]');" +
                    "var viewport = (meta == null) ? '' : meta.getAttribute('content');" +
                    "return viewport;";

    private final ViewportMetaTag viewportMetaTag;
    private final long innerWidth;

    public MobileDeviceSizeAdjuster(IEyesJsExecutor jsExecutor, long innerWidth) {
        String viewportMetaTagContent = (String)jsExecutor.executeScript(GetViewportMetaTagContentScript);
        viewportMetaTag = ViewportMetaTag.parseViewportMetaTag(viewportMetaTagContent);
        this.innerWidth = innerWidth;
    }

    @Override
    public Region adjustRegion(Region inputRegion, RectangleSize deviceLogicalViewportSize) {
        if (viewportMetaTag.getFollowDeviceWidth() && (innerWidth == 0 || innerWidth == deviceLogicalViewportSize.getWidth())) {
            return inputRegion;
        }

        float widthRatio = (float) inputRegion.getWidth() / deviceLogicalViewportSize.getWidth();
        return new Region(inputRegion.getLeft(), inputRegion.getTop(), deviceLogicalViewportSize.getWidth(), Math.round(inputRegion.getHeight() / widthRatio));
    }
}
