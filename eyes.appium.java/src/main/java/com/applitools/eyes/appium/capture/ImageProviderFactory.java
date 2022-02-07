package com.applitools.eyes.appium.capture;

import com.applitools.eyes.Logger;
import com.applitools.eyes.capture.ImageProvider;
import com.applitools.eyes.selenium.EyesDriverUtils;
import com.applitools.eyes.selenium.capture.TakesScreenshotImageProvider;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import org.openqa.selenium.TakesScreenshot;

public class ImageProviderFactory {
    public static ImageProvider getImageProvider(Logger logger, EyesWebDriver driver, boolean viewportImage) {
        if (viewportImage) {
            if (EyesDriverUtils.isAndroid(driver.getRemoteWebDriver())) {
                return new AndroidViewportScreenshotImageProvider(driver);
            }
            return new MobileViewportScreenshotImageProvider(driver);
        }
        return new TakesScreenshotImageProvider(logger, (TakesScreenshot) driver);
    }
}
