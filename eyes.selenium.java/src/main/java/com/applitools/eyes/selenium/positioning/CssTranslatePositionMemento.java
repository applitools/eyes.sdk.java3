package com.applitools.eyes.selenium.positioning;

import com.applitools.eyes.Location;
import com.applitools.eyes.positioning.PositionMemento;

import java.util.Map;

/**
 * Encapsulates state for {@link CssTranslatePositionProvider} instances.
 */
public class CssTranslatePositionMemento extends PositionMemento {
    private final Map<String, String> transforms;
    private final Location position;

    /**
     *
     * @param transforms The current transforms. The keys are the style keys
     *                   from which each of the transforms were taken.
     */
    public CssTranslatePositionMemento(Map<String, String> transforms, Location position) {
        this.transforms = transforms;
        this.position = position;
    }

    /**
     *
     * @return The current transforms. The keys are the style keys from
     * which each of the transforms were taken.
     */
    public Map<String, String> getTransform() {
        return transforms;
    }

    public Location getPosition() { return position; }
}
