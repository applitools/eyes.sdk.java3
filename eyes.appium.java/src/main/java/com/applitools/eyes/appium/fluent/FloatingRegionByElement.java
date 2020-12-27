package com.applitools.eyes.appium.fluent;

import com.applitools.eyes.EyesScreenshot;
import com.applitools.eyes.FloatingMatchSettings;
import com.applitools.eyes.Logger;
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

public class FloatingRegionByElement extends com.applitools.eyes.selenium.fluent.FloatingRegionByElement implements ImplicitInitiation {

    @JsonIgnore
    private EyesAppiumDriver driver;

    public FloatingRegionByElement(WebElement element, int maxUpOffset, int maxDownOffset, int maxLeftOffset, int maxRightOffset) {
        super(element, maxUpOffset, maxDownOffset, maxLeftOffset, maxRightOffset);
    }

    @Override
    public void init(Logger logger, EyesWebDriver driver) {
        this.driver = (EyesAppiumDriver) driver;
    }

    @Override
    public List<FloatingMatchSettings> getRegions(EyesScreenshot screenshot) {
        EyesAppiumElement eyesAppiumElement = new EyesAppiumElement(driver, element, 1 / driver.getDevicePixelRatio());
        Point locationAsPoint = eyesAppiumElement.getLocation();
        Dimension size = eyesAppiumElement.getSize();
        List<FloatingMatchSettings> value = new ArrayList<>();
        value.add(new FloatingMatchSettings(locationAsPoint.getX(), locationAsPoint.getY(), size.getWidth(),
                size.getHeight(), maxUpOffset, maxDownOffset, maxLeftOffset, maxRightOffset));

        return value;
    }
}
