package com.applitools.eyes.appium;

import com.applitools.eyes.EyesScreenshot;
import com.applitools.eyes.Logger;
import com.applitools.eyes.capture.EyesScreenshotFactory;

import java.awt.image.BufferedImage;

public class EyesAppiumScreenshotFactory implements EyesScreenshotFactory {

    private final Logger logger;
    private final EyesAppiumDriver driver;

    public EyesAppiumScreenshotFactory(Logger logger, EyesAppiumDriver driver) {
        this.logger = logger;
        this.driver = driver;
    }

    public EyesScreenshot makeScreenshot(BufferedImage image) {
        return new EyesAppiumScreenshot(logger, driver, image);
    }
}
