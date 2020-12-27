package com.applitools.eyes.selenium.capture;

import com.applitools.eyes.Logger;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.UserAgent;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.TraceLevel;
import com.applitools.eyes.logging.Type;
import com.applitools.eyes.selenium.SeleniumEyes;
import com.applitools.utils.ImageUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.TakesScreenshot;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collections;

public class AndroidScreenshotImageProvider extends MobileScreenshotImageProvider {

    public AndroidScreenshotImageProvider(SeleniumEyes eyes, Logger logger, TakesScreenshot tsInstance, UserAgent userAgent) {
        super(eyes, logger, tsInstance, userAgent);
    }

    @Override
    public BufferedImage getImage() {
        BufferedImage image = super.getImage();
        logger.log(TraceLevel.Info, Collections.singleton(eyes.getTestId()), Stage.CHECK, Type.CAPTURE_SCREENSHOT,
                Pair.of("imageSize", new RectangleSize(image.getWidth(), image.getHeight())));

        eyes.getDebugScreenshotsProvider().save(image, "ANDROID");

        if (eyes.getIsCutProviderExplicitlySet()) {
            return image;
        }

        RectangleSize originalViewportSize = getViewportSize();
        logger.log(TraceLevel.Info, Collections.singleton(eyes.getTestId()), Stage.CHECK, Type.CAPTURE_SCREENSHOT,
                Pair.of("originalViewportSize", originalViewportSize));

        float widthRatio = image.getWidth() / (float) originalViewportSize.getWidth();
        float height = widthRatio * originalViewportSize.getHeight();
        Rectangle cropRect = new Rectangle(0, 0, image.getWidth(), Math.round(height));
        image = ImageUtils.cropImage(logger, image, cropRect);
        return image;
    }
}
