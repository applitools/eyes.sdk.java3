package com.applitools.eyes.selenium.positioning;

import com.applitools.eyes.*;
import com.applitools.eyes.positioning.PositionMemento;
import com.applitools.eyes.selenium.EyesDriverUtils;
import com.applitools.utils.ArgumentGuard;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

public class SeleniumScrollPositionProvider implements ScrollPositionProvider, ISeleniumPositionProvider {

    protected final Logger logger;
    protected final IEyesJsExecutor executor;
    protected final WebElement scrollRootElement;

    public SeleniumScrollPositionProvider(Logger logger, IEyesJsExecutor executor, WebElement scrollRootElement) {
        ArgumentGuard.notNull(logger, "logger");
        ArgumentGuard.notNull(executor, "executor");
//        ArgumentGuard.notNull(scrollRootElement, "scrollRootElement");

        this.logger = logger;
        this.executor = executor;
        this.scrollRootElement = scrollRootElement;
    }

    public static Location getCurrentPosition(IEyesJsExecutor executor, WebElement scrollRootElement) {
        Object position = executor.executeScript("return arguments[0].scrollLeft+';'+arguments[0].scrollTop;", scrollRootElement);
        return EyesDriverUtils.parseLocationString(position);
    }

    /**
     * @return The scroll position of the current frame.
     */
    public Location getCurrentPosition() {
        return getCurrentPosition(executor, scrollRootElement);
    }

    /**
     * Go to the specified location.
     * @param location The position to scroll to.
     */
    @Override
    public Location setPosition(Location location) {
        Object position = executor.executeScript(String.format("arguments[0].scrollLeft=%d; arguments[0].scrollTop=%d; return (arguments[0].scrollLeft+';'+arguments[0].scrollTop);",
                location.getX(), location.getY()),
                scrollRootElement);
        return EyesDriverUtils.parseLocationString(position);
    }

    @Override
    public void setPosition(WebElement element) {
        Point loc = element.getLocation();
        setPosition(new Location(loc.x, loc.y));
    }

    /**
     *
     * @return The entire size of the container which the position is relative
     * to.
     */
    public RectangleSize getEntireSize() {
        return EyesDriverUtils.getEntireElementSize(logger, executor, scrollRootElement);
    }

    public PositionMemento getState() {
        return new ScrollPositionMemento(getCurrentPosition());
    }

    public void restoreState(PositionMemento state) {
        ScrollPositionMemento s = (ScrollPositionMemento) state;
        setPosition(new Location(s.getX(), s.getY()));
    }

    @Override
    public WebElement getScrolledElement() {
        return this.scrollRootElement;
    }


    public boolean equals(SeleniumScrollPositionProvider other){
        return this.scrollRootElement.equals(other.scrollRootElement);
    }

    @Override
    public boolean equals(Object other){
        if (other instanceof SeleniumScrollPositionProvider) {
            return equals((SeleniumScrollPositionProvider)other);
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (this.scrollRootElement != null)
            return this.scrollRootElement.hashCode();
        return 0;
    }

    @Override
    public String toString() {
        return "ScrollPositionProvider{" +
                "scrollRootElement=" + scrollRootElement +
                '}';
    }
}
