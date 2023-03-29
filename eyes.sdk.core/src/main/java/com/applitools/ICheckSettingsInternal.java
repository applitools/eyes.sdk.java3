package com.applitools;

import com.applitools.eyes.MatchLevel;
import com.applitools.eyes.Region;
import com.applitools.eyes.visualgrid.model.VisualGridOption;

import java.util.List;
import java.util.Map;

public interface ICheckSettingsInternal {

    Region getTargetRegion();

    Integer getTimeout();

    MatchLevel getMatchLevel();

    String getName();

    Map<String, String> getScriptHooks();

    String getSizeMode();

    Boolean isSendDom();

    Boolean isUseDom();

    ICheckSettingsInternal clone();

    Boolean isStitchContent();

    Boolean isIgnoreDisplacements();

    List<VisualGridOption> getVisualGridOptions();

    Boolean isDisableBrowserFetching();

    ICheckSettings setDisableBrowserFetching(Boolean disableBrowserFetching);
}
