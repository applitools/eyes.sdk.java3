package com.applitools.eyes.selenium.fluent;

import com.applitools.eyes.*;
import com.applitools.eyes.fluent.GetSimpleRegion;
import com.applitools.eyes.selenium.EyesDriverUtils;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import com.applitools.eyes.serializers.BySerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.List;

public class SimpleRegionBySelector implements GetSimpleRegion, IGetSeleniumRegion, ImplicitInitiation {

    @JsonIgnore
    private EyesWebDriver driver;
    @JsonSerialize(using = BySerializer.class)
    private final By selector;

    public SimpleRegionBySelector(By selector) {
        this.selector = selector;
    }

    @Override
    public void init(Logger logger, EyesWebDriver driver) {
        this.driver = driver;
    }

    @Override
    public List<Region> getRegions(EyesScreenshot screenshot) {
        List<WebElement> elements = driver.findElements(this.selector);
        List<Region> values = new ArrayList<>(elements.size());
        for (WebElement element : elements) {
            Rectangle rectangle = EyesDriverUtils.getVisibleElementRect(element, driver);
            Location adjustedLocation = new Location(rectangle.x, rectangle.y);
            if (screenshot != null) {
                // Element's coordinates are context relative, so we need to convert them first.
                adjustedLocation = screenshot.convertLocation(adjustedLocation,
                        CoordinatesType.CONTEXT_RELATIVE, CoordinatesType.SCREENSHOT_AS_IS);
            }
            values.add(new Region(adjustedLocation, new RectangleSize(rectangle.width, rectangle.height),
                    CoordinatesType.SCREENSHOT_AS_IS));
        }
        return values;
    }

    @Override
    public List<WebElement> getElements() {
        return driver.findElements(selector);
    }
}
