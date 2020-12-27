package com.applitools.eyes.selenium.capture;

import com.applitools.eyes.capture.ImageProvider;
import com.applitools.eyes.Logger;
import com.applitools.utils.ImageUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.awt.image.BufferedImage;

/**
 * An image provider based on WebDriver's {@link TakesScreenshot} interface.
 */
public class TakesScreenshotImageProvider implements ImageProvider {

    protected final Logger logger;
    protected final TakesScreenshot tsInstance;

    public TakesScreenshotImageProvider(Logger logger, TakesScreenshot tsInstance) {
        this.logger = logger;
        this.tsInstance = tsInstance;
    }

    @Override
    public BufferedImage getImage() {
        String screenshot64 = tsInstance.getScreenshotAs(OutputType.BASE64);
        return ImageUtils.imageFromBase64(screenshot64);
    }
}
