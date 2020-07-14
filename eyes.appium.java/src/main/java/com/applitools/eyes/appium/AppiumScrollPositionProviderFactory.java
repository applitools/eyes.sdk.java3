package com.applitools.eyes.appium;

import com.applitools.eyes.Logger;
import com.applitools.eyes.selenium.EyesDriverUtils;

public class AppiumScrollPositionProviderFactory {

    private final Logger logger;
    private final EyesAppiumDriver driver;

    public AppiumScrollPositionProviderFactory(Logger logger, EyesAppiumDriver driver) {
        this.logger = logger;
        this.driver = driver;
    }

    public AppiumScrollPositionProvider getScrollPositionProvider() {
        if (EyesDriverUtils.isAndroid(driver.getRemoteWebDriver())) {
            return new AndroidScrollPositionProvider(logger, driver);
        } else if (EyesDriverUtils.isIOS(driver.getRemoteWebDriver())) {
            return new IOSScrollPositionProvider(logger, driver);
        }
        throw new Error("Could not find driver type to get scroll position provider");
    }
}
