package com.applitools.eyes.fluent;

import com.applitools.eyes.*;

import java.util.ArrayList;
import java.util.List;

public class FloatingRegionByRectangle implements GetFloatingRegion {
    private final Region rect;
    private final int maxUpOffset;
    private final int maxDownOffset;
    private final int maxLeftOffset;
    private final int maxRightOffset;

    public FloatingRegionByRectangle(Region rect, int maxUpOffset, int maxDownOffset, int maxLeftOffset, int maxRightOffset) {
        this.rect = rect;
        this.maxUpOffset = maxUpOffset;
        this.maxDownOffset = maxDownOffset;
        this.maxLeftOffset = maxLeftOffset;
        this.maxRightOffset = maxRightOffset;
    }

    @Override
    public List<FloatingMatchSettings> getRegions(EyesScreenshot screenshot) {
        List<FloatingMatchSettings> value = new ArrayList<>();
        value.add(new FloatingMatchSettings(
                rect.getLeft(), rect.getTop(), rect.getWidth(), rect.getHeight(),
                maxUpOffset, maxDownOffset, maxLeftOffset, maxRightOffset));
        return value;
    }
}
