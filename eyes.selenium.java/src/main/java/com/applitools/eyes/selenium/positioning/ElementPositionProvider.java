package com.applitools.eyes.selenium.positioning;

import com.applitools.eyes.*;
import com.applitools.eyes.positioning.PositionMemento;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.eyes.selenium.wrappers.EyesRemoteWebElement;
import com.applitools.eyes.selenium.wrappers.EyesSeleniumDriver;
import com.applitools.utils.ArgumentGuard;
import org.openqa.selenium.*;

public class ElementPositionProvider implements PositionProvider ,ISeleniumPositionProvider{
    private final Logger logger;
    private final EyesRemoteWebElement element;

    public ElementPositionProvider(Logger logger, EyesSeleniumDriver driver, WebElement element) {
        ArgumentGuard.notNull(logger, "logger");
        ArgumentGuard.notNull(driver, "driver");
        ArgumentGuard.notNull(element, "element");

        this.logger = logger;
        this.element = (element instanceof EyesRemoteWebElement) ?
                (EyesRemoteWebElement) element : new EyesRemoteWebElement(logger, driver, element);

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

        Location result = element.scrollTo(location);

        logger.verbose("Done scrolling element! result: " + result);
        return result;
    }

    /**
     * @return The entire size of the container which the position is relative
     * to.
     */
    public RectangleSize getEntireSize() {
        logger.verbose("enter");

        RectangleSize result = new RectangleSize(element.getScrollWidth(), element.getScrollHeight());

        logger.verbose("Entire size: " + result);
        return result;
    }

    public PositionMemento getState() {
        return new ElementPositionMemento(getCurrentPosition());
    }

    public void restoreState(PositionMemento state) {
        ElementPositionMemento s = (ElementPositionMemento) state;
        setPosition(new Location(s.getX(), s.getY()));
    }

    public EyesRemoteWebElement getElement() {
        return element;
    }

    @Override
    public WebElement getScrolledElement() {
        return element;
    }
}
