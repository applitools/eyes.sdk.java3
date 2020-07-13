package com.applitools.eyes.selenium.positioning;

/**
 * Encapsulates rotation model for images.
 */
public class ImageRotation {
    private final int rotation;

    /**
     *
     * @param rotation The degrees by which to rotate.
     */
    public ImageRotation(int rotation) {
        this.rotation = rotation;
    }

    /**
     *
     * @return The degrees by which to rotate.
     */
    public int getRotation() {
        return rotation;
    }
}
