package com.applitools.eyes.appium.capture;

import com.applitools.eyes.Logger;
import com.applitools.eyes.capture.ImageProvider;
import com.applitools.eyes.selenium.capture.TakesScreenshotImageProvider;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class ImageProviderFactory {
    public static ImageProvider getImageProvider(Logger logger, WebDriver driver, boolean viewportImage) {
        if (viewportImage) {
            return new MobileViewportScreenshotImageProvider(logger, (JavascriptExecutor) driver);
        }
        return new TakesScreenshotImageProvider(logger, (TakesScreenshot) driver);
    }
}
