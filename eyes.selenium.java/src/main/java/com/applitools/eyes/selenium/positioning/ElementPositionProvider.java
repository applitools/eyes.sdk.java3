package com.applitools.eyes.selenium.positioning;

import com.applitools.eyes.*;
import com.applitools.eyes.positioning.PositionMemento;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.eyes.selenium.wrappers.EyesRemoteWebElement;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import com.applitools.utils.ArgumentGuard;
import org.openqa.selenium.*;

public class ElementPositionProvider implements PositionProvider {
    private final Logger logger;
    private final EyesRemoteWebElement element;

    public ElementPositionProvider(Logger logger, EyesWebDriver driver, WebElement element) {
        ArgumentGuard.notNull(logger, "logger");
        ArgumentGuard.notNull(driver, "driver");
        ArgumentGuard.notNull(element, "element");

        this.logger = logger;
        this.element = (element instanceof  EyesRemoteWebElement) ?
                (EyesRemoteWebElement)element : new EyesRemoteWebElement(logger, driver, element);

        logger.verbose("creating ElementPositionProvider");
    }

    /**
     * @return The scroll position of the current element.
     */
    public Location getCurrentPosition() {
        logger.verbose("getCurrentScrollPosition()");

        Location result = new Location(element.getScrollLeft(), element.getScrollTop());

        logger.verbose(String.format("Current position: %s", result));

        return result;
    }

    /**
     * Go to the specified location.
     * @param location The position to scroll to.
     */
    public Location setPosition(Location location) {
        logger.verbose(String.format("Scrolling element to %s", location));
        return element.scrollTo(location);
    }

    /**
     *
     * @return The entire size of the container which the position is relative
     * to.
     */
    public RectangleSize getEntireSize() {
        logger.verbose("ElementPositionProvider - getEntireSize()");

        RectangleSize result = new RectangleSize(element.getScrollWidth(), element.getScrollHeight());

        logger.verbose("ElementPositionProvider - Entire size: " + result);
        return result;
    }

    public PositionMemento getState() {
        return new ElementPositionMemento(getCurrentPosition());
    }

    public void restoreState(PositionMemento state) {
        ElementPositionMemento s = (ElementPositionMemento) state;
        setPosition(new Location(s.getX(), s.getY()));
    }
}
