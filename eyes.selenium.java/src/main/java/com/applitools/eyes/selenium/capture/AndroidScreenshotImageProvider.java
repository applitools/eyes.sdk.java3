package com.applitools.eyes.selenium.capture;

import com.applitools.eyes.Logger;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.UserAgent;
import com.applitools.eyes.selenium.SeleniumEyes;
import com.applitools.utils.ImageUtils;
import org.openqa.selenium.TakesScreenshot;

import java.awt.*;
import java.awt.image.BufferedImage;

public class AndroidScreenshotImageProvider extends MobileScreenshotImageProvider {

    public AndroidScreenshotImageProvider(SeleniumEyes eyes, Logger logger, TakesScreenshot tsInstance, UserAgent userAgent) {
        super(eyes, logger, tsInstance, userAgent);
    }

    @Override
    public BufferedImage getImage() {
        BufferedImage image = super.getImage();
        logger.verbose("Bitmap Size: " + image.getWidth() + "," + image.getHeight());

        eyes.getDebugScreenshotsProvider().save(image, "ANDROID");

        if (eyes.getIsCutProviderExplicitlySet()) {
            return image;
        }

        RectangleSize originalViewportSize = getViewportSize();

        logger.verbose("logical viewport size: " + originalViewportSize);

        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        logger.verbose("physical device pixel size: " + imageWidth + "," + imageHeight);

        float widthRatio = image.getWidth() / (float) originalViewportSize.getWidth();
        float height = widthRatio * originalViewportSize.getHeight();
        Rectangle cropRect = new Rectangle(0, 0, imageWidth, Math.round(height));
        image = ImageUtils.cropImage(logger, image, cropRect);

        return image;
    }
}
