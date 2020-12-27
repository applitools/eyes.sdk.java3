package com.applitools.eyes.appium.capture;

import com.applitools.eyes.capture.ImageProvider;
import com.applitools.utils.ImageUtils;
import org.openqa.selenium.JavascriptExecutor;

import java.awt.image.BufferedImage;

/**
 * An image provider returning viewport screenshots for {@link io.appium.java_client.AppiumDriver}
 */
public class MobileViewportScreenshotImageProvider implements ImageProvider {

    private final JavascriptExecutor jsExecutor;

    public MobileViewportScreenshotImageProvider(JavascriptExecutor jsExecutor) {
        this.jsExecutor = jsExecutor;
    }

    @Override
    public BufferedImage getImage() {
        String screenshot64 = (String) jsExecutor.executeScript("mobile: viewportScreenshot");
        return ImageUtils.imageFromBase64(screenshot64);
    }
}