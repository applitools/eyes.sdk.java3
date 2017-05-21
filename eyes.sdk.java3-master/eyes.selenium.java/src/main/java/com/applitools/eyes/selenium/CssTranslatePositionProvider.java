package com.applitools.eyes.selenium;

import com.applitools.eyes.*;
import com.applitools.utils.ArgumentGuard;
import org.openqa.selenium.JavascriptExecutor;

/**
 * A {@link PositionProvider} which is based on CSS translates. This is
 * useful when we want to stitch a page which contains fixed position elements.
 */
public class CssTranslatePositionProvider implements PositionProvider {

    private final Logger logger;
    private final JavascriptExecutor executor;
    private Location lastSetPosition; // cache.

    public CssTranslatePositionProvider(Logger logger, JavascriptExecutor
            executor) {
        ArgumentGuard.notNull(logger, "logger");
        ArgumentGuard.notNull(executor, "executor");

        this.logger = logger;
        this.executor = executor;
    }

    public Location getCurrentPosition() {
        logger.verbose("position to return: " + lastSetPosition);
        return lastSetPosition;
    }

    public void setPosition(Location location) {
        ArgumentGuard.notNull(location, "location");
        logger.verbose("Setting position to: " + location);
        EyesSeleniumUtils.translateTo(executor, location);
        logger.verbose("Done!");
        lastSetPosition = location;
    }

    public RectangleSize getEntireSize() {
        RectangleSize entireSize =
                EyesSeleniumUtils.getCurrentFrameContentEntireSize(executor);
        logger.verbose("Entire size: " + entireSize);
        return entireSize;
    }

    public PositionMemento getState() {
        return new CssTranslatePositionMemento(
                EyesSeleniumUtils.getCurrentTransform(executor));
    }

    public void restoreState(PositionMemento state) {
        EyesSeleniumUtils.setTransforms(executor,
                ((CssTranslatePositionMemento)state).getTransform());
    }
}
