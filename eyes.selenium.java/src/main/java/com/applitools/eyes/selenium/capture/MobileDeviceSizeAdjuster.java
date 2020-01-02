package com.applitools.eyes.selenium.capture;

import com.applitools.eyes.IEyesJsExecutor;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.Region;
import com.applitools.eyes.ViewportMetaTag;

public class MobileDeviceSizeAdjuster implements ISizeAdjuster {

    private final String GetViewportMetaTagContentScript =
            "var meta = document.querySelector('head > meta[name=viewport]');" +
                    "var viewport = (meta == null) ? '' : meta.getAttribute('content');" +
                    "return viewport;";

    protected ViewportMetaTag viewportMetaTag;

    public MobileDeviceSizeAdjuster(IEyesJsExecutor jsExecutor)
    {
        String viewportMetaTagContent = (String)jsExecutor.executeScript(GetViewportMetaTagContentScript);
        viewportMetaTag = ViewportMetaTag.parseViewportMetaTag(viewportMetaTagContent);
    }

    @Override
    public Region adjustRegion(Region inputRegion, RectangleSize deviceLogicalViewportSize) {
        if (viewportMetaTag.getFollowDeviceWidth())
        {
            return inputRegion;
        }
        float widthRatio = (float)inputRegion.getWidth() / deviceLogicalViewportSize.getWidth();
        Region adjustedRegion = new Region(inputRegion.getLeft(), inputRegion.getTop(), deviceLogicalViewportSize.getWidth(), Math.round(inputRegion.getHeight() / widthRatio));
        return adjustedRegion;
    }
}
