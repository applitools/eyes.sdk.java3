package com.applitools.eyes.appium;

import com.applitools.eyes.AccessibilityRegionType;
import com.applitools.eyes.Logger;
import com.applitools.eyes.Region;
import com.applitools.eyes.appium.fluent.AccessibilityRegionByElement;
import com.applitools.eyes.appium.fluent.FloatingRegionByElement;
import com.applitools.eyes.appium.fluent.SimpleRegionByElement;
import com.applitools.eyes.fluent.CheckSettings;
import com.applitools.eyes.fluent.GetRegion;
import com.applitools.eyes.selenium.EyesWebDriver;
import com.applitools.eyes.selenium.fluent.AccessibilityRegionBySelector;
import com.applitools.eyes.selenium.fluent.ImplicitInitiation;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import java.util.List;

public class AppiumCheckSettings extends CheckSettings implements ImplicitInitiation {

    private By targetSelector;
    private WebElement targetElement;
    private By cutElementSelector;
    private ElementType cutElementType;
    private Boolean statusBarExists;

    protected AppiumCheckSettings() {
        super();
    }

    protected AppiumCheckSettings(Region region) {
        super(region);
    }

    protected AppiumCheckSettings(By selector) {
        this.targetSelector = selector;
    }

    protected AppiumCheckSettings(WebElement webElement) {
        this.targetElement = webElement;
    }

    @Override
    public void init(Logger logger, EyesWebDriver driver) {
        initGetRegions(logger, driver, ignoreRegions);
        initGetRegions(logger, driver, layoutRegions);
        initGetRegions(logger, driver, strictRegions);
        initGetRegions(logger, driver, contentRegions);
        initGetRegions(logger, driver, floatingRegions);
        initGetRegions(logger, driver, accessibilityRegions);
    }

    private void initGetRegions(Logger logger, EyesWebDriver driver, List<? extends GetRegion> getRegions) {
        for (GetRegion getRegion : getRegions) {
            if (getRegion instanceof ImplicitInitiation) {
                ((ImplicitInitiation) getRegion).init(logger, driver);
            }
        }
    }

    @Override
    public AppiumCheckSettings clone() {
        AppiumCheckSettings clone = new AppiumCheckSettings();
        super.populateClone(clone);
        clone.targetSelector = this.targetSelector;
        clone.targetElement = this.targetElement;
        clone.cutElementSelector = this.cutElementSelector;
        clone.cutElementType = this.cutElementType;
        return clone;
    }

    public AppiumCheckSettings layout(WebElement[] elements) {
        AppiumCheckSettings clone = this.clone();
        for (WebElement e : elements) {
            clone.layout_(new SimpleRegionByElement(e));
        }
        return clone;
    }

    public AppiumCheckSettings layout(WebElement element, WebElement... elements) {
        AppiumCheckSettings clone = this.clone();
        clone.layout_(new SimpleRegionByElement(element));
        for (WebElement e : elements) {
            clone.layout_(new SimpleRegionByElement(e));
        }
        return clone;
    }

    public AppiumCheckSettings ignore(WebElement element, WebElement... elements) {
        AppiumCheckSettings clone = this.clone();
        clone.ignore_(new SimpleRegionByElement(element));
        for (WebElement e : elements) {
            clone.ignore_(new SimpleRegionByElement(e));
        }
        return clone;
    }

    public AppiumCheckSettings ignore(WebElement[] elements) {
        AppiumCheckSettings clone = this.clone();
        for (WebElement e : elements) {
            clone.ignore_(new SimpleRegionByElement(e));
        }
        return clone;
    }

    public AppiumCheckSettings strict(WebElement element, WebElement... elements) {
        AppiumCheckSettings clone = this.clone();
        clone.strict_(new SimpleRegionByElement(element));
        for (WebElement e : elements) {
            clone.strict_(new SimpleRegionByElement(e));
        }
        return clone;
    }

    public AppiumCheckSettings strict(WebElement[] elements) {
        AppiumCheckSettings clone = this.clone();
        for (WebElement e : elements) {
            clone.strict_(new SimpleRegionByElement(e));
        }
        return clone;
    }

    public AppiumCheckSettings content(WebElement element, WebElement... elements) {
        AppiumCheckSettings clone = this.clone();
        clone.content_(new SimpleRegionByElement(element));
        for (WebElement e : elements) {
            clone.content_(new SimpleRegionByElement(e));
        }
        return clone;
    }

    public AppiumCheckSettings content(WebElement[] elements) {
        AppiumCheckSettings clone = this.clone();
        for (WebElement e : elements) {
            clone.content_(new SimpleRegionByElement(e));
        }
        return clone;
    }

    public AppiumCheckSettings floating(WebElement element, int maxUpOffset, int maxDownOffset, int maxLeftOffset, int maxRightOffset) {
        AppiumCheckSettings clone = this.clone();
        clone.floating(new FloatingRegionByElement(element, maxUpOffset, maxDownOffset, maxLeftOffset, maxRightOffset));
        return clone;
    }

    public AppiumCheckSettings accessibility(By regionSelector, AccessibilityRegionType regionType) {
        AppiumCheckSettings clone = clone();
        clone.accessibility_(new AccessibilityRegionBySelector(regionSelector, regionType));
        return clone;
    }

    public AppiumCheckSettings accessibility(WebElement element, AccessibilityRegionType regionType) {
        AppiumCheckSettings clone = clone();
        clone.accessibility(new AccessibilityRegionByElement(element, regionType));
        return clone;
    }

    public AppiumCheckSettings accessibility(AccessibilityRegionType regionType, WebElement[] elementsToIgnore) {
        AppiumCheckSettings clone = clone();
        for (WebElement element : elementsToIgnore) {
            clone.accessibility(new AccessibilityRegionByElement(element, regionType));
        }
        return clone;
    }

    public AppiumCheckSettings cut(@Nonnull ElementType type, @Nonnull By selector) {
        AppiumCheckSettings clone = this.clone();
        clone.cutElementSelector = selector;
        clone.cutElementType = type;
        return clone;
    }

    public ElementType getCutElementType() {
        return cutElementType;
    }

    public By getCutElementSelector() {
        return cutElementSelector;
    }

    public AppiumCheckSettings statusBarExists() {
        AppiumCheckSettings clone = this.clone();
        clone.statusBarExists = true;
        return clone;
    }

    public AppiumCheckSettings statusBarExists(boolean statusBarExists) {
        AppiumCheckSettings clone = this.clone();
        clone.statusBarExists = statusBarExists;
        return clone;
    }

    public Boolean getStatusBarExists() {
        return statusBarExists;
    }

    public By getTargetSelector() {
        return this.targetSelector;
    }

    public WebElement getTargetElement() {
        return this.targetElement;
    }

    @Override
    public AppiumCheckSettings fully() {
        return (AppiumCheckSettings) super.fully();
    }

    @Override
    public AppiumCheckSettings fully(Boolean fully) {
        return (AppiumCheckSettings) super.fully(fully);
    }
}
