package com.applitools.eyes.selenium.fluent;

import com.applitools.eyes.*;
import com.applitools.eyes.fluent.IGetAccessibilityRegionType;
import com.applitools.eyes.selenium.rendering.IGetSeleniumRegion;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import java.util.Collections;
import java.util.List;

public class AccessibilityRegionByElement implements GetAccessibilityRegion, IGetSeleniumRegion, IGetAccessibilityRegionType {

    private final AccessibilityRegionType regionType;
    private final WebElement element;

    public AccessibilityRegionByElement(WebElement element, AccessibilityRegionType regionType) {
        this.element = element;
        this.regionType = regionType;
    }

    @Override
    public List<AccessibilityRegionByRectangle> getRegions(EyesScreenshot screenshot) {
        Point p = element.getLocation();
        Location pTag = screenshot.convertLocation(new Location(p.x, p.y), CoordinatesType.CONTEXT_RELATIVE, CoordinatesType.SCREENSHOT_AS_IS);
        return Collections.singletonList(new AccessibilityRegionByRectangle(new Region(pTag, new RectangleSize(element.getSize().width, element.getSize().height)), regionType));
    }


    @Override
    public AccessibilityRegionType getAccessibilityRegionType() {
        return regionType;
    }

    @Override
    public List<WebElement> getElements() {
        return Collections.singletonList(element);
    }

}
