package com.applitools.eyes.debug;

import java.awt.image.BufferedImage;

/**
 * A mock debug screenshot provider.
 */
public class NullDebugScreenshotProvider extends DebugScreenshotsProvider {

    @Override
    public void save(BufferedImage image, String suffix) {
        // Do nothing.
    }
}
