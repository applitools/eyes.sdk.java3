package com.applitools.eyes.selenium.locators;

import com.applitools.eyes.Logger;
import com.applitools.eyes.UserAgent;
import com.applitools.eyes.capture.ImageProvider;
import com.applitools.eyes.debug.DebugScreenshotsProvider;
import com.applitools.eyes.locators.BaseVisualLocatorsProvider;
import com.applitools.eyes.selenium.SeleniumEyes;
import com.applitools.eyes.selenium.capture.ImageProviderFactory;
import com.applitools.eyes.selenium.wrappers.EyesSeleniumDriver;
import com.applitools.utils.ImageUtils;

import java.awt.image.BufferedImage;

public class SeleniumVisualLocatorsProvider extends BaseVisualLocatorsProvider {

    private final EyesSeleniumDriver driver;
    private final SeleniumEyes eyes;

    public SeleniumVisualLocatorsProvider(SeleniumEyes eyes, EyesSeleniumDriver driver, Logger logger, DebugScreenshotsProvider debugScreenshotsProvider) {
        super(logger, eyes.getServerConnector(), eyes.getDevicePixelRatio(), eyes.getConfiguration().getAppName(), debugScreenshotsProvider);
        this.driver = driver;
        this.eyes = eyes;
    }

    @Override
    protected BufferedImage getViewPortScreenshot() {
        String uaString = driver.getUserAgent();
        UserAgent userAgent = null;
        if (uaString != null) {
            userAgent = UserAgent.parseUserAgentString(uaString, true);
        }
        UserAgent.parseUserAgentString(uaString, true);
        ImageProvider provider = ImageProviderFactory.getImageProvider(userAgent, eyes, logger, driver);
        BufferedImage image = provider.getImage();
        if (eyes.getIsCutProviderExplicitlySet()) {
            image = eyes.getCutProvider().cut(image);
        }

        double scaleRatio = devicePixelRatio;
        if (eyes.getIsScaleProviderExplicitlySet()) {
            scaleRatio = eyes.getScaleProvider().getScaleRatio();
        }
        return ImageUtils.scaleImage(image, 1 / scaleRatio);
    }
}
