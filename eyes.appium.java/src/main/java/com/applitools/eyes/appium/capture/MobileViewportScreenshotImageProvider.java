package com.applitools.eyes.appium.capture;

import com.applitools.eyes.appium.EyesAppiumDriver;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import com.applitools.utils.ImageUtils;
import org.openqa.selenium.OutputType;

import java.awt.image.BufferedImage;

/**
 * An image provider returning viewport screenshots for {@link io.appium.java_client.AppiumDriver}
 */
public class MobileViewportScreenshotImageProvider extends MobileImageProvider {

    public MobileViewportScreenshotImageProvider(EyesWebDriver driver) {
        super((EyesAppiumDriver) driver);
    }

    @Override
    public BufferedImage getImage() {
        String screenshot64;
        screenshot64 = driver.getScreenshotAs(OutputType.BASE64);
        BufferedImage screenshot = ImageUtils.imageFromBase64(screenshot64);
        if (!captureStatusBar) {
            screenshot = cropTop(screenshot, driver.getStatusBarHeight());
        }
        return screenshot;
    }
}
