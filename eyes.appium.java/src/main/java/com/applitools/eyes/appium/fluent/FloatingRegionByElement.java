package com.applitools.eyes.appium.fluent;

import com.applitools.eyes.EyesScreenshot;
import com.applitools.eyes.FloatingMatchSettings;
import com.applitools.eyes.appium.Eyes;
import com.applitools.eyes.appium.EyesAppiumElement;
import com.applitools.eyes.selenium.fluent.IGetSeleniumRegion;
import com.applitools.eyes.visualgrid.model.IGetFloatingRegionOffsets;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FloatingRegionByElement implements IGetSeleniumRegion, IGetFloatingRegionOffsets {

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

    public List<FloatingMatchSettings> getRegions(Eyes eyes) {
        EyesAppiumElement eyesAppiumElement = new EyesAppiumElement((eyes.getEyesDriver(),
                element, 1/eyes.getDevicePixelRatio());

        Point locationAsPoint = eyesAppiumElement.getLocation();
        Dimension size = eyesAppiumElement.getSize();

        List<FloatingMatchSettings> value = new ArrayList<>();

        value.add(new FloatingMatchSettings(locationAsPoint.getX(), locationAsPoint.getY(), size.getWidth(),
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
