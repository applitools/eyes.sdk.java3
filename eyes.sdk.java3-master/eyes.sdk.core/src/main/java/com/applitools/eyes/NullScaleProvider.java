package com.applitools.eyes;

/**
 * A scale provider which does nothing.
 */
public class NullScaleProvider extends FixedScaleProvider {

    public NullScaleProvider() {
        super(1);
    }
}
