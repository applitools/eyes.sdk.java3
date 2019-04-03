package com.applitools;

import com.applitools.eyes.MatchLevel;
import com.applitools.eyes.Region;
import com.applitools.eyes.visualgrid.model.VisualGridSelector;

import java.util.Map;

public interface ICheckSettingsInternal {
    Region getTargetRegion();

    int getTimeout();

    MatchLevel getMatchLevel();

    String getName();

    Map<String, String> getScriptHooks();

    String getSizeMode();

    Region getRegion();

    boolean isSendDom();

    void setSizeMode(String sizeMode);

    VisualGridSelector GetTargetSelector();
}
