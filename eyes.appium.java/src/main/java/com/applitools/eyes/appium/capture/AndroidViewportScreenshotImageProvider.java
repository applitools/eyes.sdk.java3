package com.applitools.eyes.appium.capture;

import com.applitools.eyes.appium.EyesAppiumDriver;
import com.applitools.eyes.appium.EyesAppiumUtils;
import com.applitools.eyes.capture.ImageProvider;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import com.applitools.utils.ImageUtils;
import org.openqa.selenium.OutputType;

import java.awt.image.BufferedImage;
import java.util.Map;

public class AndroidViewportScreenshotImageProvider implements ImageProvider {

    private final EyesAppiumDriver driver;

    public AndroidViewportScreenshotImageProvider(EyesWebDriver driver) {
        this.driver = (EyesAppiumDriver) driver;
    }

    @Override
    public BufferedImage getImage() {
        BufferedImage screenshot = ImageUtils.imageFromBytes(driver.getScreenshotAs(OutputType.BYTES));
        Map<String, Integer> systemBarHeights = EyesAppiumUtils.getSystemBarsHeights(driver);
        screenshot = cropTop(screenshot, systemBarHeights.get(EyesAppiumUtils.STATUS_BAR));
        screenshot = cropBottom(screenshot, systemBarHeights.get(EyesAppiumUtils.NAVIGATION_BAR));

        BufferedImage result = new BufferedImage(screenshot.getWidth(), screenshot.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        result.setData(screenshot.getData());
        return result;
    }

    private BufferedImage cropTop(BufferedImage screenshot, Integer cropHeight) {
        if (cropHeight == null || cropHeight <= 0 || cropHeight >= screenshot.getHeight()) {
            return screenshot;
        }
        return screenshot.getSubimage(0, cropHeight, screenshot.getWidth(), screenshot.getHeight() - cropHeight);
    }

    private BufferedImage cropBottom(BufferedImage screenshot, Integer cropHeight) {
        if (cropHeight == null || cropHeight <= 0 || cropHeight >= screenshot.getHeight()) {
            return screenshot;
        }
        return screenshot.getSubimage(0, 0, screenshot.getWidth(), screenshot.getHeight() - cropHeight);
    }
}
