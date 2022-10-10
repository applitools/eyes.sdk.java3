package com.applitools.eyes.selenium.fluent;

import com.applitools.eyes.*;
import com.applitools.eyes.fluent.GetSimpleRegion;
import com.applitools.eyes.Borders;
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
//    private final Borders padding;
    private final Padding padding;

    private String regionId;

    public SimpleRegionBySelector(By selector) {
        this(selector, new Padding());
    }

    public SimpleRegionBySelector(By selector, Padding padding) {
        this.selector = selector;
        this.padding = padding;
    }

    @Override
    public void init(Logger logger, EyesWebDriver driver) {
        this.driver = driver;
        new SimpleRegionBySelector(By.cssSelector("asd"), null);
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
            region = region.addPadding(padding);
            values.add(region);
        }
        return values;
    }

    @Override
    public List<WebElement> getElements() {
        return driver.findElements(selector);
    }

    public By getSelector() {
        return selector;
    }

    public SimpleRegionBySelector regionId(String regionId) {
        this.regionId = regionId;
        return this;
    }

    public String getRegionId() {
        return regionId;
    }

    public Padding getPadding() { return this.padding; }
}
