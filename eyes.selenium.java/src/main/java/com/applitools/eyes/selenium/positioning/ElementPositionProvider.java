package com.applitools.eyes.selenium.positioning;

import com.applitools.eyes.*;
import com.applitools.eyes.positioning.PositionMemento;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.eyes.selenium.wrappers.EyesRemoteWebElement;
import com.applitools.eyes.selenium.wrappers.EyesSeleniumDriver;
import com.applitools.utils.ArgumentGuard;
import org.openqa.selenium.*;

public class ElementPositionProvider implements PositionProvider ,ISeleniumPositionProvider{
    private final EyesRemoteWebElement element;

    public ElementPositionProvider(Logger logger, EyesSeleniumDriver driver, WebElement element) {
        ArgumentGuard.notNull(logger, "logger");
        ArgumentGuard.notNull(driver, "driver");
        ArgumentGuard.notNull(element, "element");

        this.element = (element instanceof EyesRemoteWebElement) ?
                (EyesRemoteWebElement) element : new EyesRemoteWebElement(logger, driver, element);
        }



    /**
     * @return The scroll position of the current element.
     */
    public Location getCurrentPosition() {
        return new Location(element.getScrollLeft(), element.getScrollTop());
    }

    /**
     * Go to the specified location.
     * @param location The position to scroll to.
     */
    public Location setPosition(Location location) {
        Location afterScrollPosition = element.scrollTo(location);
        if (location.getY() != 0 && afterScrollPosition.getY() < location.getY()) {
            // We should wait until scroll action will be finished
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            afterScrollPosition = getCurrentPosition();
        }
        return afterScrollPosition;
    }

    /**
     * @return The entire size of the container which the position is relative
     * to.
     */
    public RectangleSize getEntireSize() {
        return new RectangleSize(element.getScrollWidth(), element.getScrollHeight());
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
