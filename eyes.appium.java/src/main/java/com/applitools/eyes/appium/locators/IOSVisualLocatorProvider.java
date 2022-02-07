package com.applitools.eyes.appium.locators;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.Logger;
import com.applitools.eyes.Region;
import com.applitools.eyes.appium.EyesAppiumDriver;
import com.applitools.eyes.debug.DebugScreenshotsProvider;

import java.util.List;
import java.util.Map;

public class IOSVisualLocatorProvider extends MobileVisualLocatorProvider {

    public IOSVisualLocatorProvider(Logger logger, String testId, EyesAppiumDriver driver, ServerConnector serverConnector,
                             double devicePixelRatio, String appName, DebugScreenshotsProvider debugScreenshotsProvider) {
        super(logger, testId, driver, serverConnector, devicePixelRatio, appName, debugScreenshotsProvider);
    }

    @Override
    protected Map<String, List<Region>> adjustVisualLocators(Map<String, List<Region>> map) {
        return map;
    }
}
