package com.applitools.eyes.selenium.positioning;

import com.applitools.eyes.Location;
import com.applitools.eyes.positioning.PositionMemento;

/**
 * Encapsulates state for {@link CssTranslatePositionProvider} instances.
 */
public class CssTranslatePositionMemento extends PositionMemento {
    private final String transform;
    private final Location position;

    /**
     * @param transform The current transform.
     */
    public CssTranslatePositionMemento(String transform, Location position) {
        this.transform = transform;
        this.position = position;
    }

    /**
     * @return The current transform.
     */
    public String getTransform() {
        return transform;
    }

    public Location getPosition() { return position; }
}
