/*
 * Applitools SDK for Appium integration.
 */
package com.applitools.eyes.appium;

import com.applitools.eyes.AppEnvironment;
import com.applitools.eyes.Logger;
import com.applitools.eyes.ScaleProviderFactory;
import com.applitools.eyes.Trigger;
import com.applitools.eyes.selenium.ContextBasedScaleProviderFactory;
import com.applitools.eyes.selenium.EyesSeleniumUtils;
import com.applitools.eyes.selenium.ImageOrientationHandler;
import com.applitools.eyes.selenium.JavascriptHandler;
import com.applitools.eyes.selenium.exceptions.EyesDriverOperationException;
import com.applitools.eyes.selenium.positioning.ImageRotation;
import com.applitools.eyes.triggers.MouseTrigger;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.ImageUtils;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.awt.image.BufferedImage;

import static com.applitools.eyes.selenium.EyesSeleniumUtils.getUnderlyingDriver;

public class Eyes extends com.applitools.eyes.selenium.Eyes {

    private static final String NATIVE_APP = "NATIVE_APP";

    public Eyes() {
        init();
    }

    private void init() {
        EyesSeleniumUtils.setImageOrientationHandlerHandler(new ImageOrientationHandler() {
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
        });

        EyesSeleniumUtils.setJavascriptHandler(new JavascriptHandler() {
            @Override
            public void handle(String script, Object[] args) {
                // Appium commands are sometimes sent as Javascript
                if (AppiumJsCommandExtractor.isAppiumJsCommand(script)) {
                    Trigger trigger =
                            AppiumJsCommandExtractor.extractTrigger(driver.getElementIds(),
                                    driver.manage().window().getSize(), script, args);

                    if (trigger != null) {
                        // TODO - Daniel, additional type of triggers
                        if (trigger instanceof MouseTrigger) {
                            MouseTrigger mt = (MouseTrigger) trigger;
                            addMouseTrigger(mt.getMouseAction(), mt.getControl(), mt.getLocation());
                        }
                    }
                }
            }
        });
    }

    protected ScaleProviderFactory getScaleProviderFactory() {
        return new ContextBasedScaleProviderFactory(logger, positionProvider.getEntireSize(),
                viewportSizeHandler.get(), getDevicePixelRatio(), EyesAppiumUtils.isMobileDevice(getDriver()),
                scaleProviderHandler);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This override also checks for mobile operating system.
     */
    @Override
    protected AppEnvironment getAppEnvironment() {

        AppEnvironment appEnv = super.getAppEnvironment();
        RemoteWebDriver underlyingDriver = driver.getRemoteWebDriver();
        // If hostOs isn't set, we'll try and extract and OS ourselves.
        if (appEnv.getOs() == null) {
            logger.log("No OS set, checking for mobile OS...");
            if (EyesAppiumUtils.isMobileDevice(underlyingDriver)) {
                String platformName = null;
                logger.log("Mobile device detected! Checking device type..");
                if (EyesAppiumUtils.isAndroid(underlyingDriver)) {
                    logger.log("Android detected.");
                    platformName = "Android";
                } else if (EyesAppiumUtils.isIOS(underlyingDriver)) {
                    logger.log("iOS detected.");
                    platformName = "iOS";
                } else {
                    logger.log("Unknown device type.");
                }
                // We only set the OS if we identified the device type.
                if (platformName != null) {
                    String os = platformName;
                    String platformVersion = EyesAppiumUtils.getPlatformVersion(underlyingDriver);
                    if (platformVersion != null) {
                        String majorVersion =
                                platformVersion.split("\\.", 2)[0];

                        if (!majorVersion.isEmpty()) {
                            os += " " + majorVersion;
                        }
                    }

                    logger.verbose("Setting OS: " + os);
                    appEnv.setOs(os);
                }
            } else {
                logger.log("No mobile OS detected.");
            }
        }
        logger.log("Done!");
        return appEnv;
    }

    /**
     * Rotates the image as necessary. The rotation is either manually forced
     * by passing a non-null ImageRotation, or automatically inferred.
     * @param driver   The underlying driver which produced the screenshot.
     * @param image    The image to normalize.
     * @param rotation The degrees by which to rotate the image:
     *                 positive values = clockwise rotation,
     *                 negative values = counter-clockwise,
     *                 0 = force no rotation, null = rotate automatically
     *                 when needed.
     * @return A normalized image.
     */
    public static BufferedImage normalizeRotation(Logger logger,
                                                  WebDriver driver,
                                                  BufferedImage image,
                                                  ImageRotation rotation) {
        ArgumentGuard.notNull(driver, "driver");
        ArgumentGuard.notNull(image, "image");
        BufferedImage normalizedImage = image;
        if (rotation != null) {
            if (rotation.getRotation() != 0) {
                normalizedImage = ImageUtils.rotateImage(image,
                        rotation.getRotation());
            }
        } else { // Do automatic rotation if necessary
            try {
                logger.verbose("Trying to automatically normalize rotation...");
                if (EyesAppiumUtils.isMobileDevice(driver) &&
                        EyesSeleniumUtils.isLandscapeOrientation(driver)
                        && image.getHeight() > image.getWidth()) {
                    // For Android, we need to rotate images to the right, and
                    // for iOS to the left.
                    int degrees = EyesAppiumUtils.isAndroid(driver) ? 90 : -90;
                    normalizedImage = ImageUtils.rotateImage(image, degrees);
                }
            } catch (Exception e) {
                logger.verbose("Got exception: " + e.getMessage());
                logger.verbose("Skipped automatic rotation handling.");
            }
        }

        return normalizedImage;
    }


}