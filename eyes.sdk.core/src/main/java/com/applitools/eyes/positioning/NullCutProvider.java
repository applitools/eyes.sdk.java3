package com.applitools.eyes.positioning;

/**
 * A cut provider which does nothing.
 */
public class NullCutProvider extends UnscaledFixedCutProvider {

    public NullCutProvider() {
        super(0, 0, 0, 0);
    }
}
