package com.applitools.eyes.selenium.fluent;

import com.applitools.eyes.*;
import com.applitools.eyes.fluent.GetSimpleRegion;
import com.applitools.eyes.selenium.rendering.IGetSeleniumRegion;
import com.applitools.eyes.selenium.wrappers.EyesRemoteWebElement;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleRegionByElement implements GetSimpleRegion, IGetSeleniumRegion, ImplicitInitiation {

    private Logger logger;
    private WebDriver driver;
    private WebElement element;

    public SimpleRegionByElement(WebElement element) {
        this.element = element;
    }

    @Override
    public void init(Logger logger, WebDriver driver) {
        this.logger = logger;
        this.driver = driver;
    }

    @Override
    public List<Region> getRegions(EyesScreenshot screenshot) {
        if (!(element instanceof EyesRemoteWebElement)) {
            element = new EyesRemoteWebElement(logger, (EyesWebDriver) driver, element);
        }

        Point locationAsPoint = element.getLocation();
        Dimension size = element.getSize();

        Location adjustedLocation = new Location(locationAsPoint.getX(), locationAsPoint.getY());
        if (screenshot != null) {
            // Element's coordinates are context relative, so we need to convert them first.
            adjustedLocation = screenshot.convertLocation(adjustedLocation,
                    CoordinatesType.CONTEXT_RELATIVE, CoordinatesType.SCREENSHOT_AS_IS);
        }

        List<Region> value = new ArrayList<>();
        value.add(new Region(adjustedLocation, new RectangleSize(size.getWidth(), size.getHeight()),
                CoordinatesType.SCREENSHOT_AS_IS));

        return value;
    }

    @Override
    public List<WebElement> getElements() {
        return Collections.singletonList(element);
    }
}
