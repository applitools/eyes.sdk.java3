package com.applitools.eyes.fluent;

import com.applitools.ICheckSettings;
import com.applitools.eyes.GetAccessibilityRegion;
import com.applitools.eyes.locators.BaseOcrRegion;
import com.applitools.eyes.visualgrid.model.VisualGridSelector;

public interface ICheckSettingsInternal extends com.applitools.ICheckSettingsInternal {

    Boolean getStitchContent();

    GetSimpleRegion[] getIgnoreRegions();

    GetSimpleRegion[] getStrictRegions();

    GetSimpleRegion[] getLayoutRegions();

    GetSimpleRegion[] getContentRegions();

    GetFloatingRegion[] getFloatingRegions();

    Boolean getIgnoreCaret();

    Boolean isEnablePatterns();

    VisualGridSelector getVGTargetSelector();

    @Deprecated
    ICheckSettings scriptHook(String hook);

    ICheckSettings beforeRenderScreenshotHook(String hook);

    Boolean isUseDom();

    Boolean isSendDom();

    Boolean isIgnoreDisplacements();

    GetAccessibilityRegion[] getAccessibilityRegions();

    BaseOcrRegion getOcrRegion();

    String getVariationGroupId();
}
