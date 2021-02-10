package com.applitools.eyes.selenium.capture;

import com.applitools.eyes.Logger;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.UserAgent;
import com.applitools.eyes.capture.ImageProvider;
import com.applitools.eyes.capture.ScreenshotProvider;
import com.applitools.eyes.debug.DebugScreenshotsProvider;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.selenium.SeleniumEyes;
import com.applitools.eyes.selenium.wrappers.EyesSeleniumDriver;
import com.applitools.utils.ImageUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.image.BufferedImage;

public class SeleniumScreenshotProvider implements ScreenshotProvider {
    private final SeleniumEyes eyes;
    private final EyesSeleniumDriver driver;
    private final Logger logger;
    private final DebugScreenshotsProvider debugScreenshotsProvider;

    public SeleniumScreenshotProvider(SeleniumEyes eyes, EyesSeleniumDriver driver, Logger logger, DebugScreenshotsProvider debugScreenshotsProvider) {
        this.eyes = eyes;
        this.driver = driver;
        this.logger = logger;
        this.debugScreenshotsProvider = debugScreenshotsProvider;
    }

    @Override
    public BufferedImage getViewPortScreenshot(Stage stage) {
        String uaString = driver.getUserAgent();
        UserAgent userAgent = null;
        if (uaString != null) {
            userAgent = UserAgent.parseUserAgentString(uaString, true);
        }
        UserAgent.parseUserAgentString(uaString, true);
        ImageProvider provider = ImageProviderFactory.getImageProvider(userAgent, eyes, logger, driver);
        BufferedImage image = provider.getImage();
        logger.log(eyes.getTestId(), stage, Pair.of("imageSize", new RectangleSize(image.getWidth(), image.getHeight())));
        debugScreenshotsProvider.save(image, "initial");
        if (eyes.getIsCutProviderExplicitlySet()) {
            image = eyes.getCutProvider().cut(image);
            logger.log(eyes.getTestId(), stage, Pair.of("croppedImageSize", new RectangleSize(image.getWidth(), image.getHeight())));
            debugScreenshotsProvider.save(image, "cut");
        }

        double scaleRatio = 1 / eyes.getDevicePixelRatio();
        if (eyes.getIsScaleProviderExplicitlySet()) {
            scaleRatio = eyes.getScaleProvider().getScaleRatio();
        }

        logger.log(eyes.getTestId(), stage, Pair.of("scaleRatio", scaleRatio));
        return ImageUtils.scaleImage(image, scaleRatio);
    }
}
