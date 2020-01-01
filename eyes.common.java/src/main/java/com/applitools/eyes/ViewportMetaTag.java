package com.applitools.eyes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViewportMetaTag {

    private static final Pattern viewportParsingRegex = Pattern.compile(
            "(width\\W*=\\W*(?<width>[a-zA-Z0-9\\.-]*))?,?\\W*(initial-scale\\W*=\\W*(?<initialScale>[a-zA-Z0-9\\.-]*))?");

    private float deviceWidth;
    private float initialScale;
    private boolean followDeviceWidth;

    public boolean getFollowDeviceWidth() {
        return followDeviceWidth;
    }

    public float getDeviceWidth() {
        return deviceWidth;
    }

    public float getInitialScale() {
        return initialScale;
    }

    private ViewportMetaTag() {
    }

    public static ViewportMetaTag ParseViewportMetaTag(String viewportMetaTagContent) {
        Matcher viewportMatches = viewportParsingRegex.matcher(viewportMetaTagContent);
        String widthStr = viewportMatches.group("width");
        String initialScaleStr = viewportMatches.group("initialScale");
        ViewportMetaTag viewportData = new ViewportMetaTag();
        viewportData.followDeviceWidth = "device-width".equalsIgnoreCase(widthStr);
        if (!viewportData.followDeviceWidth) {
            viewportData.deviceWidth = Float.parseFloat(widthStr);
        }
        viewportData.initialScale = Float.parseFloat(initialScaleStr);
        return viewportData;
    }

}
