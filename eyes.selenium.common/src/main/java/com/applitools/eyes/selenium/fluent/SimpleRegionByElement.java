package com.applitools.eyes.selenium.fluent;

import com.applitools.eyes.*;
import com.applitools.eyes.fluent.GetSimpleRegion;
import com.applitools.eyes.selenium.EyesDriverUtils;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import com.applitools.eyes.serializers.WebElementSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleRegionByElement implements GetSimpleRegion, IGetSeleniumRegion, ImplicitInitiation {

    @JsonIgnore
    private EyesWebDriver driver;
    @JsonSerialize(using = WebElementSerializer.class)
    protected final WebElement element;

    public SimpleRegionByElement(WebElement element) {
        this.element = element;
    }

    @Override
    public void init(Logger logger, EyesWebDriver driver) {
        this.driver = driver;
    }

    @Override
    public List<Region> getRegions(EyesScreenshot screenshot) {
        Rectangle rectangle = EyesDriverUtils.getVisibleElementRect(element, driver);
        Dimension size = element.getSize();
        Location adjustedLocation = new Location(rectangle.x, rectangle.y);
        if (screenshot != null) {
            // Element's coordinates are context relative, so we need to convert them first.
            adjustedLocation = screenshot.convertLocation(adjustedLocation,
                    CoordinatesType.CONTEXT_RELATIVE, CoordinatesType.SCREENSHOT_AS_IS);
        }

        List<Region> value = new ArrayList<>();
        value.add(new Region(adjustedLocation, new RectangleSize(size.width, size.height),
                CoordinatesType.SCREENSHOT_AS_IS));

        return value;
    }

    @JsonProperty("element")
    public WebElement getElement() {
        return element;
    }

    @Override
    public List<WebElement> getElements() {
        return Collections.singletonList(element);
    }
}
