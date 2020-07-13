package com.applitools.eyes.appium.fluent;

import com.applitools.eyes.*;
import com.applitools.eyes.appium.Eyes;
import com.applitools.eyes.appium.EyesAppiumElement;
import com.applitools.eyes.selenium.fluent.IGetSeleniumRegion;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleRegionByElement implements IGetSeleniumRegion {

    private final WebElement element;

    public SimpleRegionByElement(WebElement element) {
        this.element = element;
    }

    public List<Region> getRegions(Eyes eyes) {
        EyesAppiumElement eyesAppiumElement = new EyesAppiumElement((eyes.getEyesDriver(),
                element, 1/eyes.getDevicePixelRatio());

        Point locationAsPoint = eyesAppiumElement.getLocation();
        Dimension size = eyesAppiumElement.getSize();

        List<Region> value = new ArrayList<>();
        value.add(new Region(new Location(locationAsPoint.getX(), locationAsPoint.getY()), new RectangleSize(size.getWidth(), size.getHeight()),
                CoordinatesType.SCREENSHOT_AS_IS));

        return value;
    }

    @Override
    public List<WebElement> getElements() {
        return Collections.singletonList(element);
    }
}
