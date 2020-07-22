/*
 * Applitools software.
 */
package com.applitools.eyes.appium;

import com.applitools.eyes.Logger;
import com.applitools.eyes.selenium.EyesDriverUtils;
import com.applitools.utils.ArgumentGuard;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebElement;

import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EyesAppiumUtils {

    private static final String NATIVE_APP = "NATIVE_APP";
    private static String SCROLLVIEW_XPATH = "//*[@scrollable='true']";
    private static String FIRST_VIS_XPATH = "/*[@firstVisible='true']";

    /**
     * @param driver The driver to get the platform version from.
     * @return The platform version or {@code null} if it is undefined.
     */
    public static String getPlatformVersion(HasCapabilities driver) {
        Capabilities capabilities = driver.getCapabilities();
        Object platformVersionObj =
                capabilities.getCapability
                        (MobileCapabilityType.PLATFORM_VERSION);

        return platformVersionObj == null ?
                null : String.valueOf(platformVersionObj);
    }

    public static WebElement getFirstScrollableView(WebDriver driver) {
        return driver.findElement(By.xpath(SCROLLVIEW_XPATH));
    }

    public static WebElement getFirstVisibleChild(WebElement element) {
        return element.findElement(By.xpath(FIRST_VIS_XPATH));
    }

    public static void scrollByDirection(AppiumDriver driver, String direction) {
        EyesAppiumUtils.scrollByDirection(driver, direction, 1.0);
    }

    public static void scrollByDirection(AppiumDriver driver, String direction, double distanceRatio) {
        HashMap<String, String> args = new HashMap<>();
        args.put("direction", direction);
        args.put("distance", Double.toString(distanceRatio));
        driver.executeScript("mobile: scroll", args);
    }

    public static void scrollBackToElement(AndroidDriver driver, RemoteWebElement scroller, RemoteWebElement scrollToEl) {
        HashMap<String, String> args = new HashMap<>();
        args.put("elementId", scroller.getId());
        args.put("elementToId", scrollToEl.getId());
        driver.executeScript("mobile: scrollBackTo", args);
    }

    public static ContentSize getContentSize(AppiumDriver driver, WebElement element) throws IOException {
        ContentSize contentSize;
        try {
            String contentSizeJson = element.getAttribute("contentSize");
            ObjectMapper objectMapper = new ObjectMapper();
            contentSize = objectMapper.readValue(contentSizeJson, ContentSize.class);
            contentSize.setDriver(driver);
        } catch (WebDriverException e) {
            contentSize = new ContentSize();
            contentSize.height = element.getSize().getHeight();
            contentSize.width = element.getSize().getWidth();
            contentSize.top = element.getLocation().getY();
            contentSize.left = element.getLocation().getX();
            contentSize.scrollableOffset = element.getSize().getHeight();
        }
        return contentSize;
    }

    @Nullable
    public static LastScrollData getLastScrollData(AppiumDriver driver) {
        Map<String, Long> scrollData = (Map<String, Long>) driver.getSessionDetail("lastScrollData");
        if (scrollData == null) {
            return null;
        }
        return new LastScrollData(scrollData);
    }

    public static boolean isLandscapeOrientation(Logger logger, WebDriver driver) {
        // We can only find orientation for mobile devices.
        if (!EyesDriverUtils.isMobileDevice(driver)) {
            return false;
        }

        AppiumDriver<?> appiumDriver = (AppiumDriver<?>) EyesDriverUtils.getUnderlyingDriver(driver);

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
        } catch (WebDriverException e) {
            originalContext = null;
        }
        try {
            ScreenOrientation orientation = appiumDriver.getOrientation();
            return orientation == ScreenOrientation.LANDSCAPE;
        } catch (Exception e) {
            logger.log("WARNING: Couldn't get device orientation. Assuming Portrait.");
            return false;
        }
        finally {
            if (originalContext != null) {
                appiumDriver.context(originalContext);
            }
        }
    }

    public static int tryAutomaticRotation(Logger logger, WebDriver driver, BufferedImage image) {
        ArgumentGuard.notNull(logger, "logger");
        int degrees = 0;
        try {
            logger.verbose("Trying to automatically normalize rotation...");
            if (EyesDriverUtils.isMobileDevice(driver) &&
                    isLandscapeOrientation(logger, driver)
                    && image.getHeight() > image.getWidth()) {
                // For Android, we need to rotate images to the right, and
                // for iOS to the left.
                degrees = EyesDriverUtils.isAndroid(driver) ? 90 : -90;
            }
        } catch (Exception e) {
            logger.verbose("Got exception: " + e.getMessage());
            logger.verbose("Skipped automatic rotation handling.");
        }
        return degrees;
    }
}
