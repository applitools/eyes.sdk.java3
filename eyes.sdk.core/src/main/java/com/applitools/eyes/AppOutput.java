package com.applitools.eyes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * An application output (title, image, etc).
 */
@JsonIgnoreProperties({"screenshotBytes"})
public class AppOutput {

    /**
     * The title of the screen of the application being captured.
     */
    private final String title;
    private final String domUrl;
    private String screenshotUrl;
    private final byte[] screenshotBytes;

    /**
     * @param title           The title of the window.
     * @param screenshotBytes The screenshot's bytes.
     * @param domUrl          A URL to a DOM snapshot.
     * @param screenshotUrl   A URL to a screenshot.
     */
    public AppOutput(String title, byte[] screenshotBytes, String domUrl, String screenshotUrl) {
        this.title = title;
        this.screenshotBytes = screenshotBytes;
        this.domUrl = domUrl;
        this.screenshotUrl = screenshotUrl;
    }

    public String getTitle() {
        return title;
    }

    public byte[] getScreenshotBytes() {
        return screenshotBytes;
    }

    public String getDomUrl() {
        return domUrl;
    }

    public String getScreenshotUrl() {
        return screenshotUrl;
    }

    public void setScreenshotUrl(String screenshotUrl) {
        this.screenshotUrl = screenshotUrl;
    }
}