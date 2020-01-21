package com.applitools.eyes.fluent;

import com.applitools.ICheckSettings;
import com.applitools.eyes.IGetAccessibilityRegion;
import com.applitools.eyes.visualgrid.model.VisualGridSelector;

public interface ICheckSettingsInternal extends com.applitools.ICheckSettingsInternal {

    Boolean getStitchContent();

    GetRegion[] getIgnoreRegions();

    GetRegion[] getStrictRegions();

    GetRegion[] getLayoutRegions();

    GetRegion[] getContentRegions();

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

    IGetAccessibilityRegion[] getAccessibilityRegions();
}
