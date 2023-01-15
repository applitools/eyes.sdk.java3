package com.applitools.eyes.playwright;

import com.applitools.eyes.*;
import com.applitools.eyes.fluent.CheckSettings;
import com.applitools.eyes.fluent.FloatingRegionByRectangle;
import com.applitools.eyes.fluent.GetRegion;
import com.applitools.eyes.fluent.SimpleRegionByRectangle;
import com.applitools.eyes.playwright.fluent.AccessibilityElement;
import com.applitools.eyes.playwright.fluent.AccessibilitySelector;
import com.applitools.eyes.playwright.fluent.FloatingRegionElement;
import com.applitools.eyes.playwright.fluent.FloatingRegionSelector;
import com.applitools.eyes.playwright.universal.driver.Element;
import com.applitools.eyes.playwright.universal.driver.Selector;
import com.applitools.eyes.selenium.StitchMode;
import com.applitools.eyes.universal.Reference;
import com.applitools.eyes.visualgrid.model.VisualGridOption;
import com.applitools.utils.ArgumentGuard;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Locator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlaywrightCheckSettings extends CheckSettings implements IPlaywrightCheckSettings {

    private Reference targetElement;
    private Reference scrollRootElement;
    private final List<Reference> frameChain = new ArrayList<>();
    private Boolean isDefaultLayoutBreakpointsSet;
    private final List<Integer> layoutBreakpoints = new ArrayList<>();

    public PlaywrightCheckSettings() {
        super();
    }

    public PlaywrightCheckSettings region(String selector) {
        PlaywrightCheckSettings clone = this.clone();
        clone.targetElement = new Selector(selector);
        return clone;
    }

    public PlaywrightCheckSettings region(Region region) {
        PlaywrightCheckSettings clone = this.clone();
        clone.updateTargetRegion(region);
        return clone;
    }

    public PlaywrightCheckSettings region(Locator locator) {
        return region(locator.elementHandle());
    }

    public PlaywrightCheckSettings region(ElementHandle element) {
        PlaywrightCheckSettings clone = this.clone();
        clone.targetElement = new Element(element);
        return clone;
    }

    public PlaywrightCheckSettings frame(String selector) {
        return frame(new Selector(selector));
    }

    public PlaywrightCheckSettings frame(Frame frame) {
        return frame(frame.frameElement());
    }

    public PlaywrightCheckSettings frame(Locator frameLocator) {
        return frame(frameLocator.elementHandle());
    }

    public PlaywrightCheckSettings frame(ElementHandle frameElement) {
        return frame(new Element(frameElement));
    }

    private PlaywrightCheckSettings frame(Element element) {
        PlaywrightCheckSettings clone = this.clone();
        clone.frameChain.add(element);
        return clone;
    }

    private PlaywrightCheckSettings frame(Selector selector) {
        PlaywrightCheckSettings clone = this.clone();
        clone.frameChain.add(selector);
        return clone;
    }

    public PlaywrightCheckSettings ignore(String selector) {
        PlaywrightCheckSettings clone = this.clone();
        clone.ignoreRegions.add(new Selector(selector));
        return clone;

    }

    public PlaywrightCheckSettings ignore(Locator locator) {
        return ignore(locator.elementHandle());
    }

    public PlaywrightCheckSettings ignore(ElementHandle element) {
        PlaywrightCheckSettings clone = this.clone();
        clone.ignoreRegions.add(new Element(element));
        return clone;
    }

    public PlaywrightCheckSettings ignore(ElementHandle element, String regionId) {
        PlaywrightCheckSettings clone = this.clone();
        Element tElement = new Element(element);
        tElement.setRegionId(regionId);
        clone.ignoreRegions.add(tElement);
        return clone;
    }

    public PlaywrightCheckSettings ignore(String selector, Padding padding) {
        PlaywrightCheckSettings clone = this.clone();
        Selector tSelector = new Selector(selector);
        tSelector.setPadding(padding);
        clone.ignoreRegions.add(tSelector);
        return clone;
    }

    public PlaywrightCheckSettings ignore(Locator locator, Padding padding) {
        return ignore(locator.elementHandle(), padding);
    }

    public PlaywrightCheckSettings ignore(ElementHandle element, Padding padding) {
        PlaywrightCheckSettings clone = this.clone();
        Element tElement = new Element(element);
        tElement.setPadding(padding);
        clone.ignoreRegions.add(tElement);
        return clone;
    }

    @Override
    public PlaywrightCheckSettings ignore(Region[] regions) {
        PlaywrightCheckSettings clone = this.clone();
        for (Region r : regions) {
            clone.ignore_(r);
        }
        return clone;
    }

    @Override
    public PlaywrightCheckSettings ignore(Region region, Region... regions) {
        PlaywrightCheckSettings clone = this.clone();
        clone.ignore_(region);
        for (Region r : regions) {
            clone.ignore_(r);
        }
        return clone;
    }

    @Override
    protected void ignore_(Region region) {
        SimpleRegionByRectangle simpleRegionByRectangle = new SimpleRegionByRectangle(region);
        this.ignoreRegions.add(simpleRegionByRectangle);
    }

    public PlaywrightCheckSettings layout(String selector) {
        PlaywrightCheckSettings clone = this.clone();
        clone.layoutRegions.add(new Selector(selector));
        return clone;

    }

    public PlaywrightCheckSettings layout(Locator locator) {
        return layout(locator.elementHandle());
    }

    public PlaywrightCheckSettings layout(ElementHandle element) {
        PlaywrightCheckSettings clone = this.clone();
        clone.layoutRegions.add(new Element(element));
        return clone;
    }

    public PlaywrightCheckSettings layout(ElementHandle element, String regionId) {
        PlaywrightCheckSettings clone = this.clone();
        Element tElement = new Element(element);
        tElement.setRegionId(regionId);
        clone.layoutRegions.add(tElement);
        return clone;
    }

    public PlaywrightCheckSettings layout(String selector, Padding padding) {
        PlaywrightCheckSettings clone = this.clone();
        Selector tSelector = new Selector(selector);
        tSelector.setPadding(padding);
        clone.layoutRegions.add(tSelector);
        return clone;
    }

    public PlaywrightCheckSettings layout(Locator locator, Padding padding) {
        return layout(locator.elementHandle(), padding);
    }

    public PlaywrightCheckSettings layout(ElementHandle element, Padding padding) {
        PlaywrightCheckSettings clone = this.clone();
        Element tElement = new Element(element);
        tElement.setPadding(padding);
        clone.layoutRegions.add(tElement);
        return clone;
    }

    @Override
    public PlaywrightCheckSettings layout(Region[] regions) {
        PlaywrightCheckSettings clone = this.clone();
        for (Region r : regions) {
            clone.layout_(r);
        }
        return clone;
    }

    @Override
    public PlaywrightCheckSettings layout(Region region, Region... regions) {
        PlaywrightCheckSettings clone = this.clone();
        clone.layout_(region);
        for (Region r : regions) {
            clone.layout_(r);
        }
        return clone;
    }

    @Override
    protected void layout_(Region region) {
        SimpleRegionByRectangle simpleRegionByRectangle = new SimpleRegionByRectangle(region);
        this.layoutRegions.add(simpleRegionByRectangle);
    }

    public PlaywrightCheckSettings strict(String selector) {
        PlaywrightCheckSettings clone = this.clone();
        clone.strictRegions.add(new Selector(selector));
        return clone;

    }

    public PlaywrightCheckSettings strict(Locator locator) {
        return strict(locator.elementHandle());
    }

    public PlaywrightCheckSettings strict(ElementHandle element) {
        PlaywrightCheckSettings clone = this.clone();
        clone.strictRegions.add(new Element(element));
        return clone;
    }

    public PlaywrightCheckSettings strict(ElementHandle element, String regionId) {
        PlaywrightCheckSettings clone = this.clone();
        Element tElement = new Element(element);
        tElement.setRegionId(regionId);
        clone.strictRegions.add(tElement);
        return clone;
    }

    public PlaywrightCheckSettings strict(String selector, Padding padding) {
        PlaywrightCheckSettings clone = this.clone();
        Selector tSelector = new Selector(selector);
        tSelector.setPadding(padding);
        clone.strictRegions.add(tSelector);
        return clone;
    }

    public PlaywrightCheckSettings strict(Locator locator, Padding padding) {
        return strict(locator.elementHandle(), padding);
    }

    public PlaywrightCheckSettings strict(ElementHandle element, Padding padding) {
        PlaywrightCheckSettings clone = this.clone();
        Element tElement = new Element(element);
        tElement.setPadding(padding);
        clone.strictRegions.add(tElement);
        return clone;
    }

    @Override
    public PlaywrightCheckSettings strict(Region[] regions) {
        PlaywrightCheckSettings clone = this.clone();
        for (Region r : regions) {
            clone.strict_(r);
        }
        return clone;
    }

    @Override
    public PlaywrightCheckSettings strict(Region region, Region... regions) {
        PlaywrightCheckSettings clone = this.clone();
        clone.strict_(region);
        for (Region r : regions) {
            clone.strict_(r);
        }
        return clone;
    }

    @Override
    protected void strict_(Region region) {
        SimpleRegionByRectangle simpleRegionByRectangle = new SimpleRegionByRectangle(region);
        this.strictRegions.add(simpleRegionByRectangle);
    }

    public PlaywrightCheckSettings ignoreColors(String selector) {
        PlaywrightCheckSettings clone = this.clone();
        clone.contentRegions.add(new Selector(selector));
        return clone;
    }

    public PlaywrightCheckSettings ignoreColors(Locator locator) {
        return ignoreColors(locator.elementHandle());
    }

    public PlaywrightCheckSettings ignoreColors(ElementHandle element) {
        PlaywrightCheckSettings clone = this.clone();
        clone.contentRegions.add(new Element(element));
        return clone;
    }

    public PlaywrightCheckSettings ignoreColors(ElementHandle element, String regionId) {
        PlaywrightCheckSettings clone = this.clone();
        Element tElement = new Element(element);
        tElement.setRegionId(regionId);
        clone.contentRegions.add(tElement);
        return clone;
    }

    public PlaywrightCheckSettings ignoreColors(String selector, Padding padding) {
        PlaywrightCheckSettings clone = this.clone();
        Selector tSelector = new Selector(selector);
        tSelector.setPadding(padding);
        clone.contentRegions.add(tSelector);
        return clone;
    }

    public PlaywrightCheckSettings ignoreColors(Locator locator, Padding padding) {
        return ignoreColors(locator.elementHandle(), padding);
    }

    public PlaywrightCheckSettings ignoreColors(ElementHandle element, Padding padding) {
        PlaywrightCheckSettings clone = this.clone();
        Element tElement = new Element(element);
        tElement.setPadding(padding);
        clone.contentRegions.add(tElement);
        return clone;
    }

    public PlaywrightCheckSettings ignoreColors(Region[] regions) {
        PlaywrightCheckSettings clone = this.clone();
        for (Region r : regions) {
            clone.ignoreColors_(r);
        }
        return clone;
    }

    public PlaywrightCheckSettings ignoreColors(Region region, Region... regions) {
        PlaywrightCheckSettings clone = this.clone();
        clone.ignoreColors_(region);
        for (Region r : regions) {
            clone.ignoreColors_(r);
        }
        return clone;
    }

    protected void ignoreColors_(Region region) {
        SimpleRegionByRectangle simpleRegionByRectangle = new SimpleRegionByRectangle(region);
        this.contentRegions.add(simpleRegionByRectangle);
    }

    public PlaywrightCheckSettings floating(String selector, int maxUpOffset, int mapDownOffset, int maxLeftOffset, int maxRightOffset) {
        PlaywrightCheckSettings clone = this.clone();
        clone.floatingRegions.add(new FloatingRegionSelector(selector, maxUpOffset, mapDownOffset, maxLeftOffset, maxRightOffset));
        return clone;
    }

    public PlaywrightCheckSettings floating(Locator locator, int maxUpOffset, int mapDownOffset, int maxLeftOffset, int maxRightOffset) {
        PlaywrightCheckSettings clone = this.clone();
        clone.floatingRegions.add(new FloatingRegionElement(locator, maxUpOffset, mapDownOffset, maxLeftOffset, maxRightOffset));
        return clone;
    }

    public PlaywrightCheckSettings floating(ElementHandle element, int maxUpOffset, int mapDownOffset, int maxLeftOffset, int maxRightOffset) {
        PlaywrightCheckSettings clone = this.clone();
        clone.floatingRegions.add(new FloatingRegionElement(element, maxUpOffset, mapDownOffset, maxLeftOffset, maxRightOffset));
        return clone;
    }

    public PlaywrightCheckSettings floating(int maxOffset, String selector) {
        PlaywrightCheckSettings clone = this.clone();
        clone.floatingRegions.add(new FloatingRegionSelector(selector, maxOffset));
        return clone;
    }

    public PlaywrightCheckSettings floating(int maxOffset, Locator locator) {
        PlaywrightCheckSettings clone = this.clone();
        clone.floatingRegions.add(new FloatingRegionElement(locator, maxOffset));
        return clone;
    }

    public PlaywrightCheckSettings floating(int maxOffset, ElementHandle element) {
        PlaywrightCheckSettings clone = this.clone();
        clone.floatingRegions.add(new FloatingRegionElement(element, maxOffset));
        return clone;
    }

    @Override
    public PlaywrightCheckSettings floating(int maxOffset, Region... regions) {
        PlaywrightCheckSettings clone = this.clone();
        for (Region r: regions) {
            clone.floating_(r, maxOffset, maxOffset, maxOffset, maxOffset);
        }
        return clone;
    }

    @Override
    public PlaywrightCheckSettings floating(Region region, int maxUpOffset, int maxDownOffset, int maxLeftOffset, int maxRightOffset) {
        PlaywrightCheckSettings clone = this.clone();
        clone.floating_(region, maxUpOffset, maxDownOffset, maxLeftOffset, maxRightOffset);
        return clone;
    }

    @Override
    protected void floating_(Region region, int maxUpOffset, int maxDownOffset, int maxLeftOffset, int maxRightOffset) {
        this.floatingRegions.add(new FloatingRegionByRectangle(region, maxUpOffset, maxDownOffset, maxLeftOffset, maxRightOffset));
    }

    public PlaywrightCheckSettings accessibility(String selector, AccessibilityRegionType type) {
        PlaywrightCheckSettings clone = this.clone();
        clone.accessibilityRegions.add(new AccessibilitySelector(selector, type));
        return clone;
    }

    public PlaywrightCheckSettings accessibility(Locator locator, AccessibilityRegionType type) {
        return accessibility(locator.elementHandle(), type);
    }

    public PlaywrightCheckSettings accessibility(ElementHandle element, AccessibilityRegionType type) {
        PlaywrightCheckSettings clone = this.clone();
        clone.accessibilityRegions.add(new AccessibilityElement(element, type));
        return clone;
    }

    @Override
    public PlaywrightCheckSettings accessibility(Region region, AccessibilityRegionType regionType) {
        PlaywrightCheckSettings clone = this.clone();
        clone.accessibility_(region, regionType);
        return clone;
    }

    @Override
    protected void accessibility_(Region rect, AccessibilityRegionType regionType) {
        this.accessibilityRegions.add(new AccessibilityRegionByRectangle(rect, regionType));
    }

    public PlaywrightCheckSettings scrollRootElement(String selector) {
        PlaywrightCheckSettings clone = this.clone();
        clone.scrollRootElement = new Selector(selector);
        return clone;
    }

    public PlaywrightCheckSettings scrollRootElement(Locator locator) {
        return scrollRootElement(locator.elementHandle());
    }

    public PlaywrightCheckSettings scrollRootElement(ElementHandle element) {
        PlaywrightCheckSettings clone = this.clone();
        clone.scrollRootElement = new Element(element);
        return clone;
    }

    public PlaywrightCheckSettings layoutBreakpoints(int... breakpoints) {
        isDefaultLayoutBreakpointsSet = false;
        layoutBreakpoints.clear();
        if (breakpoints == null || breakpoints.length == 0) {
            return this;
        }

        for (int breakpoint : breakpoints) {
            ArgumentGuard.greaterThanZero(breakpoint, "breakpoint");
            layoutBreakpoints.add(breakpoint);
        }

        Collections.sort(layoutBreakpoints);
        return this;
    }

    public PlaywrightCheckSettings layoutBreakpoints(Boolean shouldSet) {
        this.isDefaultLayoutBreakpointsSet = shouldSet;
        layoutBreakpoints.clear();
        return this;
    }

    @Override
    public PlaywrightCheckSettings setLayoutBreakpoints(int... breakpoints) {
        return layoutBreakpoints(breakpoints);
    }

    @Override
    public PlaywrightCheckSettings setLayoutBreakpoints(Boolean shouldSet) {
        return layoutBreakpoints(shouldSet);
    }

    @Override
    public PlaywrightCheckSettings fully() {
        return fully(true);
    }

    @Override
    public PlaywrightCheckSettings fully(Boolean fully) {
        return (PlaywrightCheckSettings) super.fully(fully);
    }

    @Override
    public PlaywrightCheckSettings withName(String name) {
        return (PlaywrightCheckSettings) super.withName(name);
    }

    @Override
    public PlaywrightCheckSettings ignoreCaret(Boolean ignoreCaret) {
        return (PlaywrightCheckSettings) super.ignoreCaret(ignoreCaret);
    }

    @Override
    public PlaywrightCheckSettings sendDom(Boolean sendDom) {
        return (PlaywrightCheckSettings) super.sendDom(sendDom);
    }

    @Override
    public PlaywrightCheckSettings sendDom() {
        return (PlaywrightCheckSettings) super.sendDom();
    }

    @Override
    public PlaywrightCheckSettings enablePatterns(Boolean enablePatterns) {
        return (PlaywrightCheckSettings) super.enablePatterns(enablePatterns);
    }

    @Override
    public PlaywrightCheckSettings enablePatterns() {
        return (PlaywrightCheckSettings) super.enablePatterns();
    }

    @Override
    public PlaywrightCheckSettings beforeRenderScreenshotHook(String hook) {
        return (PlaywrightCheckSettings) super.beforeRenderScreenshotHook(hook);
    }

    /**
     * {@inheritDoc}
     *
     * @param timeoutMilliseconds
     */
    @Override
    public PlaywrightCheckSettings timeout(Integer timeoutMilliseconds) {
        return (PlaywrightCheckSettings) super.timeout(timeoutMilliseconds);
    }

    @Override
    public PlaywrightCheckSettings useDom(Boolean useDom) {
        return (PlaywrightCheckSettings) super.useDom(useDom);
    }

    @Override
    public PlaywrightCheckSettings variationGroupId(String variationGroupId) {
        return (PlaywrightCheckSettings) super.variationGroupId(variationGroupId);
    }

    @Override
    public PlaywrightCheckSettings visualGridOptions(VisualGridOption... options) {
        return (PlaywrightCheckSettings) super.visualGridOptions(options);
    }

    @Override
    public PlaywrightCheckSettings waitBeforeCapture(Integer milliSec) {
        return (PlaywrightCheckSettings) super.waitBeforeCapture(milliSec);
    }

    @Override
    public PlaywrightCheckSettings setDisableBrowserFetching(Boolean disableBrowserFetching) {
        return (PlaywrightCheckSettings) super.setDisableBrowserFetching(disableBrowserFetching);
    }

    @Override
    public PlaywrightCheckSettings autProxy(AutProxySettings autProxy) {
        return (PlaywrightCheckSettings) super.autProxy(autProxy);
    }

    @Override
    public PlaywrightCheckSettings setAccessibilityValidation(AccessibilitySettings accessibilitySettings) {
        return (PlaywrightCheckSettings) super.setAccessibilityValidation(accessibilitySettings);
    }

    @Override
    public PlaywrightCheckSettings stitchMode(StitchMode stitchMode) {
        return (PlaywrightCheckSettings) super.stitchMode(stitchMode);
    }

    @Override
    public PlaywrightCheckSettings setHideScrollBars(Boolean hideScrollBars) {
        return (PlaywrightCheckSettings) super.setHideScrollBars(hideScrollBars);
    }

    @Override
    public PlaywrightCheckSettings setHideCaret(Boolean hideCaret) {
        return (PlaywrightCheckSettings) super.setHideCaret(hideCaret);
    }

    @Override
    public PlaywrightCheckSettings setOverlap(Integer overlap) {
        return (PlaywrightCheckSettings) super.setOverlap(overlap);
    }

    @Override
    public PlaywrightCheckSettings pageId(String pageId) {
        return (PlaywrightCheckSettings) super.pageId(pageId);
    }

    @Override
    public PlaywrightCheckSettings ignoreDisplacements(Boolean ignoreDisplacements) {
        return (PlaywrightCheckSettings) super.ignoreDisplacements(ignoreDisplacements);
    }

    @Override
    public PlaywrightCheckSettings ignoreDisplacements() {
        return (PlaywrightCheckSettings) super.ignoreDisplacements();
    }

    @Override
    public PlaywrightCheckSettings ignoreCaret() {
        return (PlaywrightCheckSettings) super.ignoreCaret();
    }

    @Override
    public PlaywrightCheckSettings matchLevel(MatchLevel matchLevel) {
        return (PlaywrightCheckSettings) super.matchLevel(matchLevel);
    }

    @Override
    public PlaywrightCheckSettings strict() {
        return (PlaywrightCheckSettings) super.strict();
    }

    @Override
    public PlaywrightCheckSettings layout() {
        return (PlaywrightCheckSettings) super.layout();
    }

    @Override
    public PlaywrightCheckSettings exact() {
        return (PlaywrightCheckSettings) super.exact();
    }

    @Override
    public PlaywrightCheckSettings lazyLoad() {
        return (PlaywrightCheckSettings) super.lazyLoad();
    }

    @Override
    public PlaywrightCheckSettings lazyLoad(LazyLoadOptions lazyLoadOptions) {
        return (PlaywrightCheckSettings) super.lazyLoad(lazyLoadOptions);
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public PlaywrightCheckSettings clone() {
        PlaywrightCheckSettings clone = new PlaywrightCheckSettings();
        super.populateClone(clone);
        clone.targetElement = this.targetElement;
        clone.scrollRootElement = this.scrollRootElement;
        clone.frameChain.addAll(this.frameChain);
        clone.isDefaultLayoutBreakpointsSet = this.isDefaultLayoutBreakpointsSet;
        clone.layoutBreakpoints.addAll(this.layoutBreakpoints);
        clone.ignoreRegions.addAll(this.ignoreRegions);
        clone.contentRegions.addAll(this.contentRegions);
        clone.layoutRegions.addAll(this.layoutRegions);
        clone.strictRegions.addAll(this.strictRegions);
        clone.floatingRegions.addAll(this.floatingRegions);
        clone.accessibilityRegions.addAll(this.accessibilityRegions);
        return clone;
    }

    public Reference getTargetElement() {
        return targetElement;
    }

    public Reference getScrollRootElement() {
        return scrollRootElement;
    }

    public List<Reference> getFrameChain() {
        return frameChain;
    }

    @Override
    public LazyLoadOptions getLazyLoadOptions() {
        return super.getLazyLoadOptions();
    }

    @Override
    public GetRegion[] getIgnoreRegions() {
        return ignoreRegions.toArray(new GetRegion[0]);
    }

    @Override
    public GetRegion[] getLayoutRegions() {
        return layoutRegions.toArray(new GetRegion[0]);
    }

    @Override
    public GetRegion[] getStrictRegions() {
        return strictRegions.toArray(new GetRegion[0]);
    }

    @Override
    public GetRegion[] getContentRegions() {
        return contentRegions.toArray(new GetRegion[0]);
    }

    @Override
    public GetRegion[] getFloatingRegions() {
        return floatingRegions.toArray(new GetRegion[0]);
    }

    @Override
    public GetRegion[] getAccessibilityRegions() {
        return accessibilityRegions.toArray(new GetRegion[0]);
    }

    @Deprecated
    @Override
    public PlaywrightCheckSettings content() {
        return (PlaywrightCheckSettings) super.content();
    }

    @Deprecated
    public PlaywrightCheckSettings content(String selector) {
        return ignoreColors(selector);
    }

    @Deprecated
    public PlaywrightCheckSettings content(Locator locator) {
        return ignoreColors(locator.elementHandle());
    }

    @Deprecated
    public PlaywrightCheckSettings content(ElementHandle element) {
        return ignoreColors(element);
    }

    @Deprecated
    public PlaywrightCheckSettings content(ElementHandle element, String regionId) {
        return ignoreColors(element, regionId);
    }

    @Deprecated
    public PlaywrightCheckSettings content(String selector, Padding padding) {
        return ignoreColors(selector, padding);
    }

    @Deprecated
    public PlaywrightCheckSettings content(Locator locator, Padding padding) {
        return ignoreColors(locator.elementHandle(), padding);
    }

    @Deprecated
    public PlaywrightCheckSettings content(ElementHandle element, Padding padding) {
        return ignoreColors(element, padding);
    }

    @Deprecated
    @Override
    public PlaywrightCheckSettings content(Region[] regions) {
        return ignoreColors(regions);
    }

    @Deprecated
    @Override
    public PlaywrightCheckSettings content(Region region, Region... regions) {
        return ignoreColors(region, regions);
    }
}
