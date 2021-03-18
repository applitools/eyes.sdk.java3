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
    @JsonIgnore
    private final Rectangle padding;

    public SimpleRegionBySelector(By selector) {
        this(selector, new Rectangle(0, 0, 0, 0));
    }

    public SimpleRegionBySelector(By selector, Rectangle padding) {
        this.selector = selector;
        this.padding = padding;
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
            Dimension size = element.getSize();
            Location adjustedLocation = new Location(rectangle.x, rectangle.y);
            if (screenshot != null) {
                // Element's coordinates are context relative, so we need to convert them first.
                adjustedLocation = screenshot.convertLocation(adjustedLocation,
                        CoordinatesType.CONTEXT_RELATIVE, CoordinatesType.SCREENSHOT_AS_IS);
            }
            Region region = new Region(adjustedLocation, new RectangleSize(size.width, size.height),
                    CoordinatesType.SCREENSHOT_AS_IS);
            region = region.addPadding(padding.x, padding.y, padding.width, padding.height);
            values.add(region);
        }
        return values;
    }

    @Override
    public List<WebElement> getElements() {
        return driver.findElements(selector);
    }
}
