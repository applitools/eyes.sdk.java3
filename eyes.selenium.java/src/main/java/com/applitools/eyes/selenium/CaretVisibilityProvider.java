package com.applitools.eyes.selenium;

import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.selenium.frames.FrameChain;
import com.applitools.eyes.selenium.wrappers.EyesSeleniumDriver;
import com.applitools.eyes.selenium.wrappers.EyesTargetLocator;

class CaretVisibilityProvider {
    private final EyesSeleniumDriver driver;
    private final Configuration configuration;

    private Object activeElement = null;
    private FrameChain frameChain;

    private static final String HIDE_CARET = "var activeElement = document.activeElement; activeElement && activeElement.blur(); return activeElement;";

    public CaretVisibilityProvider(EyesSeleniumDriver driver, Configuration configuration) {
        this.driver = driver;
        this.configuration = configuration;
    }

    public void hideCaret() {
        if (!EyesDriverUtils.isMobileDevice(driver) && configuration.getHideCaret()) {
            frameChain = driver.getFrameChain().clone();
            activeElement = driver.executeScript(HIDE_CARET);
        }
    }

    public void restoreCaret() {
        if (!EyesDriverUtils.isMobileDevice(driver) && configuration.getHideCaret() && activeElement != null) {
            ((EyesTargetLocator) driver.switchTo()).frames(frameChain);
            driver.executeScript("arguments[0].focus();", activeElement);
        }
    }
}
