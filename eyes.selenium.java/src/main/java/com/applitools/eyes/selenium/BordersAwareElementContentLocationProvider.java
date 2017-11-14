package com.applitools.eyes.selenium;

import com.applitools.eyes.Location;
import com.applitools.eyes.Logger;
import com.applitools.eyes.selenium.wrappers.EyesRemoteWebElement;
import com.applitools.utils.ArgumentGuard;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

/**
 * Encapsulates an algorithm to find an element's content location, based on
 * the element's location.
 */
public class BordersAwareElementContentLocationProvider {

    /**
     * Returns a location based on the given location.
     * @param logger   The logger to use.
     * @param element  The element for which we want to find the content's
     *                 location.
     * @param location The location of the element.
     * @return The location of the content of the element.
     */
    public static Location getLocation(Logger logger, WebElement element,
                                       Location location) {
        ArgumentGuard.notNull(logger, "logger");
        ArgumentGuard.notNull(element, "element");
        ArgumentGuard.notNull(location, "location");

        logger.verbose(String.format(
                "BordersAdditionFrameLocationProvider(logger, element, %s)",
                location));

        // Frame borders also have effect on the frame's location.
        int leftBorderWidth = getPropertyValue(logger, element, "border-left-width");
        int topBorderWidth = getPropertyValue(logger, element, "border-top-width");

        Location contentLocation = new Location(location).offset(leftBorderWidth, topBorderWidth);
        logger.verbose("Done!");
        return contentLocation;
    }

    protected static int getPropertyValue(Logger logger, WebElement element, String propName) {
        int propNumericValue;
        String propValue;
        try {
            logger.verbose("Get element border left width...");
            if (element instanceof EyesRemoteWebElement) {
                logger.verbose("Element is an EyesWebElement, using 'getComputedStyle'.");
                try {
                    propValue = ((EyesRemoteWebElement) element).getComputedStyle(propName);
                } catch (WebDriverException e) {
                    logger.verbose("Using getComputedStyle failed: " + e.getMessage());
                    logger.verbose("Using getCssValue...");
                    propValue = element.getCssValue(propName);
                }
                logger.verbose("Done!");
            } else {
                // OK, this is weird, we got an element which is not
                // EyesWebElement?? Log it and try to move on.
                logger.verbose(String.format(
                        "Element is not an EyesWebElement! (when trying to get %s) Element's class: %s",
                        propName, element.getClass().getName()));

                logger.verbose("Using getCssValue...");
                propValue = element.getCssValue(propName);
                logger.verbose("Done!");
            }
            // Convert border value from the format "2px" to int.
            propNumericValue = Math.round(Float.valueOf(
                    propValue.trim().replace("px", "")
            ));
            logger.verbose(propName + ": " + propNumericValue);
        } catch (WebDriverException e) {
            logger.verbose(String.format(
                    "Couldn't get the element's %s: %s. Falling back to default",
                    propName, e.getMessage()));
            propNumericValue = 0;
        }
        return propNumericValue;
    }
}
