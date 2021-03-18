package com.applitools.eyes.appium.fluent;

import com.applitools.eyes.*;
import com.applitools.eyes.appium.EyesAppiumDriver;
import com.applitools.eyes.appium.EyesAppiumElement;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import com.applitools.eyes.selenium.fluent.ImplicitInitiation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class SimpleRegionByElement extends com.applitools.eyes.selenium.fluent.SimpleRegionByElement implements ImplicitInitiation {

    @JsonIgnore
    private EyesAppiumDriver driver;

    public SimpleRegionByElement(WebElement element) {
        super(element);
    }

    @Override
    public void init(Logger logger, EyesWebDriver driver) {
        this.driver = (EyesAppiumDriver) driver;
    }

    @Override
    public List<Region> getRegions(EyesScreenshot screenshot) {
        EyesAppiumElement eyesAppiumElement = new EyesAppiumElement(driver, element, 1 / driver.getDevicePixelRatio());
        Point locationAsPoint = eyesAppiumElement.getLocation();
        Dimension size = eyesAppiumElement.getSize();

        List<Region> value = new ArrayList<>();
        Region region = new Region(new Location(locationAsPoint.getX(), locationAsPoint.getY()), new RectangleSize(size.getWidth(), size.getHeight()),
                CoordinatesType.SCREENSHOT_AS_IS);
        region = region.addPadding(padding.x, padding.y, padding.width, padding.height);
        value.add(region);
        return value;
    }
}
