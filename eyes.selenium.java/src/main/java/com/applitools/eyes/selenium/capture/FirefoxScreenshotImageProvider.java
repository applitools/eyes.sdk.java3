package com.applitools.eyes.selenium.capture;

import com.applitools.eyes.Logger;
import com.applitools.eyes.capture.ImageProvider;
import com.applitools.eyes.selenium.SeleniumEyes;
import com.applitools.eyes.selenium.frames.FrameChain;
import com.applitools.eyes.selenium.wrappers.EyesTargetLocator;
import com.applitools.eyes.selenium.wrappers.EyesSeleniumDriver;
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

    private final SeleniumEyes eyes;
    private final TakesScreenshot tsInstance;

    public FirefoxScreenshotImageProvider(SeleniumEyes eyes, TakesScreenshot tsInstance) {
        this.eyes = eyes;
        this.tsInstance = tsInstance;
    }

    @Override
    public BufferedImage getImage() {
        EyesSeleniumDriver eyesSeleniumDriver = (EyesSeleniumDriver) eyes.getDriver();
        FrameChain frameChain = eyesSeleniumDriver.getFrameChain().clone();
        eyesSeleniumDriver.switchTo().defaultContent();
        String screenshot64 = tsInstance.getScreenshotAs(OutputType.BASE64);
        BufferedImage image = ImageUtils.imageFromBase64(screenshot64);
        eyes.getDebugScreenshotsProvider().save(image, "FIREFOX");
        ((EyesTargetLocator) eyesSeleniumDriver.switchTo()).frames(frameChain);
        return image;
    }
}
