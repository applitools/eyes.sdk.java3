package com.applitools.eyes.selenium;

import com.applitools.eyes.Logger;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.selenium.frames.FrameChain;
import com.applitools.eyes.selenium.wrappers.EyesSeleniumDriver;
import com.applitools.eyes.selenium.wrappers.EyesTargetLocator;

class CaretVisibilityProvider {
    private final Logger logger;
    private final EyesSeleniumDriver driver;
    private final Configuration configuration;

    private Object activeElement = null;
    private FrameChain frameChain;

    private static final String HIDE_CARET = "var activeElement = document.activeElement; activeElement && activeElement.blur(); return activeElement;";

    public CaretVisibilityProvider(Logger logger, EyesSeleniumDriver driver, Configuration configuration)
    {
        this.logger = logger;
        this.driver = driver;
        this.configuration = configuration;
    }

    public void hideCaret()
    {
        if (!EyesDriverUtils.isMobileDevice(driver) && configuration.getHideCaret())
        {
            frameChain = driver.getFrameChain().clone();
            logger.verbose("Hiding caret. driver.FrameChain.Count: " + frameChain.size());
            activeElement = driver.executeScript(HIDE_CARET);
        }
    }

    public void restoreCaret()
    {
        if (!EyesDriverUtils.isMobileDevice(driver) && configuration.getHideCaret() && activeElement != null) {
            logger.verbose("Restoring caret. driver.FrameChain.Count: " + driver.getFrameChain().size());
            ((EyesTargetLocator) driver.switchTo()).frames(frameChain);
            driver.executeScript("arguments[0].focus();", activeElement);
        }
    }
}
