package com.applitools.eyes.selenium.locators;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.Logger;
import com.applitools.eyes.Region;
import com.applitools.eyes.UserAgent;
import com.applitools.eyes.capture.ImageProvider;
import com.applitools.eyes.debug.DebugScreenshotsProvider;
import com.applitools.eyes.locators.VisualLocatorProvider;
import com.applitools.eyes.locators.VisualLocatorSettings;
import com.applitools.eyes.locators.VisualLocatorsData;
import com.applitools.eyes.selenium.SeleniumEyes;
import com.applitools.eyes.selenium.capture.ImageProviderFactory;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import com.applitools.eyes.visualgrid.model.RenderingInfo;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;
import com.applitools.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SeleniumVisualLocatorProvider implements VisualLocatorProvider {

    protected Logger logger;
    private final ServerConnector serverConnector;
    private final SeleniumEyes eyes;
    private final  EyesWebDriver driver;
    private final DebugScreenshotsProvider debugScreenshotsProvider;

    public SeleniumVisualLocatorProvider(SeleniumEyes eyes, EyesWebDriver driver, Logger logger, DebugScreenshotsProvider debugScreenshotsProvider) {
        this.driver = driver;
        this.eyes = eyes;
        this.serverConnector = eyes.getServerConnector();
        this.logger = logger;
        this.debugScreenshotsProvider = debugScreenshotsProvider;
    }

    private BufferedImage getViewPortScreenshot() {
        UserAgent userAgent = UserAgent.parseUserAgentString(driver.getUserAgent());
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
        String viewportScreenshotUrl = postViewportImage(image);

        logger.verbose("Screenshot URL: " + viewportScreenshotUrl);

        VisualLocatorsData data = new VisualLocatorsData(driver.getEyes().getConfigGetter().getAppName(), viewportScreenshotUrl, visualLocatorSettings.isFirstOnly(), visualLocatorSettings.getNames());

        logger.verbose("Post visual locators: " + data.toString());
        return serverConnector.postLocators(data);
    }

    private String postViewportImage(byte[] bytes) {
        String targetUrl;
        RenderingInfo renderingInfo = serverConnector.getRenderInfo();
        if (renderingInfo != null && (targetUrl = renderingInfo.getResultsUrl()) != null) {
            try {
                UUID uuid = UUID.randomUUID();
                targetUrl = targetUrl.replace("__random__", uuid.toString());
                logger.verbose("uploading viewport image to " + targetUrl);

                for (int i = 0; i < ServerConnector.MAX_CONNECTION_RETRIES; i++) {
                    int statusCode = serverConnector.uploadData(bytes, renderingInfo, targetUrl, "image/png", "image/png");
                    if (statusCode == 200 || statusCode == 201) {
                        return targetUrl;
                    }
                    if (statusCode < 500) {
                        throw new IOException(String.format("Failed uploading image. Status code %d", statusCode));
                    }
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                logger.log("Error uploading viewport image");
                GeneralUtils.logExceptionStackTrace(logger, e);
            }
        }
        return null;
    }
}
