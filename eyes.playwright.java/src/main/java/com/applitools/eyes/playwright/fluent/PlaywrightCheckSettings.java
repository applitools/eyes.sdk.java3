package com.applitools.eyes.playwright.fluent;

import com.applitools.eyes.*;
import com.applitools.eyes.fluent.CheckSettings;
import com.applitools.eyes.fluent.FloatingRegionByRectangle;
import com.applitools.eyes.fluent.GetRegion;
import com.applitools.eyes.fluent.SimpleRegionByRectangle;
import com.applitools.eyes.playwright.universal.dto.Element;
import com.applitools.eyes.playwright.universal.dto.FrameLocator;
import com.applitools.eyes.playwright.universal.dto.Selector;
import com.applitools.eyes.selenium.StitchMode;
import com.applitools.eyes.universal.Reference;
import com.applitools.eyes.visualgrid.model.VisualGridOption;
import com.applitools.utils.ArgumentGuard;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Locator;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlaywrightCheckSettings extends CheckSettings implements IPlaywrightCheckSettings {

    /** The target region. */
    private Reference targetElement;

    /** the scroll root element. */
    private Reference scrollRootElement;

    /** The frame chain. */
    private final List<FrameLocator> frameChain = new ArrayList<>();

    /** The layout breakpoints. */
    private final List<Integer> layoutBreakpoints = new ArrayList<>();

    /** Used to determine if using default layout breakpoints. */
    private Boolean isDefaultLayoutBreakpointsSet;

    public PlaywrightCheckSettings() {
        super();
    }

    /**
     * Specify the target as a region.
     *
     * @param region  the region
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings region(@Nonnull Region region) {
        PlaywrightCheckSettings clone = this.clone();
        clone.updateTargetRegion(region);
        return clone;
    }

    /**
     * Specify the target as a region.
     *
     * @param selector  the css or xpath selector
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings region(@Nonnull String selector) {
        return region(new Selector(selector));
    }

    /**
     * Specify the target as a region.
     *
     * @param locator  the locator
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings region(@Nonnull Locator locator) {
        return region(new Selector(locator));
    }

    /**
     * Specify the target as a region.
     *
     * @param element  the element
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings region(@Nonnull ElementHandle element) {
        return region(new Element(element));
    }

    /** internal */
    private PlaywrightCheckSettings region(Selector selector) {
        PlaywrightCheckSettings clone = this.clone();
        clone.targetElement = selector;
        return clone;
    }

    /** internal */
    private PlaywrightCheckSettings region(Element element) {
        PlaywrightCheckSettings clone = this.clone();
        clone.targetElement = element;
        return clone;
    }

    /**
     * Specify the target as a frame.
     *
     * @param frameNameOrId  the frame's name or its id
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings frame(@Nonnull String frameNameOrId) {
        FrameLocator frameLocator = new FrameLocator();
        frameLocator.setFrameNameOrId(frameNameOrId);
        return frame(frameLocator);
    }

    /**
     * Specify the target as a frame.
     *
     * @param frameIndex  the frame's index
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings frame(@Nonnull Integer frameIndex) {
        FrameLocator frameLocator = new FrameLocator();
        frameLocator.setFrameIndex(frameIndex);
        return frame(frameLocator);
    }

    /**
     * Specify the target as a frame.
     *
     * @param frame  the frame
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings frame(@Nonnull Frame frame) {
        return frame(frame.frameElement());
    }

    /**
     * Specify the target as a frame.
     *
     * @param frameLocator  the frame as a locator
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings frame(@Nonnull Locator frameLocator) {
        return frame(new Selector(frameLocator));
    }

    /**
     * Specify the target as a frame.
     *
     * @param frameElement  the frame as an element
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings frame(@Nonnull ElementHandle frameElement) {
        return frame(new Element(frameElement));
    }

    /** internal */
    private PlaywrightCheckSettings frame(Selector selector) {
        FrameLocator frameLocator = new FrameLocator();
        frameLocator.setFrameSelector(selector);
        return frame(frameLocator);
    }

    /** internal */
    private PlaywrightCheckSettings frame(Element element) {
        FrameLocator frameLocator = new FrameLocator();
        frameLocator.setFrameElement(element);
        return frame(frameLocator);
    }

    /** internal */
    private PlaywrightCheckSettings frame(FrameLocator frameLocator) {
        PlaywrightCheckSettings clone = this.clone();
        clone.frameChain.add(frameLocator);
        return clone;
    }

    /**
     * Adds an ignore region.
     *
     * @param selector  the css or xpath selector to ignore match
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings ignore(@Nonnull String selector) {
        return ignore(new Selector(selector));
    }

    /**
     * Adds an ignore region.
     *
     * @param locator  the locator to ignore match
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings ignore(@Nonnull Locator locator) {
        return ignore(new Selector(locator));
    }

    /**
     * Adds an ignore region.
     *
     * @param element  the element to ignore match
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings ignore(@Nonnull ElementHandle element) {
        return ignore(new Element(element));
    }

    /** internal */
    private PlaywrightCheckSettings ignore(Selector selector) {
        PlaywrightCheckSettings clone = this.clone();
        clone.ignoreRegions.add(selector);
        return clone;
    }

    /** internal */
    private PlaywrightCheckSettings ignore(Element element) {
        PlaywrightCheckSettings clone = this.clone();
        clone.ignoreRegions.add(element);
        return clone;
    }

    /**
     * Adds an ignore region with a region ID.
     *
     * @param selector  the css or xpath selector to ignore match
     * @param regionId  the region ID
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings ignore(@Nonnull String selector, @Nonnull String regionId) {
        return ignore(new Selector(selector), regionId);
    }

    /**
     * Adds an ignore region with a region ID.
     *
     * @param locator   the locator to ignore match
     * @param regionId  the region ID
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings ignore(@Nonnull Locator locator, @Nonnull String regionId) {
        return ignore(new Selector(locator), regionId);
    }

    /**
     * Adds an ignore region with a region ID.
     *
     * @param element   the element to ignore match
     * @param regionId  the region ID
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings ignore(@Nonnull ElementHandle element, @Nonnull String regionId) {
        return ignore(new Element(element), regionId);
    }

    /** internal */
    private PlaywrightCheckSettings ignore(Selector selector, String regionId) {
        PlaywrightCheckSettings clone = this.clone();
        selector.setRegionId(regionId);
        clone.ignoreRegions.add(selector);
        return clone;
    }

    /** internal */
    private PlaywrightCheckSettings ignore(Element element, String regionId) {
        PlaywrightCheckSettings clone = this.clone();
        element.setRegionId(regionId);
        clone.ignoreRegions.add(element);
        return clone;
    }

    /**
     * Adds an ignore region with {@link Padding}.
     *
     * @param selector  the css or xpath selector to ignore match
     * @param padding  the padding
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings ignore(@Nonnull String selector, @Nonnull Padding padding) {
        return ignore(new Selector(selector), padding);
    }

    /**
     * Adds an ignore region with {@link Padding}.
     *
     * @param locator  the locator to ignore match
     * @param padding  the padding
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings ignore(@Nonnull Locator locator, @Nonnull Padding padding) {
        return ignore(new Selector(locator), padding);
    }

    /**
     * Adds an ignore region with {@link Padding}.
     *
     * @param element  the element to ignore match
     * @param padding  the padding
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings ignore(@Nonnull ElementHandle element, @Nonnull Padding padding) {
        return ignore(new Element(element), padding);
    }

    /** internal */
    private PlaywrightCheckSettings ignore(@Nonnull Selector selector, @Nonnull Padding padding) {
        PlaywrightCheckSettings clone = this.clone();
        selector.setPadding(padding);
        clone.ignoreRegions.add(selector);
        return clone;
    }

    /** internal */
    private PlaywrightCheckSettings ignore(@Nonnull Element element, @Nonnull Padding padding) {
        PlaywrightCheckSettings clone = this.clone();
        element.setPadding(padding);
        clone.ignoreRegions.add(element);
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings ignore(@Nonnull Region[] regions) {
        PlaywrightCheckSettings clone = this.clone();
        for (Region r : regions) {
            clone.ignore_(r);
        }
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings ignore(@Nonnull Region region, Region... regions) {
        PlaywrightCheckSettings clone = this.clone();
        clone.ignore_(region);
        for (Region r : regions) {
            clone.ignore_(r);
        }
        return clone;
    }

    /** internal */
    @Override
    protected void ignore_(Region region) {
        SimpleRegionByRectangle simpleRegionByRectangle = new SimpleRegionByRectangle(region);
        this.ignoreRegions.add(simpleRegionByRectangle);
    }

    /**
     * Adds a layout region.
     *
     * @param selector  the css or xpath selector to match using the layout algorithm
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings layout(@Nonnull String selector) {
        return layout(new Selector(selector));
    }

    /**
     * Adds a layout region.
     *
     * @param locator  the locator to match using the layout algorithm
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings layout(@Nonnull Locator locator) {
        return layout(new Selector(locator));
    }

    /**
     * Adds a layout region.
     *
     * @param element  the css or xpath selector to match using the layout algorithm
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings layout(@Nonnull ElementHandle element) {
        return layout(new Element(element));
    }

    /** internal */
    private PlaywrightCheckSettings layout(Selector selector) {
        PlaywrightCheckSettings clone = this.clone();
        clone.layoutRegions.add(selector);
        return clone;
    }

    /** internal */
    private PlaywrightCheckSettings layout(Element element) {
        PlaywrightCheckSettings clone = this.clone();
        clone.layoutRegions.add(element);
        return clone;
    }

    /**
     * Adds a layout region with a region ID.
     *
     * @param selector  the css or xpath selector to match using the layout algorithm
     * @param regionId  the region ID
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings layout(@Nonnull String selector, @Nonnull String regionId) {
        return layout(new Selector(selector), regionId);
    }

    /**
     * Adds a layout region with a region ID.
     *
     * @param locator   the locator to match using the layout algorithm
     * @param regionId  the region ID
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings layout(@Nonnull Locator locator, @Nonnull String regionId) {
        return layout(new Selector(locator), regionId);
    }

    /**
     * Adds a layout region with a region ID.
     *
     * @param element   the element to match using the layout algorithm
     * @param regionId  the region ID
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings layout(@Nonnull ElementHandle element, @Nonnull String regionId) {
        return layout(new Element(element), regionId);
    }

    /** internal */
    private PlaywrightCheckSettings layout(Selector selector, String regionId) {
        PlaywrightCheckSettings clone = this.clone();
        selector.setRegionId(regionId);
        clone.layoutRegions.add(selector);
        return clone;
    }

    /** internal */
    private PlaywrightCheckSettings layout(Element element, String regionId) {
        PlaywrightCheckSettings clone = this.clone();
        element.setRegionId(regionId);
        clone.layoutRegions.add(element);
        return clone;
    }

    /**
     * Adds a layout region with {@link Padding}.
     *
     * @param selector  the css or xpath selector to match using the layout algorithm
     * @param padding   the padding
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings layout(@Nonnull String selector, @Nonnull Padding padding) {
        return layout(new Selector(selector), padding);
    }

    /**
     * Adds a layout region with {@link Padding}.
     *
     * @param locator   the locator to match using the layout algorithm
     * @param padding   the padding
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings layout(@Nonnull Locator locator, @Nonnull Padding padding) {
        return layout(new Selector(locator), padding);
    }

    /**
     * Adds a layout region with {@link Padding}.
     *
     * @param element   the element to match using the layout algorithm
     * @param padding   the padding
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings layout(@Nonnull ElementHandle element, @Nonnull Padding padding) {
        return layout(new Element(element), padding);
    }

    /** internal */
    private PlaywrightCheckSettings layout(@Nonnull Selector selector, @Nonnull Padding padding) {
        PlaywrightCheckSettings clone = this.clone();
        selector.setPadding(padding);
        clone.layoutRegions.add(selector);
        return clone;
    }

    /** internal */
    private PlaywrightCheckSettings layout(@Nonnull Element element, @Nonnull Padding padding) {
        PlaywrightCheckSettings clone = this.clone();
        element.setPadding(padding);
        clone.layoutRegions.add(element);
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings layout(@Nonnull Region[] regions) {
        PlaywrightCheckSettings clone = this.clone();
        for (Region r : regions) {
            clone.layout_(r);
        }
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings layout(@Nonnull Region region, Region... regions) {
        PlaywrightCheckSettings clone = this.clone();
        clone.layout_(region);
        for (Region r : regions) {
            clone.layout_(r);
        }
        return clone;
    }

    /** internal */
    @Override
    protected void layout_(Region region) {
        SimpleRegionByRectangle simpleRegionByRectangle = new SimpleRegionByRectangle(region);
        this.layoutRegions.add(simpleRegionByRectangle);
    }

    /**
     * Adds a strict region.
     *
     * @param selector  the css or xpath selector to match using the strict algorithm
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings strict(@Nonnull String selector) {
        return strict(new Selector(selector));
    }

    /**
     * Adds a strict region.
     *
     * @param locator  the locator to match using the strict algorithm
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings strict(@Nonnull Locator locator) {
        return strict(new Selector(locator));
    }

    /**
     * Adds a strict region.
     *
     * @param element  the element to match using the strict algorithm
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings strict(@Nonnull ElementHandle element) {
        return strict(new Element(element));
    }

    /** internal */
    private PlaywrightCheckSettings strict(@Nonnull Selector selector) {
        PlaywrightCheckSettings clone = this.clone();
        clone.strictRegions.add(selector);
        return clone;
    }

    /** internal */
    private PlaywrightCheckSettings strict(@Nonnull Element element) {
        PlaywrightCheckSettings clone = this.clone();
        clone.strictRegions.add(element);
        return clone;
    }

    /**
     * Adds a strict region with a region ID.
     *
     * @param selector  the css or xpath selector to match using the strict algorithm
     * @param regionId  the region ID
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings strict(@Nonnull String selector, @Nonnull String regionId) {
        return strict(new Selector(selector), regionId);
    }

    /**
     * Adds a strict region with a region ID.
     *
     * @param locator   the locator to match using the strict algorithm
     * @param regionId  the region ID
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings strict(@Nonnull Locator locator, @Nonnull String regionId) {
        return strict(new Selector(locator), regionId);
    }

    /**
     * Adds a strict region with a region ID.
     *
     * @param element   the element to match using the strict algorithm
     * @param regionId  the region ID
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings strict(@Nonnull ElementHandle element, @Nonnull String regionId) {
        return strict(new Element(element), regionId);
    }

    /** internal */
    private PlaywrightCheckSettings strict(Selector selector, String regionId) {
        PlaywrightCheckSettings clone = this.clone();
        selector.setRegionId(regionId);
        clone.strictRegions.add(selector);
        return clone;
    }

    /** internal */
    private PlaywrightCheckSettings strict(Element element, String regionId) {
        PlaywrightCheckSettings clone = this.clone();
        element.setRegionId(regionId);
        clone.strictRegions.add(element);
        return clone;
    }

    /**
     * Adds a strict region with {@link Padding}.
     *
     * @param selector   the css or xpath selector to match using the strict algorithm
     * @param padding    the padding
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings strict(@Nonnull String selector, @Nonnull Padding padding) {
        return strict(new Selector(selector), padding);
    }

    /**
     * Adds a strict region with {@link Padding}.
     *
     * @param locator   the locator to match using the strict algorithm
     * @param padding   the padding
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings strict(@Nonnull Locator locator, @Nonnull Padding padding) {
        return strict(new Selector(locator), padding);
    }

    /**
     * Adds a strict region with {@link Padding}.
     *
     * @param element   the element to match using the strict algorithm
     * @param padding   the padding
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings strict(@Nonnull ElementHandle element, @Nonnull Padding padding) {
        return strict(new Element(element), padding);
    }

    /** internal */
    private PlaywrightCheckSettings strict(@Nonnull Selector selector, @Nonnull Padding padding) {
        PlaywrightCheckSettings clone = this.clone();
        selector.setPadding(padding);
        clone.strictRegions.add(selector);
        return clone;
    }

    /** internal */
    private PlaywrightCheckSettings strict(@Nonnull Element element, @Nonnull Padding padding) {
        PlaywrightCheckSettings clone = this.clone();
        element.setPadding(padding);
        clone.strictRegions.add(element);
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings strict(@Nonnull Region[] regions) {
        PlaywrightCheckSettings clone = this.clone();
        for (Region r : regions) {
            clone.strict_(r);
        }
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings strict(@Nonnull Region region, Region... regions) {
        PlaywrightCheckSettings clone = this.clone();
        clone.strict_(region);
        for (Region r : regions) {
            clone.strict_(r);
        }
        return clone;
    }

    /** internal */
    @Override
    protected void strict_(Region region) {
        SimpleRegionByRectangle simpleRegionByRectangle = new SimpleRegionByRectangle(region);
        this.strictRegions.add(simpleRegionByRectangle);
    }

    /**
     * Adds an ignore colors region.
     *
     * @param selector  the css or xpath selector to match using the ignore colors algorithm
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings ignoreColors(@Nonnull String selector) {
        return ignoreColors(new Selector(selector));
    }

    /**
     * Adds an ignore colors region.
     *
     * @param locator  the locator to match using the ignore colors algorithm
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings ignoreColors(@Nonnull Locator locator) {
        return ignoreColors(new Selector(locator));
    }

    /**
     * Adds an ignore colors region.
     *
     * @param element  the element to match using the ignore colors algorithm
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings ignoreColors(@Nonnull ElementHandle element) {
        return ignoreColors(new Element(element));
    }

    /** internal */
    private PlaywrightCheckSettings ignoreColors(Selector selector) {
        PlaywrightCheckSettings clone = this.clone();
        clone.contentRegions.add(selector);
        return clone;
    }

    /** internal */
    private PlaywrightCheckSettings ignoreColors(Element element) {
        PlaywrightCheckSettings clone = this.clone();
        clone.contentRegions.add(element);
        return clone;
    }

    /**
     * Adds an ignore colors region with a region ID.
     *
     * @param selector  the css or xpath to match using the ignore colors algorithm
     * @param regionId  the region ID
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings ignoreColors(@Nonnull String selector, @Nonnull String regionId) {
        return ignoreColors(new Selector(selector), regionId);
    }

    /**
     * Adds an ignore colors region with a region ID.
     *
     * @param locator   the locator to match using the ignore colors algorithm
     * @param regionId  the region ID
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings ignoreColors(@Nonnull Locator locator, @Nonnull String regionId) {
        return ignoreColors(new Selector(locator), regionId);
    }

    /**
     * Adds an ignore colors region with a region ID.
     *
     * @param element   the element to match using the ignore colors algorithm
     * @param regionId  the region ID
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings ignoreColors(@Nonnull ElementHandle element, @Nonnull String regionId) {
        return ignoreColors(new Element(element), regionId);
    }

    /** internal */
    private PlaywrightCheckSettings ignoreColors(Selector selector, String regionId) {
        PlaywrightCheckSettings clone = this.clone();
        selector.setRegionId(regionId);
        clone.contentRegions.add(selector);
        return clone;
    }

    /** internal */
    private PlaywrightCheckSettings ignoreColors(Element element, String regionId) {
        PlaywrightCheckSettings clone = this.clone();
        element.setRegionId(regionId);
        clone.contentRegions.add(element);
        return clone;
    }

    /**
     * Adds an ignore colors region with {@link Padding}.
     *
     * @param selector  the css or xpath selector to match using the ignore colors algorithm
     * @param padding   the padding
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings ignoreColors(@Nonnull String selector, @Nonnull Padding padding) {
        return ignoreColors(new Selector(selector), padding);
    }

    /**
     * Adds an ignore colors region with {@link Padding}.
     *
     * @param locator   the locator to match using the ignore colors algorithm
     * @param padding   the padding
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings ignoreColors(@Nonnull Locator locator, @Nonnull Padding padding) {
        return ignoreColors(new Selector(locator), padding);
    }

    /**
     * Adds an ignore colors region with {@link Padding}.
     *
     * @param element   the element to match using the ignore colors algorithm
     * @param padding   the padding
     * @return an updated instance of this settings object
     */
    public PlaywrightCheckSettings ignoreColors(@Nonnull ElementHandle element, @Nonnull Padding padding) {
        return ignoreColors(new Element(element), padding);
    }

    /** internal */
    private PlaywrightCheckSettings ignoreColors(Selector selector, Padding padding) {
        PlaywrightCheckSettings clone = this.clone();
        selector.setPadding(padding);
        clone.contentRegions.add(selector);
        return clone;
    }

    /** internal */
    private PlaywrightCheckSettings ignoreColors(Element element, Padding padding) {
        PlaywrightCheckSettings clone = this.clone();
        element.setPadding(padding);
        clone.contentRegions.add(element);
        return clone;
    }

    /**
     * Adds one or more ignore colors regions.
     *
     * @param regions An array of regions to match using the ignore colors method.
     * @return An updated clone of this settings object.
     */
    public PlaywrightCheckSettings ignoreColors(@Nonnull Region[] regions) {
        PlaywrightCheckSettings clone = this.clone();
        for (Region r : regions) {
            clone.ignoreColors_(r);
        }
        return clone;
    }

    /**
     * Adds one or more ignore colors regions.
     *
     * @param region A region to match using the Content method.
     * @param regions Optional extra regions to match using the Content method.
     * @return An updated clone of this settings object.
     */
    public PlaywrightCheckSettings ignoreColors(@Nonnull Region region, Region... regions) {
        PlaywrightCheckSettings clone = this.clone();
        clone.ignoreColors_(region);
        for (Region r : regions) {
            clone.ignoreColors_(r);
        }
        return clone;
    }

    /** internal */
    protected void ignoreColors_(Region region) {
        SimpleRegionByRectangle simpleRegionByRectangle = new SimpleRegionByRectangle(region);
        this.contentRegions.add(simpleRegionByRectangle);
    }

    /**
     * Adds a floating region.
     * A floating region is a region that can be placed within the boundaries of a bigger region.
     *
     * @param selector          the css or xpath selector
     * @param maxUpOffset       how much the content can move up
     * @param maxDownOffset     how much the content can move down
     * @param maxLeftOffset     how much the content can move to the left
     * @param maxRightOffset    how much the content can move to the right
     * @return an updated clone of this settings object
     */
    public PlaywrightCheckSettings floating(String selector, int maxUpOffset, int maxDownOffset, int maxLeftOffset, int maxRightOffset) {
        PlaywrightCheckSettings clone = this.clone();
        clone.floatingRegions.add(new FloatingRegionSelector(selector, maxUpOffset, maxDownOffset, maxLeftOffset, maxRightOffset));
        return clone;
    }

    /**
     * Adds a floating region.
     * A floating region is a region that can be placed within the boundaries of a bigger region.
     *
     * @param locator           the locator
     * @param maxUpOffset       how much the content can move up
     * @param maxDownOffset     how much the content can move down
     * @param maxLeftOffset     how much the content can move to the left
     * @param maxRightOffset    how much the content can move to the right
     * @return an updated clone of this settings object
     */
    public PlaywrightCheckSettings floating(Locator locator, int maxUpOffset, int maxDownOffset, int maxLeftOffset, int maxRightOffset) {
        PlaywrightCheckSettings clone = this.clone();
        clone.floatingRegions.add(new FloatingRegionSelector(locator, maxUpOffset, maxDownOffset, maxLeftOffset, maxRightOffset));
        return clone;
    }

    /**
     * Adds a floating region.
     * A floating region is a region that can be placed within the boundaries of a bigger region.
     *
     * @param element           the element
     * @param maxUpOffset       how much the content can move up
     * @param maxDownOffset     how much the content can move down
     * @param maxLeftOffset     how much the content can move to the left
     * @param maxRightOffset    how much the content can move to the right
     * @return an updated clone of this settings object
     */
    public PlaywrightCheckSettings floating(ElementHandle element, int maxUpOffset, int maxDownOffset, int maxLeftOffset, int maxRightOffset) {
        PlaywrightCheckSettings clone = this.clone();
        clone.floatingRegions.add(new FloatingRegionElement(element, maxUpOffset, maxDownOffset, maxLeftOffset, maxRightOffset));
        return clone;
    }

    /**
     * Adds a floating region.
     * A floating region is a region that can be placed within the boundaries of a bigger region.
     *
     * @param maxOffset    how much the content can move in all directions
     * @param selector     the css or xpath selector
     * @return an updated clone of this settings object
     */
    public PlaywrightCheckSettings floating(int maxOffset, String selector) {
        PlaywrightCheckSettings clone = this.clone();
        clone.floatingRegions.add(new FloatingRegionSelector(selector, maxOffset));
        return clone;
    }

    /**
     * Adds a floating region.
     * A floating region is a region that can be placed within the boundaries of a bigger region.
     *
     * @param maxOffset     how much the content can move in all directions
     * @param locator       the locator
     * @return an updated clone of this settings object
     */
    public PlaywrightCheckSettings floating(int maxOffset, Locator locator) {
        PlaywrightCheckSettings clone = this.clone();
        clone.floatingRegions.add(new FloatingRegionSelector(locator, maxOffset));
        return clone;
    }

    /**
     * Adds a floating region.
     * A floating region is a region that can be placed within the boundaries of a bigger region.
     *
     * @param maxOffset     how much the content can move in all directions
     * @param element       the element
     * @return an updated clone of this settings object
     */
    public PlaywrightCheckSettings floating(int maxOffset, ElementHandle element) {
        PlaywrightCheckSettings clone = this.clone();
        clone.floatingRegions.add(new FloatingRegionElement(element, maxOffset));
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings floating(int maxOffset, Region... regions) {
        PlaywrightCheckSettings clone = this.clone();
        for (Region r: regions) {
            clone.floating_(r, maxOffset, maxOffset, maxOffset, maxOffset);
        }
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings floating(Region region, int maxUpOffset, int maxDownOffset, int maxLeftOffset, int maxRightOffset) {
        PlaywrightCheckSettings clone = this.clone();
        clone.floating_(region, maxUpOffset, maxDownOffset, maxLeftOffset, maxRightOffset);
        return clone;
    }

    /** internal */
    @Override
    protected void floating_(Region region, int maxUpOffset, int maxDownOffset, int maxLeftOffset, int maxRightOffset) {
        this.floatingRegions.add(new FloatingRegionByRectangle(region, maxUpOffset, maxDownOffset, maxLeftOffset, maxRightOffset));
    }

    /**
     * Adds an accessibility region with a {@link AccessibilityRegionType}.
     *
     * @param selector      the css or xpath selector
     * @param type          the accessibility region type
     * @return an updated clone of this settings object
     */
    public PlaywrightCheckSettings accessibility(@Nonnull String selector, AccessibilityRegionType type) {
        return accessibility(new AccessibilitySelector(selector, type));
    }

    /**
     * Adds an accessibility region with a {@link AccessibilityRegionType}.
     *
     * @param locator       the css or xpath selector
     * @param type          the accessibility region type
     * @return an updated clone of this settings object
     */
    public PlaywrightCheckSettings accessibility(@Nonnull Locator locator, AccessibilityRegionType type) {
        return accessibility(new AccessibilitySelector(locator, type));
    }

    /**
     * Adds an accessibility region with a {@link AccessibilityRegionType}.
     *
     * @param element       the element
     * @param type          the accessibility region type
     * @return an updated clone of this settings object
     */
    public PlaywrightCheckSettings accessibility(@Nonnull ElementHandle element, AccessibilityRegionType type) {
        return accessibility(new AccessibilityElement(element, type));
    }

    /** internal */
    private PlaywrightCheckSettings accessibility(AccessibilitySelector accessibilitySelector) {
        PlaywrightCheckSettings clone = this.clone();
        clone.accessibilityRegions.add(accessibilitySelector);
        return clone;
    }

    /** internal */
    private PlaywrightCheckSettings accessibility(AccessibilityElement accessibilityElement) {
        PlaywrightCheckSettings clone = this.clone();
        clone.accessibilityRegions.add(accessibilityElement);
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings accessibility(Region region, AccessibilityRegionType regionType) {
        PlaywrightCheckSettings clone = this.clone();
        clone.accessibility_(region, regionType);
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void accessibility_(Region rect, AccessibilityRegionType regionType) {
        this.accessibilityRegions.add(new AccessibilityRegionByRectangle(rect, regionType));
    }

    /**
     * Specify the scroll root element for the current target.
     *
     * @param selector  the css or xpath selector
     * @return an updated clone of this settings object
     */
    public PlaywrightCheckSettings scrollRootElement(@Nonnull String selector) {
        return scrollRootElement(new Selector(selector));
    }

    /**
     * Specify the scroll root element for the current target.
     *
     * @param locator  the locator
     * @return an updated clone of this settings object
     */
    public PlaywrightCheckSettings scrollRootElement(@Nonnull Locator locator) {
        return scrollRootElement(new Selector(locator));
    }

    /**
     * Specify the scroll root element for the current target.
     *
     * @param element  the element
     * @return an updated clone of this settings object
     */
    public PlaywrightCheckSettings scrollRootElement(@Nonnull ElementHandle element) {
        return scrollRootElement(new Element(element));
    }

    /** internal */
    private PlaywrightCheckSettings scrollRootElement(Selector selector) {
        PlaywrightCheckSettings clone = this.clone();
        if (frameChain.size() == 0) {
            clone.scrollRootElement = selector;
        } else {
            frameChain.get(frameChain.size() - 1).setScrollRootSelector(selector);
        }
        return clone;
    }

    /** internal */
    private PlaywrightCheckSettings scrollRootElement(Element element) {
        PlaywrightCheckSettings clone = this.clone();
        if (frameChain.size() == 0) {
            clone.scrollRootElement = element;
        } else {
            frameChain.get(frameChain.size() - 1).setScrollRootElement(element);
        }
        return clone;
    }

    /**
     * Enable and set the layout breakpoints for the current target.
     *
     * @param breakpoints  one or more viewport widths explicitly
     * @return an updated clone of this settings object
     */
    public PlaywrightCheckSettings layoutBreakpoints(int... breakpoints) {
        return setLayoutBreakpoints(breakpoints);
    }

    /**
     * Enable layout breakpoints for the current target.
     *
     * @param shouldSet  should enable this feature
     * @return an updated clone of this settings object
     */
    public PlaywrightCheckSettings layoutBreakpoints(Boolean shouldSet) {
        return setLayoutBreakpoints(shouldSet);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings setLayoutBreakpoints(int... breakpoints) {
        PlaywrightCheckSettings clone = this.clone();
        clone.isDefaultLayoutBreakpointsSet = false;
        clone.layoutBreakpoints.clear();
        if (breakpoints == null || breakpoints.length == 0) {
            return this;
        }

        for (int breakpoint : breakpoints) {
            ArgumentGuard.greaterThanZero(breakpoint, "breakpoint");
            clone.layoutBreakpoints.add(breakpoint);
        }

        Collections.sort(clone.layoutBreakpoints);
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings setLayoutBreakpoints(Boolean shouldSet) {
        PlaywrightCheckSettings clone = this.clone();
        clone.isDefaultLayoutBreakpointsSet = shouldSet;
        clone.layoutBreakpoints.clear();
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getLayoutBreakpoints() {
        return layoutBreakpoints;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isDefaultLayoutBreakpointsSet() {
        return isDefaultLayoutBreakpointsSet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings fully() {
        return fully(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings fully(Boolean fully) {
        return (PlaywrightCheckSettings) super.fully(fully);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings withName(String name) {
        return (PlaywrightCheckSettings) super.withName(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings ignoreCaret(Boolean ignoreCaret) {
        return (PlaywrightCheckSettings) super.ignoreCaret(ignoreCaret);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings sendDom(Boolean sendDom) {
        return (PlaywrightCheckSettings) super.sendDom(sendDom);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings sendDom() {
        return (PlaywrightCheckSettings) super.sendDom();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings enablePatterns(Boolean enablePatterns) {
        return (PlaywrightCheckSettings) super.enablePatterns(enablePatterns);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings enablePatterns() {
        return (PlaywrightCheckSettings) super.enablePatterns();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings beforeRenderScreenshotHook(String hook) {
        return (PlaywrightCheckSettings) super.beforeRenderScreenshotHook(hook);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings timeout(Integer timeoutMilliseconds) {
        return (PlaywrightCheckSettings) super.timeout(timeoutMilliseconds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings useDom(Boolean useDom) {
        return (PlaywrightCheckSettings) super.useDom(useDom);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings variationGroupId(String variationGroupId) {
        return (PlaywrightCheckSettings) super.variationGroupId(variationGroupId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings visualGridOptions(VisualGridOption... options) {
        return (PlaywrightCheckSettings) super.visualGridOptions(options);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings waitBeforeCapture(Integer milliSec) {
        return (PlaywrightCheckSettings) super.waitBeforeCapture(milliSec);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings setDisableBrowserFetching(Boolean disableBrowserFetching) {
        return (PlaywrightCheckSettings) super.setDisableBrowserFetching(disableBrowserFetching);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings autProxy(AutProxySettings autProxy) {
        return (PlaywrightCheckSettings) super.autProxy(autProxy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings setAccessibilityValidation(AccessibilitySettings accessibilitySettings) {
        return (PlaywrightCheckSettings) super.setAccessibilityValidation(accessibilitySettings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings stitchMode(StitchMode stitchMode) {
        return (PlaywrightCheckSettings) super.stitchMode(stitchMode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings setHideScrollBars(Boolean hideScrollBars) {
        return (PlaywrightCheckSettings) super.setHideScrollBars(hideScrollBars);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings setHideCaret(Boolean hideCaret) {
        return (PlaywrightCheckSettings) super.setHideCaret(hideCaret);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings setOverlap(Integer overlap) {
        return (PlaywrightCheckSettings) super.setOverlap(overlap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings pageId(String pageId) {
        return (PlaywrightCheckSettings) super.pageId(pageId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings ignoreDisplacements(Boolean ignoreDisplacements) {
        return (PlaywrightCheckSettings) super.ignoreDisplacements(ignoreDisplacements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings ignoreDisplacements() {
        return (PlaywrightCheckSettings) super.ignoreDisplacements();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings ignoreCaret() {
        return (PlaywrightCheckSettings) super.ignoreCaret();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings matchLevel(MatchLevel matchLevel) {
        return (PlaywrightCheckSettings) super.matchLevel(matchLevel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings strict() {
        return (PlaywrightCheckSettings) super.strict();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings layout() {
        return (PlaywrightCheckSettings) super.layout();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings exact() {
        return (PlaywrightCheckSettings) super.exact();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings lazyLoad() {
        return (PlaywrightCheckSettings) super.lazyLoad();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaywrightCheckSettings lazyLoad(LazyLoadOptions lazyLoadOptions) {
        return (PlaywrightCheckSettings) super.lazyLoad(lazyLoadOptions);
    }

    /**
     * Create a new clone of this settings object.
     *
     * @return  the clone of this settings object
     */
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
        return clone;
    }

    /** internal */
    public Reference getTargetElement() {
        return targetElement;
    }

    /** internal */
    public Reference getScrollRootElement() {
        return scrollRootElement;
    }

    /** internal */
    public List<FrameLocator> getFrameChain() {
        return frameChain;
    }

    /** internal */
    @Override
    public LazyLoadOptions getLazyLoadOptions() {
        return super.getLazyLoadOptions();
    }

    /** internal */
    @Override
    public GetRegion[] getIgnoreRegions() {
        return ignoreRegions.toArray(new GetRegion[0]);
    }

    /** internal */
    @Override
    public GetRegion[] getLayoutRegions() {
        return layoutRegions.toArray(new GetRegion[0]);
    }

    /** internal */
    @Override
    public GetRegion[] getStrictRegions() {
        return strictRegions.toArray(new GetRegion[0]);
    }

    /** internal */
    @Override
    public GetRegion[] getContentRegions() {
        return contentRegions.toArray(new GetRegion[0]);
    }

    /** internal */
    @Override
    public GetRegion[] getFloatingRegions() {
        return floatingRegions.toArray(new GetRegion[0]);
    }

    /** internal */
    @Override
    public GetRegion[] getAccessibilityRegions() {
        return accessibilityRegions.toArray(new GetRegion[0]);
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    @Override
    public PlaywrightCheckSettings content() {
        return (PlaywrightCheckSettings) super.content();
    }

    /** see {@link #ignoreColors(String)} */
    @Deprecated
    public PlaywrightCheckSettings content(String selector) {
        return ignoreColors(selector);
    }

    /** see {@link #ignoreColors(Locator)} */
    @Deprecated
    public PlaywrightCheckSettings content(Locator locator) {
        return ignoreColors(locator.elementHandle());
    }

    /** see {@link #ignoreColors(ElementHandle)} */
    @Deprecated
    public PlaywrightCheckSettings content(ElementHandle element) {
        return ignoreColors(element);
    }

    /** see {@link #ignoreColors(String, String)} */
    @Deprecated
    public PlaywrightCheckSettings content(String selector, String regionId) {
        return ignoreColors(selector, regionId);
    }

    /** see {@link #ignoreColors(Locator, String)} */
    @Deprecated
    public PlaywrightCheckSettings content(Locator locator, String regionId) {
        return ignoreColors(locator, regionId);
    }

    /** see {@link #ignoreColors(ElementHandle, String)} */
    @Deprecated
    public PlaywrightCheckSettings content(ElementHandle element, String regionId) {
        return ignoreColors(element, regionId);
    }

    /** see {@link #ignoreColors(String, Padding)} */
    @Deprecated
    public PlaywrightCheckSettings content(String selector, Padding padding) {
        return ignoreColors(selector, padding);
    }

    /** see {@link #ignoreColors(Locator, Padding)} */
    @Deprecated
    public PlaywrightCheckSettings content(Locator locator, Padding padding) {
        return ignoreColors(locator.elementHandle(), padding);
    }

    /** see {@link #ignoreColors(Locator, Padding)} */
    @Deprecated
    public PlaywrightCheckSettings content(ElementHandle element, Padding padding) {
        return ignoreColors(element, padding);
    }
    
    /** see {@link #ignoreColors(Region[])} )} */
    @Deprecated
    @Override
    public PlaywrightCheckSettings content(Region[] regions) {
        return ignoreColors(regions);
    }
    /** see {@link #ignoreColors(Region, Region...)} )} )} */
    @Deprecated
    @Override
    public PlaywrightCheckSettings content(Region region, Region... regions) {
        return ignoreColors(region, regions);
    }

}
