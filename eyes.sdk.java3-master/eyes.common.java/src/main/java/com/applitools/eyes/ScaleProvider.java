package com.applitools.eyes;

/**
 * Encapsulates scaling logic.
 */
public interface ScaleProvider {
    /**
     *
     * @return The ratio by which an image will be scaled.
     */
    double getScaleRatio();
}
