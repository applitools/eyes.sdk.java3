package com.applitools.eyes.capture;

import com.applitools.eyes.EyesScreenshot;

import java.awt.image.BufferedImage;

/**
 * Encapsulates the instantiation of an EyesScreenshot object.
 */
public interface EyesScreenshotFactory {
    EyesScreenshot makeScreenshot(BufferedImage image);
}
