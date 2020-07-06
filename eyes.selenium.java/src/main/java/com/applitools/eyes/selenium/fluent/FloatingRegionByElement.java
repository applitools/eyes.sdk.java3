package com.applitools.eyes.selenium.fluent;

import com.applitools.eyes.CoordinatesType;
import com.applitools.eyes.EyesScreenshot;
import com.applitools.eyes.FloatingMatchSettings;
import com.applitools.eyes.Location;
import com.applitools.eyes.fluent.GetFloatingRegion;
import com.applitools.eyes.selenium.rendering.IGetSeleniumRegion;
import com.applitools.eyes.visualgrid.model.IGetFloatingRegionOffsets;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FloatingRegionByElement implements GetFloatingRegion, IGetSeleniumRegion, IGetFloatingRegionOffsets {

    private final WebElement element;
    private final int maxUpOffset;
    private final int maxDownOffset;
    private final int maxLeftOffset;
    private final int maxRightOffset;

    public FloatingRegionByElement(WebElement element, int maxUpOffset, int maxDownOffset, int maxLeftOffset, int maxRightOffset) {

        this.element = element;
        this.maxUpOffset = maxUpOffset;
        this.maxDownOffset = maxDownOffset;
        this.maxLeftOffset = maxLeftOffset;
        this.maxRightOffset = maxRightOffset;
    }

    @Override
    public List<FloatingMatchSettings> getRegions(EyesScreenshot screenshot) {
        Point locationAsPoint = element.getLocation();
        Dimension size = element.getSize();

        Location adjustedLocation;
        if (screenshot != null) {
            // Element's coordinates are context relative, so we need to convert them first.
            adjustedLocation = screenshot.getLocationInScreenshot(new Location(locationAsPoint.getX(), locationAsPoint.getY()),
                    CoordinatesType.CONTEXT_RELATIVE);
        } else {
            adjustedLocation = new Location(locationAsPoint.getX(), locationAsPoint.getY());
        }

        List<FloatingMatchSettings> value = new ArrayList<>();

        value.add(new FloatingMatchSettings(adjustedLocation.getX(), adjustedLocation.getY(), size.getWidth(),
                size.getHeight(), maxUpOffset, maxDownOffset, maxLeftOffset, maxRightOffset));

        return value;
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
