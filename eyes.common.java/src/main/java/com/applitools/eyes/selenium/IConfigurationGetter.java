package com.applitools.eyes.selenium;

import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.visualgrid.model.DesktopBrowserInfo;

import java.util.List;

public interface IConfigurationGetter extends com.applitools.eyes.config.IConfigurationGetter {

    Boolean getForceFullPageScreenshot();

    int getWaitBeforeScreenshots();

    StitchMode getStitchMode();

    boolean getHideScrollbars();

    boolean getHideCaret();

    List<DesktopBrowserInfo> getBrowsersInfo();

    String getTestName();

    Boolean isForceFullPageScreenshot();

    boolean isRenderingConfig();

    Configuration cloneConfig();

    boolean isVisualGrid();
}
