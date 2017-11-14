package com.applitools.eyes.selenium.fluent;

import com.applitools.eyes.Region;
import com.applitools.eyes.fluent.CheckSettings;
import com.applitools.eyes.fluent.FloatingRegionByRectangle;
import com.applitools.eyes.fluent.IgnoreRegionByRectangle;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class SeleniumCheckSettings extends CheckSettings implements ISeleniumCheckTarget {

    private By targetSelector;
    private WebElement targetElement;
    private List<FrameLocator> frameChain = new ArrayList<>();

    SeleniumCheckSettings() { }

    SeleniumCheckSettings(Region region) {
        super(region);
    }

    SeleniumCheckSettings(By targetSelector) {
        this.targetSelector = targetSelector;
    }

    SeleniumCheckSettings(WebElement targetElement) {
        this.targetElement = targetElement;
    }

    @Override
    public By getTargetSelector() {
        return this.targetSelector;
    }

    @Override
    public WebElement getTargetElement(){
        return this.targetElement;
    }

    @Override
    public List<FrameLocator> getFrameChain() {
        return this.frameChain;
    }

    public SeleniumCheckSettings frame(By by) {
        FrameLocator fl = new FrameLocator();
        fl.setFrameSelector(by);
        this.frameChain.add(fl);
        return this;
    }

    public SeleniumCheckSettings frame(String frameNameOrId) {
        FrameLocator fl = new FrameLocator();
        fl.setFrameNameOrId(frameNameOrId);
        this.frameChain.add(fl);
        return this;
    }

    public SeleniumCheckSettings frame(int index) {
        FrameLocator fl = new FrameLocator();
        fl.setFrameIndex(index);
        this.frameChain.add(fl);
        return this;
    }

    public SeleniumCheckSettings region(Region region) {
        super.updateTargetRegion(region);
        return this;
    }

    public SeleniumCheckSettings region(By by) {
        this.targetSelector = by;
        return this;
    }

    public SeleniumCheckSettings ignore(By... regionSelectors) {
        for (By selector : regionSelectors) {
            ignore(new IgnoreRegionBySelector(selector));
        }

        return this;
    }

    public SeleniumCheckSettings ignore(WebElement... elements) {
        for (WebElement element : elements) {
            Point loc = element.getLocation();
            Dimension dim = element.getSize();
            Region region = new Region(loc.getX(), loc.getY(), dim.getWidth(), dim.getHeight());
            ignore(new IgnoreRegionByRectangle(region));
        }

        return this;
    }

    public SeleniumCheckSettings floating(By regionSelector, int maxUpOffset, int maxDownOffset, int maxLeftOffset, int maxRightOffset) {
        floating(new FloatingRegionBySelector(regionSelector, maxUpOffset, maxDownOffset, maxLeftOffset, maxRightOffset));
        return this;
    }

    public SeleniumCheckSettings floating(WebElement element, int maxUpOffset, int maxDownOffset, int maxLeftOffset, int maxRightOffset) {
        Point loc = element.getLocation();
        Dimension dim = element.getSize();
        Region region = new Region(loc.getX(), loc.getY(), dim.getWidth(), dim.getHeight());
        floating(new FloatingRegionByRectangle(region, maxUpOffset, maxDownOffset, maxLeftOffset, maxRightOffset));
        return this;
    }
}
