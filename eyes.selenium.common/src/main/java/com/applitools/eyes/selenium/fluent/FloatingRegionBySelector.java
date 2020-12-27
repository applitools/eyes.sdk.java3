package com.applitools.eyes.selenium.fluent;

import com.applitools.eyes.*;
import com.applitools.eyes.fluent.GetFloatingRegion;
import com.applitools.eyes.selenium.EyesDriverUtils;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import com.applitools.eyes.serializers.BySerializer;
import com.applitools.eyes.serializers.WebElementSerializer;
import com.applitools.eyes.visualgrid.model.IGetFloatingRegionOffsets;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.List;

public class FloatingRegionBySelector implements GetFloatingRegion , IGetSeleniumRegion, IGetFloatingRegionOffsets, ImplicitInitiation {

    @JsonIgnore
    private Logger logger;
    @JsonIgnore
    private EyesWebDriver driver;
    @JsonSerialize(using = BySerializer.class)
    private final By selector;
    private final int maxUpOffset;
    private final int maxDownOffset;
    private final int maxLeftOffset;
    private final int maxRightOffset;

    public FloatingRegionBySelector(By regionSelector, int maxUpOffset, int maxDownOffset, int maxLeftOffset, int maxRightOffset) {
        this.selector = regionSelector;
        this.maxUpOffset = maxUpOffset;
        this.maxDownOffset = maxDownOffset;
        this.maxLeftOffset = maxLeftOffset;
        this.maxRightOffset = maxRightOffset;
    }

    @Override
    public void init(Logger logger, EyesWebDriver driver) {
        this.logger = logger;
        this.driver = driver;
    }

    @Override
    public List<FloatingMatchSettings> getRegions(EyesScreenshot screenshot) {
        List<WebElement> elements = driver.findElements(this.selector);
        List<FloatingMatchSettings> values = new ArrayList<>();

        for (WebElement element : elements) {
            Point locationAsPoint = element.getLocation();
            RectangleSize size = EyesDriverUtils.getElementVisibleSize(logger, element);

            Location adjustedLocation;
            if (screenshot != null) {
                // Element's coordinates are context relative, so we need to convert them first.
                adjustedLocation = screenshot.getLocationInScreenshot(new Location(locationAsPoint.getX(), locationAsPoint.getY()),
                        CoordinatesType.CONTEXT_RELATIVE);
            } else {
                adjustedLocation = new Location(locationAsPoint.getX(), locationAsPoint.getY());
            }

            values.add(new FloatingMatchSettings(adjustedLocation.getX(), adjustedLocation.getY(), size.getWidth(),
                    size.getHeight(), maxUpOffset, maxDownOffset, maxLeftOffset, maxRightOffset));
        }

        return values;
    }

    @Override
    public List<WebElement> getElements() {
        return driver.findElements(this.selector);
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
