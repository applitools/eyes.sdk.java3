package com.applitools.eyes.appium.locators;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.Logger;
import com.applitools.eyes.Region;
import com.applitools.eyes.appium.EyesAppiumDriver;
import com.applitools.eyes.appium.capture.MobileScreenshotProvider;
import com.applitools.eyes.debug.DebugScreenshotsProvider;
import com.applitools.eyes.locators.BaseVisualLocatorsProvider;
import com.applitools.eyes.locators.VisualLocatorSettings;

import java.util.List;
import java.util.Map;

public abstract class MobileVisualLocatorProvider extends BaseVisualLocatorsProvider {

    protected EyesAppiumDriver driver;

    MobileVisualLocatorProvider(Logger logger, String testId, EyesAppiumDriver driver, ServerConnector serverConnector,
                                double devicePixelRatio, String appName, DebugScreenshotsProvider debugScreenshotsProvider) {
        super(logger, testId, serverConnector, new MobileScreenshotProvider(driver, devicePixelRatio), devicePixelRatio, appName, debugScreenshotsProvider);
        this.driver = driver;
    }

    @Override
    public Map<String, List<Region>> getLocators(VisualLocatorSettings visualLocatorSettings) {
        return adjustVisualLocators(super.getLocators(visualLocatorSettings));
    }

    protected abstract Map<String, List<Region>> adjustVisualLocators(Map<String, List<Region>> map);
}
