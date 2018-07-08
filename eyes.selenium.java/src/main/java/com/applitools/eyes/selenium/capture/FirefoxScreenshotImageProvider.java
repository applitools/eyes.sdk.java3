package com.applitools.eyes.selenium.capture;

import com.applitools.eyes.Location;
import com.applitools.eyes.Logger;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.Region;
import com.applitools.eyes.capture.ImageProvider;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.frames.Frame;
import com.applitools.eyes.selenium.frames.FrameChain;
import com.applitools.eyes.selenium.wrappers.EyesTargetLocator;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import com.applitools.utils.ImageUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.awt.image.BufferedImage;

/**
 * This class is needed because in certain versions of firefox, a frame screenshot only brings the frame viewport.
 * To solve this issue, we create an image with the full size of the browser viewport and place the frame image
 * on it in the appropriate place.
 *
 */
public class FirefoxScreenshotImageProvider implements ImageProvider {

    private final Eyes eyes;
    private final Logger logger;
    private final TakesScreenshot tsInstance;

    public FirefoxScreenshotImageProvider(Eyes eyes, Logger logger, TakesScreenshot tsInstance) {
        this.eyes = eyes;
        this.logger = logger;
        this.tsInstance = tsInstance;
    }

    @Override
    public BufferedImage getImage() {
        EyesWebDriver eyesWebDriver = (EyesWebDriver) eyes.getDriver();
        FrameChain frameChain = eyesWebDriver.getFrameChain().clone();
        logger.verbose("frameChain size: " + frameChain.size());
        logger.verbose("Switching temporarily to default content.");
        eyesWebDriver.switchTo().defaultContent();

        logger.verbose("Getting screenshot as base64.");
        String screenshot64 = tsInstance.getScreenshotAs(OutputType.BASE64);
        logger.verbose("Done getting base64! Creating BufferedImage...");

        BufferedImage image = ImageUtils.imageFromBase64(screenshot64);
        eyes.getDebugScreenshotsProvider().save(image, "FIREFOX");

        logger.verbose("Done. Switching back to original frame.");
        ((EyesTargetLocator)eyesWebDriver.switchTo()).frames(frameChain);

        return image;
    }
}
