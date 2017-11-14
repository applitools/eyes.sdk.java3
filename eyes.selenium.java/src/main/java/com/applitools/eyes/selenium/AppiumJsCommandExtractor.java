/*
 * Applitools SDK for Selenium integration.
 */
package com.applitools.eyes.selenium;

import com.applitools.eyes.*;
import com.applitools.eyes.triggers.MouseAction;
import com.applitools.eyes.triggers.MouseTrigger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import java.util.Map;

public class AppiumJsCommandExtractor {
    private final static String COMMAND_PREFIX = "mobile: ";
    private final static String TAP_COMMAND = COMMAND_PREFIX + "tap";
    private final static double APPIUM_COORDINATES_DEFAULT = 0.5;
    private static final int APPIUM_TAP_COUNT_DEFAULT = 1;

    /**
     * Used for identifying if a javascript script is a command to Appium.
     * @param script The script to test whether it's an Appium command.
     * @return True if the script is an Appium command, false otherwise.
     */
    public static boolean isAppiumJsCommand(String script) {
        return script.startsWith(COMMAND_PREFIX);
    }

    /**
     * Given a command and its parameters, returns the equivalent trigger.
     * @param elementsIds A mapping of known elements' IDs to elements.
     * @param viewportSize The dimensions of the current viewport
     * @param script The Appium command from which the trigger would be
     *               extracted
     * @param args The trigger's parameters.
     * @return The trigger which represents the given command.
     */
    public static Trigger extractTrigger(
            Map<String, WebElement> elementsIds,
            Dimension viewportSize,
            String script,
            Object... args) {

        if (script.equals(TAP_COMMAND)) {
            if (args.length != 1) {
                // We don't know what the rest of the parameters are, so...
                return null;
            }

            Map<String, String> tapObject;

            Region control;
            Location location;

            double x, y;
            int tapCount;
            String xObj;
            String yObj;
            String tapCountObj;


            try {
                tapObject = (Map<String, String>) args[0];
                xObj  = tapObject.get("x");
                yObj  = tapObject.get("y");
                tapCountObj  = tapObject.get("tapCount");
            } catch (ClassCastException e) {
                // We only know how to handle Map as the arguments container.
                return null;
            }



            x = (xObj != null) ? Double.valueOf(xObj) :
                                    APPIUM_COORDINATES_DEFAULT;
            y = (yObj != null) ? Double.valueOf(yObj) :
                                APPIUM_COORDINATES_DEFAULT;



            // If an element is referenced, then the coordinates are relative
            // to the element.
            WebElement referencedElement;
            String elementId = tapObject.get("element");
            if (elementId != null) {
                referencedElement = elementsIds.get(elementId);

                // If an element was referenced, but we don't have it's ID,
                // we can't create the trigger.
                if (referencedElement == null) {
                    return null;
                }

                Point elementPosition = referencedElement.getLocation();
                Dimension elementSize = referencedElement.getSize();


                control = new Region(elementPosition.getX(),
                                        elementPosition.getY(),
                                        elementSize.getWidth(),
                                        elementSize.getHeight());

                // If coordinates are percentage of the size of the
                // viewport/element.
                if (x < 1) {
                    x = control.getWidth() * x;
                }
                if (y < 1) {
                    y = control.getHeight() * y;
                }

            } else {
                // If coordinates are percentage of the size of the
                // viewport/element.
                if (x < 1) {
                    x = viewportSize.getWidth() * x;
                }
                if (y < 1) {
                    y = viewportSize.getHeight() * y;
                }

                // creating a fake control, for which the tap is at the right
                // bottom corner
                control = new Region(0, 0,
                                    (int) Math.round(x), (int) Math.round(y));
            }


            location = new Location((int) Math.round(x), (int) Math.round(y));

            // Deciding whether this is click/double click.
            tapCount = (tapCountObj != null) ?
                            Integer.valueOf(tapCountObj) :
                            APPIUM_TAP_COUNT_DEFAULT;
            MouseAction action = (tapCount == 1) ? MouseAction.Click :
                                                    MouseAction.DoubleClick;

            return new MouseTrigger(action, control, location);
        }

        // No trigger from the given command.
        return null;
    }
}