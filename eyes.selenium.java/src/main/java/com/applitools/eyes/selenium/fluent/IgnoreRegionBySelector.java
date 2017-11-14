package com.applitools.eyes.selenium.fluent;

import com.applitools.eyes.EyesBase;
import com.applitools.eyes.Region;
import com.applitools.eyes.fluent.GetRegion;
import com.applitools.eyes.selenium.Eyes;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class IgnoreRegionBySelector implements GetRegion {
    private By selector;

    public IgnoreRegionBySelector(By selector) {
        this.selector = selector;
    }

    @Override
    public Region getRegion(EyesBase eyesBase) {
        WebElement element = ((Eyes)eyesBase).getDriver().findElement(this.selector);
        return new Region(
                element.getLocation().getX(),
                element.getLocation().getY(),
                element.getSize().getWidth(),
                element.getSize().getHeight());
    }
}
