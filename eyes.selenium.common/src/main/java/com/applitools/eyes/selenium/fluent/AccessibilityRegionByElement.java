package com.applitools.eyes.selenium.fluent;

import com.applitools.eyes.*;
import com.applitools.eyes.fluent.IGetAccessibilityRegionType;
import com.applitools.eyes.selenium.EyesDriverUtils;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import com.applitools.eyes.serializers.WebElementSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;

import java.util.Collections;
import java.util.List;

public class AccessibilityRegionByElement implements GetAccessibilityRegion, IGetSeleniumRegion, IGetAccessibilityRegionType, ImplicitInitiation {

    @JsonIgnore
    private EyesWebDriver driver;
    protected final AccessibilityRegionType regionType;
    @JsonSerialize(using = WebElementSerializer.class)
    protected final WebElement element;

    public AccessibilityRegionByElement(WebElement element, AccessibilityRegionType regionType) {
        this.element = element;
        this.regionType = regionType;
    }

    @Override
    public void init(Logger logger, EyesWebDriver driver) {
        this.driver = driver;
    }

    @Override
    public List<AccessibilityRegionByRectangle> getRegions(EyesScreenshot screenshot) {
        Rectangle rectangle = EyesDriverUtils.getVisibleElementRect(element, driver);
        Dimension size = element.getSize();
        Location pTag = screenshot.convertLocation(new Location(rectangle.x, rectangle.y), CoordinatesType.CONTEXT_RELATIVE, CoordinatesType.SCREENSHOT_AS_IS);
        return Collections.singletonList(new AccessibilityRegionByRectangle(new Region(pTag, new RectangleSize(size.width, size.height)), regionType));
    }


    @Override
    public AccessibilityRegionType getAccessibilityRegionType() {
        return regionType;
    }


    @JsonProperty("element")
    public WebElement getElement() {
        return element;
    }

    @Override
    public List<WebElement> getElements() {
        return Collections.singletonList(element);
    }

}
