package com.applitools.eyes.locators;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.Logger;
import com.applitools.eyes.Region;
import com.applitools.eyes.debug.DebugScreenshotsProvider;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class BaseVisualLocatorsProvider implements VisualLocatorsProvider {

    protected Logger logger;
    private final ServerConnector serverConnector;
    protected double devicePixelRatio;
    protected String appName;
    protected DebugScreenshotsProvider debugScreenshotsProvider;

    public BaseVisualLocatorsProvider(Logger logger, ServerConnector serverConnector,
                               double devicePixelRatio, String appName, DebugScreenshotsProvider debugScreenshotsProvider) {
        this.logger = logger;
        this.serverConnector = serverConnector;
        this.devicePixelRatio = devicePixelRatio;
        this.appName = appName;
        this.debugScreenshotsProvider = debugScreenshotsProvider;
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

        VisualLocatorsData data = new VisualLocatorsData(appName, viewportScreenshotUrl, visualLocatorSettings.isFirstOnly(), visualLocatorSettings.getNames());
        logger.verbose("Post visual locators: " + data.toString());
        return serverConnector.postLocators(data);
    }

    protected abstract BufferedImage getViewPortScreenshot();
}
