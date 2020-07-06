package com.applitools.eyes.selenium.fluent;

import com.applitools.eyes.*;
import com.applitools.eyes.fluent.IGetAccessibilityRegionType;
import com.applitools.eyes.selenium.rendering.IGetSeleniumRegion;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class AccessibilityRegionBySelector implements GetAccessibilityRegion, IGetSeleniumRegion, IGetAccessibilityRegionType, ImplicitInitiation {

    private Logger logger;
    private WebDriver driver;
    private final AccessibilityRegionType regionType;
    private final By selector;

    public AccessibilityRegionBySelector(By selector, AccessibilityRegionType regionType) {
        this.selector = selector;
        this.regionType = regionType;
    }

    @Override
    public void init(Logger logger, WebDriver driver) {
        this.logger = logger;
        this.driver = driver;
    }

    @Override
    public List<AccessibilityRegionByRectangle> getRegions(EyesScreenshot screenshot) {
        List<WebElement> elements = driver.findElements(selector);
        List<AccessibilityRegionByRectangle> retVal = new ArrayList<>();
        for (WebElement element : elements) {
            Point p = element.getLocation();
            Location pTag = screenshot.convertLocation(new Location(p.x, p.y), CoordinatesType.CONTEXT_RELATIVE, CoordinatesType.SCREENSHOT_AS_IS);
            retVal.add(new AccessibilityRegionByRectangle(new Region(pTag, new RectangleSize(element.getSize().width, element.getSize().height)), regionType));
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
}
