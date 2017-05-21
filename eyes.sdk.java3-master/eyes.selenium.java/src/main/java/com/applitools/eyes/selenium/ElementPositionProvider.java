package com.applitools.eyes.selenium;

import com.applitools.eyes.*;
import com.applitools.utils.ArgumentGuard;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebElement;

public class ElementPositionProvider implements PositionProvider {
    private final Logger logger;
    private final EyesWebDriver driver;
    private final EyesRemoteWebElement element;

    public ElementPositionProvider(Logger logger, EyesWebDriver driver,
                                   WebElement element) {
        ArgumentGuard.notNull(logger, "logger");
        ArgumentGuard.notNull(driver, "driver");
        ArgumentGuard.notNull(element, "element");

        this.logger = logger;
        this.driver = driver;
        this.element = new EyesRemoteWebElement(logger, driver,
                (RemoteWebElement)element);
    }

    /**
     * @return The scroll position of the current element.
     */
    public Location getCurrentPosition() {
        logger.verbose("getCurrentScrollPosition()");

        Location result = new Location(element.getScrollLeft(),
                element.getScrollTop());

        logger.verbose(String.format("Current position: %s", result));

        return result;
    }

    /**
     * Go to the specified location.
     * @param location The position to scroll to.
     */
    public void setPosition(Location location) {
        logger.verbose(String.format("Scrolling element to %s", location));

        element.scrollTo(location);

        logger.verbose("Done scrolling element!");
    }

    /**
     *
     * @return The entire size of the container which the position is relative
     * to.
     */
    public RectangleSize getEntireSize() {
        logger.verbose("getEntireSize()");

        RectangleSize result = new RectangleSize(element.getScrollWidth(),
                element.getScrollHeight());

        logger.verbose(String.format("Entire size: %s", result));
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
