/*
 * Applitools software.
 */
package com.applitools.eyes.selenium.frames;

import com.applitools.eyes.Location;
import com.applitools.eyes.Logger;
import com.applitools.eyes.RectangleSize;
import com.applitools.utils.ArgumentGuard;
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
    protected final Location location;
    protected final RectangleSize size;
    protected final RectangleSize innerSize;
    protected final Location originalLocation;

    /**
     * @param logger               A Logger instance.
     * @param reference            The web element for the frame, used as a reference to switch into the frame.
     * @param location             The location of the frame within the current frame.
     * @param size                 The frame element size (i.e., the size of the frame on the screen, not the internal document size).
     * @param innerSize            The frame element inner size (i.e., the size of the frame actual size, without borders).
     * @param originalLocation     The scroll location of the frame.
     */
    public Frame(Logger logger, WebElement reference,
                 Location location, RectangleSize size, RectangleSize innerSize,
                 Location originalLocation) {
        ArgumentGuard.notNull(logger, "logger");
        ArgumentGuard.notNull(reference, "reference");
        ArgumentGuard.notNull(location, "location");
        ArgumentGuard.notNull(size, "size");
        ArgumentGuard.notNull(innerSize, "innerSize");
        ArgumentGuard.notNull(originalLocation, "originalLocation");

        logger.verbose(String.format(
                "Frame(logger, reference, %s, %s, %s, %s)",
                location, size, innerSize, originalLocation));

        this.logger = logger;
        this.reference = reference;
        this.location = location;
        this.size = size;
        this.innerSize = innerSize;
        this.originalLocation = originalLocation;
    }

    public WebElement getReference() {
        return reference;
    }

    public Location getLocation() {
        return location;
    }

    public RectangleSize getSize() {
        return size;
    }

    public RectangleSize getInnerSize() {
        return innerSize;
    }

    public Location getOriginalLocation() {
        return originalLocation;
    }

}
