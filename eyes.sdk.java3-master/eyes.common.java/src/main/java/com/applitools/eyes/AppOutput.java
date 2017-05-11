package com.applitools.eyes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * An application output (title, image, etc).
 */
@JsonIgnoreProperties({"screenshot64"})
public class AppOutput {

    /**
     * The title of the screen of the application being captured.
     */
    private final String title;
    private final String screenshot64;

    /**
     * @param title        The title of the window.
     * @param screenshot64 Base64 encoding of the screenshot's bytes (the
     *                     byte can be in either in compressed or
     *                     uncompressed form)
     */
    public AppOutput(String title, String screenshot64) {
        this.title = title;
        this.screenshot64 = screenshot64;
    }

    public String getTitle() {
        return title;
    }

    public String getScreenshot64() {
        return screenshot64;
    }
}