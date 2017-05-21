package com.applitools.eyes.selenium;

import com.applitools.eyes.Location;
import com.applitools.eyes.Logger;
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
     * @param logger The logger to use.
     * @param element The element for which we want to find the content's
     *                location.
     * @param location The location of the element.
     * @return The location of the content of the element.
     */
    public Location getLocation(Logger logger, WebElement element,
                                Location location) {
        ArgumentGuard.notNull(logger, "logger");
        ArgumentGuard.notNull(element, "element");
        ArgumentGuard.notNull(location, "location");

        logger.verbose(String.format(
                "BordersAdditionFrameLocationProvider(logger, element, %s)",
                location));

        // Frame borders also have effect on the frame's
        // location.
        int leftBorderWidth, topBorderWidth;
        String propValue;
        try {
            logger.verbose("Get element border left width...");
            if (element instanceof EyesRemoteWebElement) {
                logger.verbose(
                        "Element is an EyesWebElement, " +
                                "using 'getComputedStyle'.");
                try {
                    propValue = ((EyesRemoteWebElement) element)
                            .getComputedStyle("border-left-width");
                } catch (WebDriverException e) {
                    logger.verbose(String.format(
                            "Using getComputedStyle failed: %s.",
                            e.getMessage()));
                    logger.verbose("Using getCssValue...");
                    propValue = element.getCssValue("border-left-width");
                }
                logger.verbose("Done!");
            } else {
                // OK, this is weird, we got an element which is not
                // EyesWebElement?? Log it and try to move on.
                logger.verbose("Element is not an EyesWebElement! " +
                        "(when trying to get border-left-width) " +
                        "Element's class: " +
                        element.getClass().getName());
                logger.verbose("Using getCssValue...");
                propValue = element.getCssValue("border-left-width");
                logger.verbose("Done!");
            }
            // Convert border value from the format "2px" to int.
            leftBorderWidth = Math.round(Float.valueOf(
                    propValue.trim().replace("px", "")
            ));
            logger.verbose("border-left-width: " + leftBorderWidth);
        } catch (WebDriverException e) {
            logger.verbose(String.format(
                    "Couldn't get the element's border-left-width: %s. " +
                            "Falling back to default",
                    e.getMessage()));
            leftBorderWidth = 0;
        }
        try {
            logger.verbose("Get element's border top width...");
            if (element instanceof EyesRemoteWebElement) {
                logger.verbose(
                        "Element is an EyesWebElement, " +
                                "using 'getComputedStyle'.");
                try {
                    propValue = ((EyesRemoteWebElement) element)
                            .getComputedStyle("border-top-width");
                } catch (WebDriverException e) {
                    logger.verbose(String.format(
                            "Using getComputedStyle failed: %s.",
                            e.getMessage()));
                    logger.verbose("Using getCssValue...");
                    propValue = element.getCssValue("border-top-width");
                }
                logger.verbose("Done!");
            } else {
                // OK, this is weird, we got an element which is not
                // EyesWebElement?? Log it and try to move on.
                logger.verbose("Element is not an EyesWebElement " +
                        "(when trying to get border-top-width) " +
                        "Element's class: " +
                        element.getClass().getName());
                logger.verbose("Using getCssValue...");
                propValue = element.getCssValue("border-top-width");
                logger.verbose("Done!");
            }
            topBorderWidth = Math.round(Float.valueOf(
                    propValue.trim().replace("px", "")
            ));
            logger.verbose("border-top-width: " + topBorderWidth);
        } catch (WebDriverException e) {
            logger.verbose(String.format(
                    "Couldn't get the element's border-top-width: %s. " +
                            "Falling back to default",
                    e.getMessage()));
            topBorderWidth = 0;
        }

        Location contentLocation = new Location(location).offset(leftBorderWidth, topBorderWidth);
        logger.verbose("Done!");
        return contentLocation;
    }
}
