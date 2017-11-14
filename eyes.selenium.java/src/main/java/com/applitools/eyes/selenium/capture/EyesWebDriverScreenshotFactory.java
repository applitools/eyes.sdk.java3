package com.applitools.eyes.selenium.capture;

import com.applitools.eyes.EyesScreenshot;
import com.applitools.eyes.capture.EyesScreenshotFactory;
import com.applitools.eyes.Logger;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;

import java.awt.image.BufferedImage;

/**
 * Encapsulates the instantiation of an {@link EyesWebDriverScreenshot} .
 */
public class EyesWebDriverScreenshotFactory implements EyesScreenshotFactory {
    private final Logger logger;
    private final EyesWebDriver driver;

    public EyesWebDriverScreenshotFactory(Logger logger, EyesWebDriver driver) {
        this.logger = logger;
        this.driver = driver;
    }

    public EyesScreenshot makeScreenshot(BufferedImage image) {
        return new EyesWebDriverScreenshot(logger, driver, image);
    }
}
