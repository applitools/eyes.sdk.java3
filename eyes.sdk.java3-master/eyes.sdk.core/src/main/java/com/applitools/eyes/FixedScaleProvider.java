package com.applitools.eyes;

import com.applitools.utils.ArgumentGuard;

/**
 * Scale provider based on a fixed scale ratio.
 */
public class FixedScaleProvider implements ScaleProvider {

    private final double scaleRatio;

    /**
     *
     * @param scaleRatio The scale ratio to use.
     */
    public FixedScaleProvider(double scaleRatio) {
        ArgumentGuard.greaterThanZero(scaleRatio, "scaleRatio");
        this.scaleRatio = scaleRatio;
    }

    /**
     *
     * {@inheritDoc}
     */
    public double getScaleRatio() {
        return scaleRatio;
    }
}
