package com.applitools.eyes.selenium.fluent;

import com.applitools.eyes.*;
import com.applitools.eyes.fluent.IGetAccessibilityRegionType;
import com.applitools.eyes.selenium.EyesDriverUtils;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import com.applitools.eyes.serializers.BySerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class AccessibilityRegionBySelector implements GetAccessibilityRegion, IGetSeleniumRegion, IGetAccessibilityRegionType, ImplicitInitiation {

    @JsonIgnore
    private EyesWebDriver driver;
    private final AccessibilityRegionType regionType;
    @JsonSerialize(using = BySerializer.class)
    private final By selector;

    public AccessibilityRegionBySelector(By selector, AccessibilityRegionType regionType) {
        this.selector = selector;
        this.regionType = regionType;
        //selector.criteria
    }

    @Override
    public void init(Logger logger, EyesWebDriver driver) {
        this.driver = driver;
    }

    @Override
    public List<AccessibilityRegionByRectangle> getRegions(EyesScreenshot screenshot) {
        List<WebElement> elements = driver.findElements(selector);
        List<AccessibilityRegionByRectangle> retVal = new ArrayList<>();
        for (WebElement element : elements) {
            Rectangle rectangle = EyesDriverUtils.getVisibleElementRect(element, driver);
            Dimension size = element.getSize();
            Location pTag = screenshot.convertLocation(new Location(rectangle.x, rectangle.y), CoordinatesType.CONTEXT_RELATIVE, CoordinatesType.SCREENSHOT_AS_IS);
            retVal.add(new AccessibilityRegionByRectangle(new Region(pTag, new RectangleSize(size.width, size.height)), regionType));
        }
        return retVal;
    }

    @Override
    public AccessibilityRegionType getAccessibilityRegionType() {
        return this.regionType;
    }

    @Override
    public List<WebElement> getElements() {
        return driver.findElements(selector);
    }

    public By getSelector() {
        return selector;
    }
}
