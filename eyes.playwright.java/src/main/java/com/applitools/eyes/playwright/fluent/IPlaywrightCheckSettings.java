package com.applitools.eyes.playwright.fluent;

import com.applitools.ICheckSettings;
import com.applitools.eyes.AccessibilityRegionType;
import com.applitools.eyes.Padding;
import com.applitools.eyes.Region;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Locator;

public interface IPlaywrightCheckSettings extends ICheckSettings {

    PlaywrightCheckSettings region(Region region);

    PlaywrightCheckSettings region(String selector);

    PlaywrightCheckSettings region(Locator locator);

    PlaywrightCheckSettings region(ElementHandle elementHandle);

    PlaywrightCheckSettings frame(String frameNameOrId);

    PlaywrightCheckSettings frame(Integer frameIndex);

    PlaywrightCheckSettings frame(Locator locator);

    PlaywrightCheckSettings frame(ElementHandle elementHandle);

    PlaywrightCheckSettings ignore(String selector);

    PlaywrightCheckSettings ignore(Locator locator);

    PlaywrightCheckSettings ignore(ElementHandle elementHandle);

    PlaywrightCheckSettings ignore(String selector, String regionId);

    PlaywrightCheckSettings ignore(Locator locator, String regionId);

    PlaywrightCheckSettings ignore(ElementHandle elementHandle, String regionId);

    PlaywrightCheckSettings ignore(String selector, Padding padding);

    PlaywrightCheckSettings ignore(Locator locator, Padding padding);

    PlaywrightCheckSettings ignore(ElementHandle elementHandle, Padding padding);

    PlaywrightCheckSettings layout(String selector);

    PlaywrightCheckSettings layout(Locator locator);

    PlaywrightCheckSettings layout(ElementHandle elementHandle);

    PlaywrightCheckSettings layout(String selector, String regionId);

    PlaywrightCheckSettings layout(Locator locator, String regionId);

    PlaywrightCheckSettings layout(ElementHandle elementHandle, String regionId);

    PlaywrightCheckSettings layout(String selector, Padding padding);

    PlaywrightCheckSettings layout(Locator locator, Padding padding);

    PlaywrightCheckSettings layout(ElementHandle elementHandle, Padding padding);

    PlaywrightCheckSettings strict(String selector);

    PlaywrightCheckSettings strict(Locator locator);

    PlaywrightCheckSettings strict(ElementHandle elementHandle);

    PlaywrightCheckSettings strict(String selector, String regionId);

    PlaywrightCheckSettings strict(Locator locator, String regionId);

    PlaywrightCheckSettings strict(ElementHandle elementHandle, String regionId);

    PlaywrightCheckSettings strict(String selector, Padding padding);

    PlaywrightCheckSettings strict(Locator locator, Padding padding);

    PlaywrightCheckSettings strict(ElementHandle elementHandle, Padding padding);

    PlaywrightCheckSettings content(String selector);

    PlaywrightCheckSettings content(Locator locator);

    PlaywrightCheckSettings content(ElementHandle elementHandle);

    PlaywrightCheckSettings content(String selector, String regionId);

    PlaywrightCheckSettings content(Locator locator, String regionId);

    PlaywrightCheckSettings content(ElementHandle elementHandle, String regionId);

    PlaywrightCheckSettings content(String selector, Padding padding);

    PlaywrightCheckSettings content(Locator locator, Padding padding);

    PlaywrightCheckSettings content(ElementHandle elementHandle, Padding padding);

    PlaywrightCheckSettings ignoreColors();

    PlaywrightCheckSettings ignoreColors(String selector);

    PlaywrightCheckSettings ignoreColors(Locator locator);

    PlaywrightCheckSettings ignoreColors(ElementHandle elementHandle);

    PlaywrightCheckSettings ignoreColors(String selector, String regionId);

    PlaywrightCheckSettings ignoreColors(Locator locator, String regionId);

    PlaywrightCheckSettings ignoreColors(ElementHandle elementHandle, String regionId);

    PlaywrightCheckSettings ignoreColors(String selector, Padding padding);

    PlaywrightCheckSettings ignoreColors(Locator locator, Padding padding);

    PlaywrightCheckSettings ignoreColors(ElementHandle elementHandle, Padding padding);

    PlaywrightCheckSettings floating(String selector, int maxUpOffset, int maxDownOffset, int maxLeftOffset, int maxRightOffset);

    PlaywrightCheckSettings floating(Locator locator, int maxUpOffset, int maxDownOffset, int maxLeftOffset, int maxRightOffset);

    PlaywrightCheckSettings floating(ElementHandle elementHandle, int maxUpOffset, int maxDownOffset, int maxLeftOffset, int maxRightOffset);

    PlaywrightCheckSettings floating(int maxOffset, String selector);

    PlaywrightCheckSettings floating(int maxOffset, Locator locator);

    PlaywrightCheckSettings floating(int maxOffset, ElementHandle elementHandle);

    PlaywrightCheckSettings accessibility(String selector, AccessibilityRegionType type);

    PlaywrightCheckSettings accessibility(Locator locator, AccessibilityRegionType type);

    PlaywrightCheckSettings accessibility(ElementHandle elementHandle, AccessibilityRegionType type);

    PlaywrightCheckSettings scrollRootElement(String selector);

    PlaywrightCheckSettings scrollRootElement(Locator locator);

    PlaywrightCheckSettings scrollRootElement(ElementHandle elementHandle);

    PlaywrightCheckSettings layoutBreakpoints(int... breakpoints);

    PlaywrightCheckSettings layoutBreakpoints(Boolean shouldSet);
}
