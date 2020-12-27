package com.applitools.eyes.selenium.triggers;

import com.applitools.eyes.Location;
import com.applitools.eyes.Region;
import com.applitools.eyes.selenium.EyesDriverUtils;
import com.applitools.eyes.selenium.wrappers.EyesSeleniumDriver;
import com.applitools.eyes.triggers.MouseAction;
import com.applitools.utils.ArgumentGuard;
import org.openqa.selenium.interactions.Coordinates;
import org.openqa.selenium.interactions.Mouse;

/**
 * A wrapper class for Selenium's Mouse class. It adds saving of mouse events
 * so they can be sent to the agent later on.
 */
public class EyesMouse implements Mouse {

    private final EyesSeleniumDriver eyesDriver;
    private final Mouse mouse;
    private Location mouseLocation;

    public EyesMouse(EyesSeleniumDriver eyesDriver, Mouse mouse) {
        ArgumentGuard.notNull(eyesDriver, "eyesDriver");
        ArgumentGuard.notNull(mouse, "mouse");

        this.eyesDriver = eyesDriver;
        this.mouse = mouse;
        this.mouseLocation = new Location(0, 0);
    }

    /**
     * Moves the mouse according to the coordinates, if required.
     *
     * @param where Optional. The coordinates to move to. If null,
     *              mouse position does not changes.
     */
    protected void moveIfNeeded(Coordinates where) {
        if (where != null) {
            mouseMove(where);
        }
    }

    public void click(Coordinates where) {
        moveIfNeeded(where);
        addMouseTrigger(MouseAction.Click);
        mouse.click(where);
    }

    public void doubleClick(Coordinates where) {
        moveIfNeeded(where);
        addMouseTrigger(MouseAction.DoubleClick);
        mouse.doubleClick(where);
    }

    public void mouseDown(Coordinates where) {
        moveIfNeeded(where);
        addMouseTrigger(MouseAction.Down);
        mouse.mouseDown(where);
    }

    public void mouseUp(Coordinates where) {
        moveIfNeeded(where);
        addMouseTrigger(MouseAction.Up);
        mouse.mouseUp(where);
    }

    public void mouseMove(Coordinates where) {
        Location location = EyesDriverUtils.getPageLocation(where);
        if (location != null) {
            int newX = Math.max(0, location.getX());
            int newY = Math.max(0, location.getY());
            mouseLocation = new Location(newX, newY);
            addMouseTrigger(MouseAction.Move);
        }

        mouse.mouseMove(where);
    }

    public void mouseMove(Coordinates where, long xOffset, long yOffset) {
        Location location = EyesDriverUtils.getPageLocation(where);
        int newX, newY;
        if (location != null) {
            newX = (int)(location.getX() + xOffset);
            newY = (int)(location.getY() + yOffset);
        } else {
            newX = (int)(mouseLocation.getX() + xOffset);
            newY = (int)(mouseLocation.getY() + yOffset);
        }

        if (newX < 0) {
            newX = 0;
        }

        if (newY < 0) {
            newY = 0;
        }

        mouseLocation = new Location(newX, newY);

        addMouseTrigger(MouseAction.Move);

        mouse.mouseMove(where, xOffset, yOffset);
    }

    public void contextClick(Coordinates where) {
        moveIfNeeded(where);
        addMouseTrigger(MouseAction.RightClick);
        mouse.contextClick(where);
    }

    protected void addMouseTrigger(MouseAction action) {
        // Notice we send a copy of 'mouseLocation' to make sure the callee
        // will not change its values thus affecting our internal state.
        eyesDriver.getEyes().addMouseTrigger(
                action, Region.EMPTY, mouseLocation);
    }
}
