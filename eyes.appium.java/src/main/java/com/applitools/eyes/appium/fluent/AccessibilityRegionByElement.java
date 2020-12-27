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

import java.util.Collections;
import java.util.List;

public class AccessibilityRegionByElement extends com.applitools.eyes.selenium.fluent.AccessibilityRegionByElement implements ImplicitInitiation {

    @JsonIgnore
    private EyesAppiumDriver driver;

    public AccessibilityRegionByElement(WebElement element, AccessibilityRegionType regionType) {
        super(element, regionType);
    }

    @Override
    public void init(Logger logger, EyesWebDriver driver) {
        this.driver = (EyesAppiumDriver) driver;
    }

    @Override
    public List<AccessibilityRegionByRectangle> getRegions(EyesScreenshot screenshot) {
        EyesAppiumElement eyesAppiumElement = new EyesAppiumElement(driver, element, 1 / driver.getDevicePixelRatio());

        Point p = eyesAppiumElement.getLocation();
        Dimension size = eyesAppiumElement.getSize();
        Location pTag = screenshot.convertLocation(new Location(p.x, p.y), CoordinatesType.CONTEXT_RELATIVE, CoordinatesType.SCREENSHOT_AS_IS);

        return Collections.singletonList(new AccessibilityRegionByRectangle(
                new Region(pTag, new RectangleSize(size.width, size.height)), regionType));
    }
}
