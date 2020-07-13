package com.applitools.eyes.appium;

import com.applitools.eyes.EyesException;
import com.applitools.eyes.Logger;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.selenium.EyesWebDriver;
import com.applitools.eyes.selenium.positioning.ImageRotation;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.ImageUtils;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebElement;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EyesAppiumDriver implements EyesWebDriver {

    private final Logger logger;
    private final AppiumDriver driver;
    private Map<String, Object> sessionDetails;
    private final Map<String, WebElement> elementsIds = new HashMap<>();
    private ImageRotation rotation;
    private RectangleSize defaultContentViewportSize = null;

    public EyesAppiumDriver(Logger logger, AppiumDriver driver) {
        this.logger = logger;
        this.driver = driver;
    }

    @Override
    public AppiumDriver getRemoteWebDriver () { return this.driver; }

    /**
     *
     * @return The image rotation model.
     */
    public ImageRotation getRotation() {
        return rotation;
    }

    /**
     * @param rotation The image rotation model.
     */
    public void setRotation(ImageRotation rotation) {
        this.rotation = rotation;
    }

    public EyesAppiumElement getEyesElement(WebElement element) {
        if (element instanceof EyesAppiumElement) {
            return (EyesAppiumElement) element;
        }

        return new EyesAppiumElement(this, element, 1/getDevicePixelRatio());
    }

    private Map<String, Object> getCachedSessionDetails () {
        if(sessionDetails == null) {
            logger.verbose("Retrieving session details and caching the result...");
            sessionDetails = getRemoteWebDriver().getSessionDetails();
            logger.verbose("Session details: " + sessionDetails.toString());
        }
        return sessionDetails;
    }

    public HashMap<String, Integer> getViewportRect () {
        Map<String, Long> rectMap = (Map<String, Long>) getCachedSessionDetails().get("viewportRect");
        HashMap<String, Integer> intRectMap = new HashMap<String, Integer>();
        intRectMap.put("width", rectMap.get("width").intValue());
        intRectMap.put("height", rectMap.get("height").intValue());
        return intRectMap;
    }

    public int getStatusBarHeight() {
        Object statusBarHeight = getCachedSessionDetails().get("statBarHeight");
        if (statusBarHeight instanceof Double) {
            return ((Double) statusBarHeight).intValue();
        } else {
            return ((Long) statusBarHeight).intValue();
        }
    }

    public double getDevicePixelRatio () {
        Object pixelRatio = getCachedSessionDetails().get("pixelRatio");
        if (pixelRatio instanceof Double) {
            return (Double) pixelRatio;
        } else {
            return ((Long) pixelRatio).doubleValue();
        }
    }

    /**
     * @param forceQuery If true, we will perform the query even if we have a cached viewport size.
     * @return The viewport size of the default content (outer most frame).
     */
    public RectangleSize getDefaultContentViewportSize(boolean forceQuery) {
        logger.verbose("getDefaultContentViewportSize(forceQuery: " + forceQuery + ")");

        if (defaultContentViewportSize != null && !forceQuery) {
            logger.verbose("Using cached viewport size: " + defaultContentViewportSize);
            return defaultContentViewportSize;
        }

        HashMap<String, Integer> rect = getViewportRect();
        double dpr = getDevicePixelRatio();
        defaultContentViewportSize = (new RectangleSize(rect.get("width"), rect.get("height"))).scale(1/dpr);
        logger.verbose("Done! Viewport size: " + defaultContentViewportSize);

        return defaultContentViewportSize;
    }

    /**
     * See {@link #getDefaultContentViewportSize(boolean)}.
     * {@code forceQuery} defaults to {@code false}.
     */
    public RectangleSize getDefaultContentViewportSize() {
        return getDefaultContentViewportSize(true);
    }

    /**
     * Rotates the image as necessary. The rotation is either manually forced
     * by passing a non-null ImageRotation, or automatically inferred.
     * @param driver   The underlying driver which produced the screenshot.
     * @param image    The image to normalize.
     * @param rotation The degrees by which to rotate the image:
     *                 positive values = clockwise rotation,
     *                 negative values = counter-clockwise,
     *                 0 = force no rotation,
     *                 null = rotate automatically as needed.
     * @return A normalized image.
     */
    public static BufferedImage normalizeRotation(Logger logger, WebDriver driver,
                                                  BufferedImage image, ImageRotation rotation) {
        ArgumentGuard.notNull(driver, "driver");
        ArgumentGuard.notNull(image, "image");
        int degrees;
        if (rotation != null) {
            degrees = rotation.getRotation();
        } else {
            degrees = EyesAppiumUtils.tryAutomaticRotation(logger, driver, image);
        }

        return ImageUtils.rotateImage(image, degrees);
    }

    public <X> X getScreenshotAs(OutputType<X> xOutputType)
            throws WebDriverException {
        // Get the image as base64.
        String screenshot64 = driver.getScreenshotAs(OutputType.BASE64);
        BufferedImage screenshot = ImageUtils.imageFromBase64(screenshot64);
        screenshot = EyesAppiumDriver.normalizeRotation(logger, driver, screenshot, rotation);

        // Return the image in the requested format.
        screenshot64 = ImageUtils.base64FromImage(screenshot);
        return xOutputType.convertFromBase64Png(screenshot64);
    }

    public EyesAppiumElement findElement(By by) {
        WebElement webElement = driver.findElement(by);
        if (webElement instanceof RemoteWebElement) {
            EyesAppiumElement appiumElement = new EyesAppiumElement(this, webElement, 1/getDevicePixelRatio());

            // For Remote web elements, we can keep the IDs,
            // for Id based lookup (mainly used for Javascript related
            // activities).
            elementsIds.put(((RemoteWebElement) webElement).getId(), webElement);
            return appiumElement;
        } else {
            throw new EyesException("findElement: Element is not a RemoteWebElement: " + by);
        }
    }

    public List<WebElement> findElements(By by) {
        List<WebElement> foundWebElementsList = driver.findElements(by);

        // This list will contain the found elements wrapped with our class.
        List<WebElement> resultElementsList = new ArrayList<>(foundWebElementsList.size());

        for (WebElement currentElement : foundWebElementsList) {
            if (currentElement instanceof RemoteWebElement) {
                resultElementsList.add(new EyesAppiumElement(this, currentElement, 1/getDevicePixelRatio()));

                // For Remote web elements, we can keep the IDs
                elementsIds.put(((RemoteWebElement) currentElement).getId(),
                        currentElement);

            } else {
                throw new EyesException(String.format(
                        "findElements: element is not a RemoteWebElement: %s",
                        by));
            }
        }

        return resultElementsList;
    }

    public Map<String, WebElement> getElementIds() {
        return elementsIds;
    }
}
