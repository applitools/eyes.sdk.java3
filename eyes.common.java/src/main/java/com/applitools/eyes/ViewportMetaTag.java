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

    public static ViewportMetaTag parseViewportMetaTag(String viewportMetaTagContent) {
        ViewportMetaTag viewportData = new ViewportMetaTag();
        Matcher match = viewportParsingRegex.matcher(viewportMetaTagContent);
        String widthStr = null;
        String initialScaleStr = null;
        while (match.find() && (widthStr == null || initialScaleStr == null)) {
            String matchResult = match.group("width");
            if (widthStr == null && matchResult != null) {
                widthStr = matchResult;
            }
            matchResult = match.group("initialScale");
            if (initialScaleStr == null && matchResult != null) {
                initialScaleStr = matchResult;
            }
        }
        viewportData.followDeviceWidth = "device-width".equalsIgnoreCase(widthStr);
        if (!viewportData.followDeviceWidth && widthStr != null) {
            try {
                viewportData.deviceWidth = Float.parseFloat(widthStr);
            } finally {
            }
        }
        if (initialScaleStr != null) {
            try {
                viewportData.initialScale = Float.parseFloat(initialScaleStr);
            } finally {
            }
        }
        return viewportData;
    }

}
