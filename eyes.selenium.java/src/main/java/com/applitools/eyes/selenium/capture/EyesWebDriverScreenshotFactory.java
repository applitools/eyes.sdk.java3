package com.applitools.eyes.selenium.capture;

import com.applitools.eyes.EyesScreenshot;
import com.applitools.eyes.Location;
import com.applitools.eyes.capture.EyesScreenshotFactory;
import com.applitools.eyes.Logger;
import com.applitools.eyes.selenium.wrappers.EyesSeleniumDriver;

import java.awt.image.BufferedImage;

/**
 * Encapsulates the instantiation of an {@link EyesWebDriverScreenshot} .
 */
public class EyesWebDriverScreenshotFactory implements EyesScreenshotFactory {
    private final Logger logger;
    private final EyesSeleniumDriver driver;

    public EyesWebDriverScreenshotFactory(Logger logger, EyesSeleniumDriver driver) {
        this.logger = logger;
        this.driver = driver;
    }

    public EyesScreenshot makeScreenshot(BufferedImage image) {
        return new EyesWebDriverScreenshot(logger, driver, image, EyesWebDriverScreenshot.ScreenshotType.VIEWPORT, Location.ZERO);
    }
}
