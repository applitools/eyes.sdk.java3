/*
 * Applitools SDK for Selenium integration.
 */
package com.applitools.eyes.triggers;

import com.applitools.eyes.Location;
import com.applitools.eyes.Region;
import com.applitools.eyes.Trigger;
import com.applitools.utils.ArgumentGuard;


/**
 * Encapsulates a mouse trigger.
 */
public class MouseTrigger extends Trigger {
    private MouseAction mouseAction;
    private Region control;

    /**
     * Relative to the top left corner of {@link #control}, or null if unknown.
     */
    private Location location;


    public MouseTrigger(MouseAction mouseAction, Region control,
                        Location location) {

        ArgumentGuard.notNull(mouseAction, "mouseAction");
        ArgumentGuard.notNull(control, "control");

        this.mouseAction = mouseAction;
        this.control = control;
        this.location = location;
    }

    public MouseAction getMouseAction() {
        return mouseAction;
    }

    public Region getControl() {
        return control;
    }

    public Location getLocation() {
        return location;
    }

    public TriggerType getTriggerType() {
        return TriggerType.Mouse;
    }

    @Override
    public String toString() {
        return String.format("%s [%s] %s", mouseAction, control, location);
    }
}