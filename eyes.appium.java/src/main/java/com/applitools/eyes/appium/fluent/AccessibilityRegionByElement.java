package com.applitools.eyes.appium.fluent;

import com.applitools.eyes.*;
import com.applitools.eyes.appium.Eyes;
import com.applitools.eyes.appium.EyesAppiumElement;
import com.applitools.eyes.fluent.IGetAccessibilityRegionType;
import com.applitools.eyes.selenium.fluent.IGetSeleniumRegion;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import java.util.Collections;
import java.util.List;

public class AccessibilityRegionByElement implements IGetSeleniumRegion, IGetAccessibilityRegionType {

    protected final AccessibilityRegionType regionType;
    protected final WebElement element;

    public AccessibilityRegionByElement(WebElement element, AccessibilityRegionType regionType) {
        this.element = element;
        this.regionType = regionType;
    }


    public List<AccessibilityRegionByRectangle> getRegions(Eyes eyes, EyesScreenshot screenshot) {
        EyesAppiumElement eyesAppiumElement = new EyesAppiumElement((eyes.getEyesDriver(), element, 1/eyes.getDevicePixelRatio());

        Point p = eyesAppiumElement.getLocation();
        Dimension size = eyesAppiumElement.getSize();
        Location pTag = screenshot.convertLocation(new Location(p.x, p.y), CoordinatesType.CONTEXT_RELATIVE, CoordinatesType.SCREENSHOT_AS_IS);

        return Collections.singletonList(new AccessibilityRegionByRectangle(
                new Region(pTag, new RectangleSize(size.width, size.height)), regionType));
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
