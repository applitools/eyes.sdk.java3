package com.applitools.eyes.appium.capture;

import com.applitools.eyes.appium.EyesAppiumDriver;
import com.applitools.eyes.capture.ScreenshotProvider;
import com.applitools.eyes.logging.Stage;
import com.applitools.utils.ImageUtils;
import org.openqa.selenium.OutputType;

import java.awt.image.BufferedImage;

public class MobileScreenshotProvider implements ScreenshotProvider {

    private final EyesAppiumDriver driver;
    private final double devicePixelRatio;

    public MobileScreenshotProvider(EyesAppiumDriver driver, double devicePixelRatio) {
        this.driver = driver;
        this.devicePixelRatio = devicePixelRatio;
    }

    @Override
    public BufferedImage getViewPortScreenshot(Stage stage) {
        String base64Image = driver.getScreenshotAs(OutputType.BASE64);
        BufferedImage image = ImageUtils.imageFromBase64(base64Image);
        return ImageUtils.scaleImage(image, 1 / devicePixelRatio, true);
    }
}
