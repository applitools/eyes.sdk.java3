package com.applitools.eyes.selenium.positioning;

import com.applitools.eyes.*;
import com.applitools.eyes.positioning.PositionMemento;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.eyes.selenium.EyesSeleniumUtils;
import com.applitools.utils.ArgumentGuard;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * A {@link PositionProvider} which is based on CSS translates. This is
 * useful when we want to stitch a page which contains fixed position elements.
 */
public class CssTranslatePositionProvider implements PositionProvider {

    private final Logger logger;
    private final IEyesJsExecutor executor;
    private final WebElement scrollRootElement;
    private Location lastSetPosition; // cache.

    private static final String JSSetTransform =
            "var originalTransform = arguments[0].style.transform;" +
                    "arguments[0].style.transform = '%s';" +
                    "return originalTransform;";

    private static final String JSGetEntirePageSize =
            "var width = Math.max(arguments[0].clientWidth, arguments[0].scrollWidth);" +
                    "var height = Math.max(arguments[0].clientHeight, arguments[0].scrollHeight);" +
                    "return [width, height];";

    private static final String JSGetCurrentTransform =
            "return arguments[0].style.transform;";

    public CssTranslatePositionProvider(Logger logger, IEyesJsExecutor executor, WebElement scrollRootElement) {
        ArgumentGuard.notNull(logger, "logger");
        ArgumentGuard.notNull(executor, "executor");

        this.logger = logger;
        this.executor = executor;
        this.scrollRootElement = scrollRootElement;

        logger.verbose("creating CssTranslatePositionProvider");
    }

    public Location getCurrentPosition() {
        logger.verbose("position to return: " + lastSetPosition);
        return lastSetPosition;
    }

    public Location setPosition(Location location) {
        ArgumentGuard.notNull(location, "location");
        logger.verbose("CssTranslatePositionProvider - Setting position to: " + location);
        executor.executeScript(String.format(JSSetTransform, "translate(-" + location.getX() + "px, -" + location.getY() + "px)"), scrollRootElement);
        lastSetPosition = location;
        return lastSetPosition;
    }

    public RectangleSize getEntireSize() {
        logger.verbose("enter (scrollRootElement_: " + scrollRootElement + ")");
        Object retVal = executor.executeScript(JSGetEntirePageSize, scrollRootElement);
        List<Long> esAsList = (List<Long>) retVal;
        RectangleSize size = new RectangleSize(
                esAsList.get(0).intValue(),
                esAsList.get(1).intValue());
        logger.verbose(size.toString());
        return size;
    }

    public PositionMemento getState() {
        return new CssTranslatePositionMemento(
                (String) executor.executeScript(JSGetCurrentTransform, scrollRootElement),
                lastSetPosition);
    }

    public void restoreState(PositionMemento state) {
        executor.executeScript(String.format(JSSetTransform, ((CssTranslatePositionMemento) state).getTransform()), scrollRootElement);
        lastSetPosition = ((CssTranslatePositionMemento) state).getPosition();
    }

    @Override
    public String toString()
    {
        return String.format("CssTranslatePositionProvider (Last set position = %d ; scrollRootElement_ = %s)",
                lastSetPosition, scrollRootElement);
    }
}
