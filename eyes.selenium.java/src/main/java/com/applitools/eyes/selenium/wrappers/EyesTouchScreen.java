package com.applitools.eyes.selenium.wrappers;

import com.applitools.eyes.Location;
import com.applitools.eyes.Region;
import com.applitools.eyes.selenium.EyesDriverUtils;
import com.applitools.eyes.triggers.MouseAction;
import com.applitools.utils.ArgumentGuard;
import org.openqa.selenium.interactions.Coordinates;
import org.openqa.selenium.interactions.TouchScreen;


/**
 * A wrapper class for TouchScreen implementation. This class will record
 * certain events such as tap.
 */
public class EyesTouchScreen implements TouchScreen {

    private final EyesSeleniumDriver driver;
    private final TouchScreen touch;

    public EyesTouchScreen(EyesSeleniumDriver driver, TouchScreen touch) {
        ArgumentGuard.notNull(driver, "driver");
        ArgumentGuard.notNull(touch, "touch");

        this.driver = driver;
        this.touch = touch;
    }

    /**
     * A tap action. From our point of view, it is the same as a click.
     * @param where Where to tap.
     */
    public void singleTap(Coordinates where) {
        // This is not a mistake - Appium only supports getPageLocation (and
        // the result is relative to the viewPort)
        Location location = EyesDriverUtils.getPageLocation(where);
        driver.getEyes().addMouseTrigger(MouseAction.Click, Region.EMPTY, location);
        touch.singleTap(where);
    }

    public void down(int x, int y) {
        touch.down(x, y);
    }

    public void up(int x, int y) {
        touch.up(x, y);
    }

    public void move(int x, int y) {
        touch.move(x, y);
    }

    public void scroll(Coordinates where, int xOffset, int yOffset) {
        touch.scroll(where, xOffset, yOffset);
    }

    /**
     * Double tap action. We treat it the same as a double click.
     * @param where Where to double click.
     */
    public void doubleTap(Coordinates where) {
        // This is not a mistake - Appium only supports getPageLocation (and
        // the result is relative to the viewPort)
        Location location = EyesDriverUtils.getPageLocation(where);
        driver.getEyes().addMouseTrigger(MouseAction.DoubleClick, Region.EMPTY, location);
        touch.doubleTap(where);
    }

    public void longPress(Coordinates where) {
        touch.longPress(where);
    }

    public void scroll(int xOffset, int yOffset) {
        touch.scroll(xOffset, yOffset);
    }

    public void flick(int xSpeed, int ySpeed) {
        touch.flick(xSpeed, ySpeed);
    }

    public void flick(Coordinates where, int xOffset, int yOffset, int speed) {
        touch.flick(where, xOffset, yOffset, speed);
    }
}