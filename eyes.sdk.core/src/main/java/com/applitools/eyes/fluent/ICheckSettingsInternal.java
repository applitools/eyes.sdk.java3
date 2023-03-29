package com.applitools.eyes.fluent;

import com.applitools.ICheckSettings;
import com.applitools.eyes.locators.BaseOcrRegion;
import com.applitools.eyes.positioning.PositionProvider;

public interface ICheckSettingsInternal extends com.applitools.ICheckSettingsInternal {

    Boolean getStitchContent();

    GetRegion[] getIgnoreRegions();

    GetRegion[] getStrictRegions();

    GetRegion[] getLayoutRegions();

    GetRegion[] getContentRegions();

    GetRegion[] getFloatingRegions();

    Boolean getIgnoreCaret();

    Boolean isEnablePatterns();

    @Deprecated
    ICheckSettings scriptHook(String hook);

    ICheckSettings beforeRenderScreenshotHook(String hook);

    Boolean isUseDom();

    Boolean isSendDom();

    Boolean isIgnoreDisplacements();

    GetRegion[] getAccessibilityRegions();

    BaseOcrRegion getOcrRegion();

    String getVariationGroupId();

    PositionProvider getStepPositionProvider();

}
