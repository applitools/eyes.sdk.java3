package com.applitools.eyes.debug;

import java.awt.image.BufferedImage;

/**
 * Interface for saving debug screenshots.
 */
public abstract class DebugScreenshotsProvider {

    private static final String DEFAULT_PREFIX = "screenshot_";
    private static final String DEFAULT_PATH = "";

    private String prefix;
    private String path;

    public DebugScreenshotsProvider() {
        prefix = DEFAULT_PREFIX;
        path = null;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix == null ? DEFAULT_PREFIX : prefix;
    }

    public void setPath(String path) {
        if (path != null) {
            path = path.endsWith("/") ? path : path + '/';
        } else {
            path = DEFAULT_PATH;
        }

        this.path = path;
    }

    public String getPath() {
        return path;
    }

    abstract public void save(BufferedImage image, String suffix);
}
