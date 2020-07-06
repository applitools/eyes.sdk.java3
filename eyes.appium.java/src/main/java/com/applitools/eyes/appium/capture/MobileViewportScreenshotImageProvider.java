package com.applitools.eyes.appium.capture;

import com.applitools.eyes.Logger;
import com.applitools.eyes.capture.ImageProvider;
import com.applitools.utils.ImageUtils;
import org.openqa.selenium.JavascriptExecutor;

import java.awt.image.BufferedImage;

/**
 * An image provider returning viewport screenshots for {@link io.appium.java_client.AppiumDriver}
 */
public class MobileViewportScreenshotImageProvider implements ImageProvider {

    private final Logger logger;
    private final JavascriptExecutor jsExecutor;

    public MobileViewportScreenshotImageProvider(Logger logger, JavascriptExecutor jsExecutor) {
        this.logger = logger;
        this.jsExecutor = jsExecutor;
    }

    @Override
    public BufferedImage getImage() {
        logger.verbose("Getting screenshot as base64...");
        String screenshot64 = (String) jsExecutor.executeScript("mobile: viewportScreenshot");
        logger.verbose("Done getting base64! Creating BufferedImage...");
        return ImageUtils.imageFromBase64(screenshot64);
    }
}