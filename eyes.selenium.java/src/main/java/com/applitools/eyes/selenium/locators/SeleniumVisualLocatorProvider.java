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
import com.applitools.eyes.selenium.capture.ImageProviderFactory;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import com.applitools.eyes.visualgrid.model.RenderingInfo;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;
import com.applitools.utils.ImageUtils;
import org.openqa.selenium.OutputType;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SeleniumVisualLocatorProvider implements VisualLocatorProvider {

    protected Logger logger;
    private final ServerConnector serverConnector;
    private final UserAgent userAgent;
    protected EyesWebDriver driver;
    private final double devicePixelRatio;
    private final DebugScreenshotsProvider debugScreenshotsProvider;

    public SeleniumVisualLocatorProvider(EyesWebDriver driver, ServerConnector serverConnector, UserAgent userAgent, Logger logger, DebugScreenshotsProvider debugScreenshotsProvider) {
        this.driver = driver;
        this.serverConnector = serverConnector;
        this.userAgent = userAgent;
        this.logger = logger;
        this.devicePixelRatio = driver.getEyes().getDevicePixelRatio();
        this.debugScreenshotsProvider = debugScreenshotsProvider;
    }

    private BufferedImage getViewPortScreenshot() {
        ImageProvider provider = ImageProviderFactory.getImageProvider(userAgent, null, logger, driver);
        BufferedImage image = provider.getImage();

        logger.verbose("Scale image with the scale ratio - " + 1/getDevicePixelRatio());
        return ImageUtils.scaleImage(image, 1/getDevicePixelRatio());
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

    double getDevicePixelRatio() {
        return devicePixelRatio;
    }

    private String postViewportImage(byte[] bytes) {
        String targetUrl;
        RenderingInfo renderingInfo = serverConnector.getRenderInfo();
        if (renderingInfo != null && (targetUrl = renderingInfo.getResultsUrl()) != null) {
            try {
                UUID uuid = UUID.randomUUID();
                targetUrl = targetUrl.replace("__random__", uuid.toString());
                logger.verbose("uploading viewport image to " + targetUrl);

                int retriesLeft = 3;
                int wait = 500;
                while (retriesLeft-- > 0) {
                    try {
                        int statusCode = serverConnector.uploadData(bytes, renderingInfo, targetUrl, "image/png", "image/png");
                        if (statusCode == 200 || statusCode == 201) {
                            logger.verbose("upload viewport image guid " + uuid + "complete.");
                            return targetUrl;
                        }
                        if (statusCode < 500) {
                            break;
                        }
                    } catch (Exception e) {
                        if (retriesLeft == 0) throw e;
                    }
                    Thread.sleep(wait);
                    wait *= 2;
                    wait = Math.min(10000, wait);
                }
            } catch (Exception e) {
                logger.log("Error uploading viewport image");
                GeneralUtils.logExceptionStackTrace(logger, e);
            }
        }
        return null;
    }
}
