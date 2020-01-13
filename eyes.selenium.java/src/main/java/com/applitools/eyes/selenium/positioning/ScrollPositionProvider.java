package com.applitools.eyes.selenium.positioning;

import com.applitools.eyes.*;
import com.applitools.eyes.positioning.PositionMemento;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.utils.ArgumentGuard;
import org.openqa.selenium.WebElement;

public class ScrollPositionProvider implements PositionProvider, ISeleniumPositionProvider {


    protected final Logger logger;
    protected final IEyesJsExecutor executor;
    protected final WebElement scrollRootElement;

    private final String JS_GET_ENTIRE_PAGE_SIZE =
            "var width = Math.max(arguments[0].clientWidth, arguments[0].scrollWidth);" +
                    "var height = Math.max(arguments[0].clientHeight, arguments[0].scrollHeight);" +
                    "return (width + ',' + height);";

    public ScrollPositionProvider(Logger logger, IEyesJsExecutor executor, WebElement scrollRootElement) {
        ArgumentGuard.notNull(logger, "logger");
        ArgumentGuard.notNull(executor, "executor");
//        ArgumentGuard.notNull(scrollRootElement, "scrollRootElement");

        this.logger = logger;
        this.executor = executor;
        this.scrollRootElement = scrollRootElement;

        logger.verbose("creating ScrollPositionProvider");
    }

    public static Location getCurrentPosition(IEyesJsExecutor executor, WebElement scrollRootElement) {
        Object position = executor.executeScript("return arguments[0].scrollLeft+';'+arguments[0].scrollTop;", scrollRootElement);
        return parseLocationString(position);
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
    public Location setPosition(Location location) {
        logger.verbose(String.format("setting position of %s to %s", scrollRootElement, location));
        Object position = executor.executeScript(String.format("arguments[0].scrollLeft=%d; arguments[0].scrollTop=%d; return (arguments[0].scrollLeft+';'+arguments[0].scrollTop);",
                location.getX(), location.getY()),
                scrollRootElement);
        return parseLocationString(position);
    }

    public static Location parseLocationString(Object position) {
        String[] xy = position.toString().split(";");
        if (xy.length != 2)
        {
            throw new EyesException("Could not get scroll position!");
        }
        float x = Float.parseFloat(xy[0]);
        float y = Float.parseFloat(xy[1]);
        return new Location((int)Math.ceil(x), (int)Math.ceil(y));
    }

    /**
     *
     * @return The entire size of the container which the position is relative
     * to.
     */
    public RectangleSize getEntireSize() {

        logger.verbose(String.format("enter (scrollRootElement: %s)", scrollRootElement));
        String entireSizeStr = (String)executor.executeScript(JS_GET_ENTIRE_PAGE_SIZE, scrollRootElement);
        String[] wh = entireSizeStr.split(",");
        if (wh.length != 2)
        {
            throw new EyesException("Could not get entire size!");
        }
        float w = Float.parseFloat(wh[0]);
        float h = Float.parseFloat(wh[1]);
        RectangleSize result = new RectangleSize((int)Math.ceil(w), (int)Math.ceil(h));
        logger.verbose("ScrollPositionProvider - Entire size: " + result);
        return result;
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


    public boolean equals(ScrollPositionProvider other){
        return this.scrollRootElement.equals(other.scrollRootElement);
    }

    @Override
    public boolean equals(Object other){
        if (other instanceof  ScrollPositionProvider) {
            return equals((ScrollPositionProvider)other);
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
