package com.applitools.eyes.selenium.positioning;

import com.applitools.eyes.IEyesJsExecutor;
import com.applitools.eyes.Location;
import com.applitools.eyes.Logger;
import com.applitools.eyes.selenium.EyesDriverUtils;
import org.openqa.selenium.WebElement;

public class EdgeBrowserScrollPositionProvider extends SeleniumScrollPositionProvider {

    public EdgeBrowserScrollPositionProvider(Logger logger, IEyesJsExecutor executor, WebElement scrollRootElement) {
        super(logger, executor, scrollRootElement);
    }

    @Override
    public Location setPosition(Location location) {
        Object position = executor.executeScript(String.format("window.scrollTo(%d,%d);" +
                        "return (window.scrollX+';'+window.scrollY);",
                location.getX(), location.getY()),
                scrollRootElement);
        return EyesDriverUtils.parseLocationString(position);
    }

    @Override
    public Location getCurrentPosition() {
        Object position = executor.executeScript("return (window.scrollX+';'+window.scrollY);", scrollRootElement);
        return EyesDriverUtils.parseLocationString(position);
    }
}
