package com.applitools.eyes.selenium.fluent;

import com.applitools.eyes.*;
import com.applitools.eyes.fluent.GetFloatingRegion;
import com.applitools.eyes.selenium.EyesDriverUtils;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import com.applitools.eyes.serializers.WebElementSerializer;
import com.applitools.eyes.visualgrid.model.IGetFloatingRegionOffsets;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FloatingRegionByElement implements GetFloatingRegion, IGetSeleniumRegion, IGetFloatingRegionOffsets, ImplicitInitiation {

    @JsonIgnore
    private EyesWebDriver driver;
    @JsonSerialize(using = WebElementSerializer.class)
    protected final WebElement element;
    protected final int maxUpOffset;
    protected final int maxDownOffset;
    protected final int maxLeftOffset;
    protected final int maxRightOffset;

    public FloatingRegionByElement(WebElement element, int maxUpOffset, int maxDownOffset, int maxLeftOffset, int maxRightOffset) {

        this.element = element;
        this.maxUpOffset = maxUpOffset;
        this.maxDownOffset = maxDownOffset;
        this.maxLeftOffset = maxLeftOffset;
        this.maxRightOffset = maxRightOffset;
    }

    @Override
    public void init(Logger logger, EyesWebDriver driver) {
        this.driver = driver;
    }

    @Override
    public List<FloatingMatchSettings> getRegions(EyesScreenshot screenshot) {
        Rectangle rectangle = EyesDriverUtils.getVisibleElementRect(element, driver);
        Location location = new Location(rectangle.x, rectangle.y);
        Dimension size = element.getSize();

        Location adjustedLocation;
        if (screenshot != null) {
            // Element's coordinates are context relative, so we need to convert them first.
            adjustedLocation = screenshot.getLocationInScreenshot(location, CoordinatesType.CONTEXT_RELATIVE);
        } else {
            adjustedLocation = location;
        }

        List<FloatingMatchSettings> value = new ArrayList<>();

        value.add(new FloatingMatchSettings(adjustedLocation.getX(), adjustedLocation.getY(), size.width,
                size.height, maxUpOffset, maxDownOffset, maxLeftOffset, maxRightOffset));

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

    @Override
    public int getMaxLeftOffset() {
        return maxLeftOffset;
    }

    @Override
    public int getMaxUpOffset() {
        return maxUpOffset;
    }

    @Override
    public int getMaxRightOffset() {
        return maxRightOffset;
    }

    @Override
    public int getMaxDownOffset() {
        return maxDownOffset;
    }
}
