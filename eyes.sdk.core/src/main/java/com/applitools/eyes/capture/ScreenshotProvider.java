package com.applitools.eyes.capture;

import com.applitools.eyes.logging.Stage;

import java.awt.image.BufferedImage;

public interface ScreenshotProvider {
    BufferedImage getViewPortScreenshot(Stage stage);
}
