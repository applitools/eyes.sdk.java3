package com.applitools.eyes.locators;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.*;
import com.applitools.eyes.debug.DebugScreenshotsProvider;
import com.applitools.eyes.logging.Stage;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.ImageUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

public abstract class BaseVisualLocatorsProvider implements VisualLocatorsProvider {

    protected Logger logger;
    protected final String testId;
    private final ServerConnector serverConnector;
    protected double devicePixelRatio;
    protected String appName;
    protected DebugScreenshotsProvider debugScreenshotsProvider;

    public BaseVisualLocatorsProvider(Logger logger, String testId, ServerConnector serverConnector,
                                      double devicePixelRatio, String appName, DebugScreenshotsProvider debugScreenshotsProvider) {
        this.logger = logger;
        this.testId = testId;
        this.serverConnector = serverConnector;
        this.devicePixelRatio = devicePixelRatio;
        this.appName = appName;
        this.debugScreenshotsProvider = debugScreenshotsProvider;
    }

    @Override
    public Map<String, List<Region>> getLocators(VisualLocatorSettings visualLocatorSettings) {
        ArgumentGuard.notNull(visualLocatorSettings, "visualLocatorSettings");


        BufferedImage viewPortScreenshot = getViewPortScreenshot();
        logger.log(testId, Stage.LOCATE,
                Pair.of("locatorNames", visualLocatorSettings.getNames()),
                Pair.of("devicePixelRatio", devicePixelRatio),
                Pair.of("scaledImageSize", new RectangleSize(viewPortScreenshot.getWidth(), viewPortScreenshot.getHeight())));
        debugScreenshotsProvider.save(viewPortScreenshot, "visual_locators_final");
        byte[] image = ImageUtils.encodeAsPng(viewPortScreenshot);
        SyncTaskListener<String> listener = new SyncTaskListener<>(logger, "getLocators");
        serverConnector.uploadImage(listener, image);
        String viewportScreenshotUrl = listener.get();
        if (viewportScreenshotUrl == null) {
            throw new EyesException("Failed posting viewport image");
        }

        VisualLocatorsData data = new VisualLocatorsData(appName, viewportScreenshotUrl, visualLocatorSettings.isFirstOnly(), visualLocatorSettings.getNames());
        logger.log(testId, Stage.LOCATE,
                Pair.of("screenshotUrl", viewportScreenshotUrl),
                Pair.of("visualLocatorsData", data));

        SyncTaskListener<Map<String, List<Region>>> postListener = new SyncTaskListener<>(logger, "getLocators");
        serverConnector.postLocators(postListener, data);
        Map<String, List<Region>> result = postListener.get();
        if (result == null) {
            throw new EyesException("Failed posting locators");
        }

        logger.log(testId, Stage.LOCATE,
                Pair.of("result", result));
        return result;
    }

    protected abstract BufferedImage getViewPortScreenshot();
}