package com.applitools.eyes.positioning;

import java.awt.image.BufferedImage;

/**
 * Encapsulates cutting logic.
 */
public interface CutProvider {

    /**
     *
     * @param image The image to cut.
     * @return A new cut image.
     */
    BufferedImage cut(BufferedImage image);


    /**
     * Get a scaled version of the cut provider.
     *
     * @param scaleRatio The ratio by which to scale the current cut parameters.
     * @return A new scale cut provider instance.
     */
    CutProvider scale(double scaleRatio);
}
