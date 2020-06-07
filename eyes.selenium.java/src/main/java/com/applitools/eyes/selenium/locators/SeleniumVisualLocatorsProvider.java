package com.applitools.eyes.selenium.locators;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.Logger;
import com.applitools.eyes.Region;
import com.applitools.eyes.UserAgent;
import com.applitools.eyes.capture.ImageProvider;
import com.applitools.eyes.debug.DebugScreenshotsProvider;
import com.applitools.eyes.locators.VisualLocatorSettings;
import com.applitools.eyes.locators.VisualLocatorsData;
import com.applitools.eyes.locators.VisualLocatorsProvider;
import com.applitools.eyes.selenium.SeleniumEyes;
import com.applitools.eyes.selenium.capture.ImageProviderFactory;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SeleniumVisualLocatorsProvider implements VisualLocatorsProvider {

    protected Logger logger;
    private final ServerConnector serverConnector;
    private final SeleniumEyes eyes;
    private final  EyesWebDriver driver;
    private final DebugScreenshotsProvider debugScreenshotsProvider;

    public SeleniumVisualLocatorsProvider(SeleniumEyes eyes, EyesWebDriver driver, Logger logger, DebugScreenshotsProvider debugScreenshotsProvider) {
        this.driver = driver;
        this.eyes = eyes;
        this.serverConnector = eyes.getServerConnector();
        this.logger = logger;
        this.debugScreenshotsProvider = debugScreenshotsProvider;
    }

    private BufferedImage getViewPortScreenshot() {
        String uaString = driver.getUserAgent();
        UserAgent userAgent = null;
        if (uaString != null) {
            userAgent = UserAgent.parseUserAgentString(uaString, true);
        }
        UserAgent.parseUserAgentString(uaString, true);
        ImageProvider provider = ImageProviderFactory.getImageProvider(userAgent, eyes, logger, driver);
        BufferedImage image = provider.getImage();
        return ImageUtils.scaleImage(image, 1 / eyes.getDevicePixelRatio());
    }

    @Override
    public Map<String, List<Region>> getLocators(VisualLocatorSettings visualLocatorSettings) {
        ArgumentGuard.notNull(visualLocatorSettings, "visualLocatorSettings");

        logger.verbose("Get locators with given names: " + visualLocatorSettings.getNames());

        logger.verbose("Requested viewport screenshot for visual locators...");
        BufferedImage viewPortScreenshot = getViewPortScreenshot();
        debugScreenshotsProvider.save(viewPortScreenshot, "Visual locators: " + Arrays.toString(visualLocatorSettings.getNames().toArray()));

        logger.verbose("Convert screenshot from BufferedImage to base64...");
        byte[] image = ImageUtils.encodeAsPng(viewPortScreenshot);

        logger.verbose("Post visual locators screenshot...");
        String viewportScreenshotUrl = serverConnector.postViewportImage(image);

        logger.verbose("Screenshot URL: " + viewportScreenshotUrl);

        VisualLocatorsData data = new VisualLocatorsData(eyes.getConfiguration().getAppName(), viewportScreenshotUrl, visualLocatorSettings.isFirstOnly(), visualLocatorSettings.getNames());

        logger.verbose("Post visual locators: " + data.toString());
        return serverConnector.postLocators(data);
    }
}
