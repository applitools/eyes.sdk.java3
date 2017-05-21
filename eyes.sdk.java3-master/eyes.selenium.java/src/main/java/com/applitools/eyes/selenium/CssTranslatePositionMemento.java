package com.applitools.eyes.selenium;

import com.applitools.eyes.PositionMemento;

import java.util.Map;

/**
 * Encapsulates state for {@link CssTranslatePositionProvider} instances.
 */
public class CssTranslatePositionMemento extends PositionMemento {
    private final Map<String, String> transforms;

    /**
     *
     * @param transforms The current transforms. The keys are the style keys
     *                   from which each of the transforms were taken.
     */
    public CssTranslatePositionMemento(Map<String, String> transforms) {
        this.transforms = transforms;
    }

    /**
     *
     * @return The current transforms. The keys are the style keys from
     * which each of the transforms were taken.
     */
    public Map<String, String> getTransform() {
        return transforms;
    }
}
