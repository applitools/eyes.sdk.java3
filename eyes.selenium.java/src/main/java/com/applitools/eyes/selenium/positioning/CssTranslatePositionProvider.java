package com.applitools.eyes.selenium.positioning;

import com.applitools.eyes.*;
import com.applitools.eyes.positioning.PositionMemento;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.eyes.selenium.EyesDriverUtils;
import com.applitools.utils.ArgumentGuard;
import org.openqa.selenium.WebElement;

/**
 * A {@link PositionProvider} which is based on CSS translates. This is
 * useful when we want to stitch a page which contains fixed position elements.
 */
public class CssTranslatePositionProvider implements PositionProvider, ISeleniumPositionProvider{

    protected final Logger logger;
    protected final IEyesJsExecutor executor;
    private final WebElement scrollRootElement;

    private final static String JSSetTransform =
            "var originalTransform = arguments[0].style.transform;" +
                    "arguments[0].style.transform = '%s';" +
                    "return originalTransform;";

    private Location lastSetPosition = Location.ZERO; // cache.

    public CssTranslatePositionProvider(Logger logger, IEyesJsExecutor executor, WebElement scrollRootElement) {
        ArgumentGuard.notNull(logger, "logger");
        ArgumentGuard.notNull(executor, "executor");
        ArgumentGuard.notNull(scrollRootElement, "scrollRootElement");

        this.logger = logger;
        this.executor = executor;
        this.scrollRootElement = scrollRootElement;
    }

    public Location getCurrentPosition() {
        return lastSetPosition;
    }

    public Location setPosition(Location location) {
        ArgumentGuard.notNull(location, "location");
        Location negatedLocation = new Location(-location.getX(), -location.getY());
        Location negatedLocation2 = new Location(10, -location.getY());
        //EyesSeleniumUtils.translateTo(executor, location);
        executor.executeScript(
                String.format("arguments[0].style.transform='translate(%dpx,%dpx)';",
                        negatedLocation2.getX(), negatedLocation2.getY()),
                this.scrollRootElement);
        executor.executeScript(
                String.format("arguments[0].style.transform='translate(%dpx,%dpx)';",
                        negatedLocation.getX(), negatedLocation.getY()),
                this.scrollRootElement);
        lastSetPosition = location;
        return lastSetPosition;
    }

    public RectangleSize getEntireSize() {
        return EyesDriverUtils.getEntireElementSize(logger, executor, scrollRootElement);
    }

    public PositionMemento getState() {
        return new CssTranslatePositionMemento(
                (String)executor.executeScript("return arguments[0].style.transform;", this.scrollRootElement),
                lastSetPosition);
    }

    public void restoreState(PositionMemento state) {
        executor.executeScript(
                String.format(JSSetTransform, ((CssTranslatePositionMemento)state).getTransform()),
                this.scrollRootElement);
        lastSetPosition = ((CssTranslatePositionMemento)state).getPosition();
    }

    @Override
    public WebElement getScrolledElement() {
        return scrollRootElement;
    }
}
