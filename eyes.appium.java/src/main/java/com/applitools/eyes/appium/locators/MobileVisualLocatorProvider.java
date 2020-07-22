package com.applitools.eyes.appium.locators;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.Logger;
import com.applitools.eyes.Region;
import com.applitools.eyes.appium.EyesAppiumDriver;
import com.applitools.eyes.debug.DebugScreenshotsProvider;
import com.applitools.eyes.locators.BaseVisualLocatorsProvider;
import com.applitools.eyes.locators.VisualLocatorSettings;
import com.applitools.utils.ImageUtils;
import org.openqa.selenium.OutputType;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

public abstract class MobileVisualLocatorProvider extends BaseVisualLocatorsProvider {

    protected EyesAppiumDriver driver;

    MobileVisualLocatorProvider(Logger logger, EyesAppiumDriver driver, ServerConnector serverConnector,
                                double devicePixelRatio, String appName, DebugScreenshotsProvider debugScreenshotsProvider) {
        super(logger, serverConnector, devicePixelRatio, appName, debugScreenshotsProvider);
        this.driver = driver;
    }

    @Override
    protected BufferedImage getViewPortScreenshot() {
        logger.verbose("Getting screenshot as base64...");
        String base64Image = driver.getScreenshotAs(OutputType.BASE64);

        logger.verbose("Done getting base64! Creating BufferedImage...");
        BufferedImage image = ImageUtils.imageFromBase64(base64Image);

        logger.verbose("Scale image with the scale ratio - " + 1 / devicePixelRatio);
        return ImageUtils.scaleImage(image, 1 / devicePixelRatio);
    }

    @Override
    public Map<String, List<Region>> getLocators(VisualLocatorSettings visualLocatorSettings) {
        return adjustVisualLocators(super.getLocators(visualLocatorSettings));
    }

    protected abstract Map<String, List<Region>> adjustVisualLocators(Map<String, List<Region>> map);
}
