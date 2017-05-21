package com.applitools.eyes.selenium;

import com.applitools.eyes.Location;
import com.applitools.eyes.PositionMemento;

/**
 * Encapsulates state for {@link ElementPositionProvider} instances.
 */
public class ElementPositionMemento extends PositionMemento {
    private final Location position;

    /**
     *
     * @param l The current location to be saved.
     */
    public ElementPositionMemento(Location l) {
        position = new Location(l);
    }

    public int getX() {
        return position.getX();
    }

    public int getY() {
        return position.getY();
    }
}
