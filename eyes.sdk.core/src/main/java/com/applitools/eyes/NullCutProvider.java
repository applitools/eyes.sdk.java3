package com.applitools.eyes;

/**
 * A cut provider which does nothing.
 */
public final class NullCutProvider extends UnscaledFixedCutProvider {

    public NullCutProvider() {
        super(0, 0, 0, 0);
    }
}
