package com.applitools.eyes;

import com.applitools.utils.ImageUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * An application output (title, image, etc).
 */
public class AppOutput {

    /**
     * The title of the screen of the application being captured.
     */
    private final String title;
    private final String domUrl;
    private String screenshotUrl;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private RectangleSize viewport;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Location location;

    @JsonIgnore
    private final EyesScreenshot screenshot;

    @JsonIgnore
    private final byte[] screenshotBytes;

    /**
     * @param title           The title of the window.
     * @param screenshot      The screenshot.
     * @param domUrl          A URL to a DOM snapshot.
     * @param screenshotUrl   A URL to a screenshot.
     */
    public AppOutput(String title, EyesScreenshot screenshot, String domUrl, String screenshotUrl, Location location) {
        this.title = title;
        this.domUrl = domUrl;
        this.screenshotUrl = screenshotUrl;
        this.location = location;
        this.screenshot = screenshot;
        this.screenshotBytes = screenshot == null ? null : ImageUtils.encodeAsPng(screenshot.getImage());
    }

    public AppOutput(String title, EyesScreenshot screenshot, String domUrl, String screenshotUrl, Location location, RectangleSize viewport) {
        this(title, screenshot, domUrl, screenshotUrl, location);
        this.viewport = viewport;
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

    public RectangleSize getViewport() {
        return viewport;
    }

    public Location getLocation() {
        return location;
    }

    public EyesScreenshot getScreenshot() {
        return screenshot;
    }
}
