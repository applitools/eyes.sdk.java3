package com.applitools.eyes.selenium.fluent;

import com.applitools.eyes.EyesBase;
import com.applitools.eyes.FloatingMatchSettings;
import com.applitools.eyes.fluent.GetFloatingRegion;
import com.applitools.eyes.selenium.Eyes;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class FloatingRegionBySelector implements GetFloatingRegion{

    private By selector;
    private int maxUpOffset;
    private int maxDownOffset;
    private int maxLeftOffset;
    private int maxRightOffset;

    public FloatingRegionBySelector(By regionSelector, int maxUpOffset, int maxDownOffset, int maxLeftOffset, int maxRightOffset) {

        this.selector = regionSelector;
        this.maxUpOffset = maxUpOffset;
        this.maxDownOffset = maxDownOffset;
        this.maxLeftOffset = maxLeftOffset;
        this.maxRightOffset = maxRightOffset;
    }

    @Override
    public FloatingMatchSettings getRegion(EyesBase eyesBase) {
        WebElement element = ((Eyes)eyesBase).getDriver().findElement(this.selector);
        return new FloatingMatchSettings(
                element.getLocation().getX(),
                element.getLocation().getY(),
                element.getSize().getWidth(),
                element.getSize().getHeight(),
                maxUpOffset, maxDownOffset, maxLeftOffset, maxRightOffset);
    }
}
