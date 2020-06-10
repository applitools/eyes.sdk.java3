package com.applitools.eyes.selenium;

import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.visualgrid.model.RenderBrowserInfo;

import java.util.List;

public interface IConfigurationGetter extends com.applitools.eyes.config.IConfigurationGetter {

    Boolean getForceFullPageScreenshot();

    int getWaitBeforeScreenshots();

    StitchMode getStitchMode();

    boolean getHideScrollbars();

    boolean getHideCaret();

    List<RenderBrowserInfo> getBrowsersInfo();

    String getTestName();

    Boolean isForceFullPageScreenshot();

    boolean isRenderingConfig();

    Configuration cloneConfig();

    boolean isVisualGrid();
}
