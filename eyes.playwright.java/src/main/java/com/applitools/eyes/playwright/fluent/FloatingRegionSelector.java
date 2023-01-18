package com.applitools.eyes.playwright.fluent;

import com.applitools.eyes.playwright.universal.dto.Selector;
import com.applitools.eyes.visualgrid.model.IGetFloatingRegionOffsets;

public class FloatingRegionSelector extends Selector implements IGetFloatingRegionOffsets {

    private final int maxUpOffset;
    private final int maxDownOffset;
    private final int maxLeftOffset;
    private final int maxRightOffset;

    public FloatingRegionSelector(String selector, int maxUpOffset, int maxDownOffset, int maxLeftOffset, int maxRightOffset) {
        super(selector);
        this.maxUpOffset = maxUpOffset;
        this.maxDownOffset = maxDownOffset;
        this.maxLeftOffset = maxLeftOffset;
        this.maxRightOffset = maxRightOffset;
    }

    public FloatingRegionSelector(String selector, int maxOffset) {
        super(selector);
        this.maxUpOffset = maxOffset;
        this.maxDownOffset = maxOffset;
        this.maxLeftOffset = maxOffset;
        this.maxRightOffset = maxOffset;
    }

    @Override
    public int getMaxLeftOffset() {
        return maxLeftOffset;
    }

    @Override
    public int getMaxUpOffset() {
        return maxUpOffset;
    }

    @Override
    public int getMaxRightOffset() {
        return maxRightOffset;
    }

    @Override
    public int getMaxDownOffset() {
        return maxDownOffset;
    }
}

