package com.applitools.eyes.appium;

import com.applitools.eyes.Logger;
import com.applitools.eyes.selenium.EyesSeleniumUtils;
import com.applitools.eyes.selenium.ImageOrientationHandler;
import com.applitools.eyes.selenium.exceptions.EyesDriverOperationException;
import com.applitools.utils.ArgumentGuard;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.WebDriver;

import java.awt.image.BufferedImage;

import static com.applitools.eyes.selenium.EyesSeleniumUtils.getUnderlyingDriver;

public class AppiumImageOrientationHandler implements ImageOrientationHandler {

    private static final String NATIVE_APP = "NATIVE_APP";

    @Override
    public boolean isLandscapeOrientation(WebDriver driver) {
        // We can only find orientation for mobile devices.
        if (EyesAppiumUtils.isMobileDevice(driver)) {
            AppiumDriver<?> appiumDriver = (AppiumDriver<?>) getUnderlyingDriver(driver);

            String originalContext = null;
            try {
                // We must be in native context in order to ask for orientation,
                // because of an Appium bug.
                originalContext = appiumDriver.getContext();
                if (appiumDriver.getContextHandles().size() > 1 &&
                        !originalContext.equalsIgnoreCase(NATIVE_APP)) {
                    appiumDriver.context(NATIVE_APP);
                } else {
                    originalContext = null;
                }
                ScreenOrientation orientation = appiumDriver.getOrientation();
                return orientation == ScreenOrientation.LANDSCAPE;
            } catch (Exception e) {
                throw new EyesDriverOperationException("Failed to get orientation!", e);
            } finally {
                if (originalContext != null) {
                    appiumDriver.context(originalContext);
                }
            }
        }

        return false;
    }

    @Override
    public int tryAutomaticRotation(Logger logger, WebDriver driver, BufferedImage image) {
        ArgumentGuard.notNull(logger, "logger");
        int degrees = 0;
        try {
            logger.verbose("Trying to automatically normalize rotation...");
            if (EyesAppiumUtils.isMobileDevice(driver) &&
                    EyesSeleniumUtils.isLandscapeOrientation(driver)
                    && image.getHeight() > image.getWidth()) {
                // For Android, we need to rotate images to the right, and
                // for iOS to the left.
                degrees = EyesAppiumUtils.isAndroid(driver) ? 90 : -90;
            }
        } catch (Exception e) {
            logger.verbose("Got exception: " + e.getMessage());
            logger.verbose("Skipped automatic rotation handling.");
        }
        return degrees;
    }
}
