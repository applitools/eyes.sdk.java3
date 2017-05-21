/*
 * Applitools software.
 */
package com.applitools.eyes.selenium;

import com.applitools.eyes.Location;
import com.applitools.eyes.Logger;
import com.applitools.eyes.RectangleSize;
import com.applitools.utils.ArgumentGuard;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

/**
 * Encapsulates a frame/iframe. This is a generic type class,
 * and it's actual type is determined by the reference used by the user in
 * order to switch into the frame.
 */
public class Frame {
    // A user can switch into a frame by either its name,
    // index or by passing the relevant web element.
    protected final Logger logger;
    protected final WebElement reference;
    protected final String id;
    protected final Location location;
    protected final RectangleSize size;
    protected final Location parentScrollPosition;

    /**
     * @param logger A Logger instance.
     * @param reference The web element for the frame, used as a reference to
     *                  switch into the frame.
     * @param frameId The id of the frame. Can be used later for comparing
     *                two frames.
     * @param location The location of the frame within the current frame.
     * @param size The frame element size (i.e., the size of the frame on the
     *             screen, not the internal document size).
     * @param parentScrollPosition The scroll position the frame's parent was
     *                             in when the frame was switched to.
     */
    public Frame(Logger logger, WebElement reference,
                 String frameId, Location location, RectangleSize size,
                 Location parentScrollPosition) {
        ArgumentGuard.notNull(logger, "logger");
        ArgumentGuard.notNull(reference, "reference");
        ArgumentGuard.notNull(frameId, "frameId");
        ArgumentGuard.notNull(location, "location");
        ArgumentGuard.notNull(size, "size");
        ArgumentGuard.notNull(parentScrollPosition, "parentScrollPosition");

        logger.verbose(String.format(
                "Frame(logger, reference, %s, %s, %s, %s)", frameId,
                location, size, parentScrollPosition));

        this.logger = logger;
        this.reference = reference;
        this.id = frameId;
        this.parentScrollPosition = parentScrollPosition;
        this.size = size;
        this.location = location;
    }

    public WebElement getReference() {
        return reference;
    }

    public String getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }

    public RectangleSize getSize() {
        return size;
    }

    public Location getParentScrollPosition() {
        return parentScrollPosition;
    }
}
