package com.applitools.eyes;

import java.awt.image.BufferedImage;

/**
 * Encapsulates the instantiation of an EyesScreenshot object.
 */
public interface EyesScreenshotFactory {
    EyesScreenshot makeScreenshot(BufferedImage image);
}
