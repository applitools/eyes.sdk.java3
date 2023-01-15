package com.applitools.eyes.fluent;

import com.applitools.ICheckSettings;
import com.applitools.eyes.*;
import com.applitools.eyes.locators.BaseOcrRegion;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.eyes.selenium.StitchMode;
import com.applitools.eyes.visualgrid.model.VisualGridOption;
import com.applitools.eyes.visualgrid.model.VisualGridSelector;
import com.applitools.utils.ArgumentGuard;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CheckSettings implements ICheckSettings, ICheckSettingsInternal {
    // For Rendering Grid
    protected static final String BEFORE_CAPTURE_SCREENSHOT = "beforeCaptureScreenshot";

    // screenshot
    private Region targetRegion;
    private Boolean stitchContent = null;
    private StitchMode stitchMode = StitchMode.CSS; // "CSS" | "Scroll"
    private Boolean hideScrollBars;
    private Boolean hideCaret;
    private Integer overlap;
    private Integer waitBeforeCapture;
    private LazyLoadOptions lazyLoadOptions;
    protected Boolean ignoreDisplacements;
    private Map<String, String> debugImages;

    // check
    protected String name;
    protected String pageId;
    protected final List<GetRegion> ignoreRegions = new ArrayList<>();
    protected final List<GetRegion> layoutRegions = new ArrayList<>();
    protected final List<GetRegion> strictRegions = new ArrayList<>();
    protected final List<GetRegion> contentRegions = new ArrayList<>();
    protected final List<GetRegion> floatingRegions = new ArrayList<>();
    protected List<GetRegion> accessibilityRegions = new ArrayList<>();
    private MatchLevel matchLevel = null;
    private AccessibilitySettings accessibilitySettings;
    private Integer retryTimeout = null; // former timeout/matchTimeout
    protected Boolean sendDom;
    protected Boolean useDom;
    protected Boolean enablePatterns;
    private Boolean ignoreCaret;
    private List<VisualGridOption> ufgOptions = new ArrayList<>();
    private Boolean isDefaultLayoutBreakpointsSet;
    private final List<Integer> layoutBreakpoints = new ArrayList<>();
    private Boolean disableBrowserFetching;
    private AutProxySettings autProxy;
    protected Map<String, String> scriptHooks = new HashMap<>();

    private BaseOcrRegion ocrRegion = null;
    private String variationGroupId = null;

    protected CheckSettings() { }

    protected CheckSettings(Region region) {
        this.targetRegion = region;
    }

    /**
     * For internal use only.
     * @param retryTimeout timeout
     */
    public CheckSettings(Integer retryTimeout) {
        this.retryTimeout = retryTimeout;
    }

    protected void ignore_(Region region) {
        this.ignore_(new SimpleRegionByRectangle(region));
    }

    protected void ignore_(GetSimpleRegion regionProvider) {
        ignoreRegions.add(regionProvider);
    }

    protected void layout_(Region region) {
        this.layout_(new SimpleRegionByRectangle(region));
    }

    protected void layout_(GetSimpleRegion regionProvider) {
        layoutRegions.add(regionProvider);
    }

    protected void content_(Region region) {
        this.content_(new SimpleRegionByRectangle(region));
    }

    protected void content_(GetSimpleRegion regionProvider) {
        contentRegions.add(regionProvider);
    }

    protected void strict_(Region region) {
        this.strict_(new SimpleRegionByRectangle(region));
    }

    protected void strict_(GetSimpleRegion regionProvider) {
        strictRegions.add(regionProvider);
    }

    protected void floating_(Region region, int maxUpOffset, int maxDownOffset, int maxLeftOffset, int maxRightOffset) {
        this.floatingRegions.add(
                new FloatingRegionByRectangle(region, maxUpOffset, maxDownOffset, maxLeftOffset, maxRightOffset)
        );
    }

    protected void floating(GetFloatingRegion regionProvider){
        this.floatingRegions.add(regionProvider);
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public CheckSettings clone(){
        CheckSettings clone = new CheckSettings();
        populateClone(clone);
        return clone;
    }

    @Override
    public Boolean isStitchContent() {
        return stitchContent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings ignore(Region region, Region... regions) {
        CheckSettings clone = clone();
        clone.ignore_(region);
        for (Region r : regions) {
            clone.ignore_(r);
        }
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings ignore(Region[] regions) {
        CheckSettings clone = clone();
        for (Region r : regions) {
            clone.ignore_(r);
        }
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings layout(Region region, Region... regions) {
        CheckSettings clone = clone();
        clone.layout_(region);
        for (Region r : regions) {
            clone.layout_(r);
        }
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings layout(Region[] regions) {
        CheckSettings clone = clone();
        for (Region r : regions) {
            clone.layout_(r);
        }
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings strict(Region region, Region... regions) {
        CheckSettings clone = clone();
        clone.strict_(region);
        for (Region r : regions) {
            clone.strict_(r);
        }
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings strict(Region[] regions) {
        CheckSettings clone = clone();
        for (Region r : regions) {
            clone.strict_(r);
        }
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings content(Region region, Region... regions) {
        CheckSettings clone = clone();
        clone.content_(region);
        for (Region r : regions) {
            clone.content_(r);
        }
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings content(Region[] regions) {
        CheckSettings clone = clone();
        for (Region r : regions) {
            clone.content_(r);
        }
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings fully() {
        CheckSettings clone = clone();
        clone.stitchContent = true;
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings fully(Boolean fully) {
        CheckSettings clone = clone();
        clone.stitchContent = fully;
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings floating(int maxOffset, Region... regions) {
        CheckSettings clone = clone();
        for (Region r : regions) {
            clone.floating_(r, maxOffset, maxOffset, maxOffset, maxOffset);
        }
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings floating(Region region, int maxUpOffset, int maxDownOffset, int maxLeftOffset, int maxRightOffset) {
        CheckSettings clone = clone();
        clone.floating_(region, maxUpOffset, maxDownOffset, maxLeftOffset, maxRightOffset);
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings timeout(Integer timeoutMilliseconds) {
        CheckSettings clone = clone();
        clone.retryTimeout = timeoutMilliseconds;
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings layout() {
        CheckSettings clone = clone();
        clone.matchLevel = MatchLevel.LAYOUT;
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings exact() {
        CheckSettings clone = clone();
        clone.matchLevel = MatchLevel.EXACT;
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings strict() {
        CheckSettings clone = clone();
        clone.matchLevel = MatchLevel.STRICT;
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings content() {
        CheckSettings clone = clone();
        clone.matchLevel = MatchLevel.IGNORE_COLORS;
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings matchLevel(MatchLevel matchLevel) {
        CheckSettings clone = clone();
        clone.matchLevel = matchLevel;
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings ignoreCaret(Boolean ignoreCaret) {
        CheckSettings clone = clone();
        clone.ignoreCaret = ignoreCaret;
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings ignoreCaret() {
        CheckSettings clone = clone();
        clone.ignoreCaret = true;
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    public ICheckSettings withName(String name) {
        CheckSettings clone = clone();
        clone.name = name;
        return clone;
    }

    @Override
    public Boolean isSendDom() {
        return sendDom;
    }

    @Override
    public Boolean isIgnoreDisplacements() {
        return ignoreDisplacements;
    }

    @Override
    public Boolean isUseDom() {
        return useDom;
    }

    @Override
    public ICheckSettings useDom(Boolean useDom) {
        CheckSettings clone = this.clone();
        clone.useDom = useDom;
        return clone;
    }


    @Override
    public ICheckSettings sendDom(Boolean sendDom) {
        CheckSettings clone = this.clone();
        clone.sendDom = sendDom;
        return clone;
    }

    @Override
    public ICheckSettings sendDom() {
        return sendDom(true);
    }

    @Override
    public Region getTargetRegion() {
        return this.targetRegion;
    }

    @Override
    public Integer getTimeout() {
        return this.retryTimeout;
    }

    @Override
    public Boolean getStitchContent() {
        return this.stitchContent;
    }

    @Override
    public MatchLevel getMatchLevel() {
        return this.matchLevel;
    }

    @Override
    public GetRegion[] getIgnoreRegions() {
        return this.ignoreRegions.toArray(new GetRegion[0]);
    }

    @Override
    public GetRegion[] getStrictRegions() {
        return this.strictRegions.toArray(new GetRegion[0]);
    }


    @Override
    public GetRegion[] getLayoutRegions() {
        return this.layoutRegions.toArray(new GetRegion[0]);
    }


    @Override
    public GetRegion[] getContentRegions() {
        return this.contentRegions.toArray(new GetRegion[0]);
    }


    @Override
    public GetRegion[] getFloatingRegions() {
        return this.floatingRegions.toArray(new GetRegion[0]);
    }

    @Override
    public Boolean getIgnoreCaret() {
        return this.ignoreCaret;
    }

    @Override
    public String getName(){
        return this.name;
    }


    @Override
    public Map<String, String> getScriptHooks() {
        return scriptHooks;
    }

    @Override
    public String getSizeMode() {
        return null;
    }

    protected void updateTargetRegion(Region region) {
        this.targetRegion = region;
    }

    protected void populateClone(CheckSettings clone) {
        clone.targetRegion = this.targetRegion;
        clone.matchLevel = this.matchLevel;
        clone.stitchContent = this.stitchContent;
        clone.retryTimeout = this.retryTimeout;
        clone.ignoreCaret = this.ignoreCaret;
        clone.name = this.name;
        clone.pageId = this.pageId;
        clone.ignoreRegions.addAll(this.ignoreRegions);
        clone.contentRegions.addAll(this.contentRegions);
        clone.layoutRegions.addAll(this.layoutRegions);
        clone.strictRegions.addAll(this.strictRegions);
        clone.floatingRegions.addAll(this.floatingRegions);
        clone.scriptHooks.putAll(this.scriptHooks);
        clone.enablePatterns = (this.enablePatterns);
        clone.ignoreDisplacements = (this.ignoreDisplacements);
        clone.accessibilityRegions = this.accessibilityRegions;
        clone.useDom = (this.useDom);
        clone.ufgOptions = this.ufgOptions;
        clone.ocrRegion = this.ocrRegion;
        clone.variationGroupId = this.variationGroupId;
        clone.disableBrowserFetching = this.disableBrowserFetching;
        clone.waitBeforeCapture = this.waitBeforeCapture;
        clone.lazyLoadOptions = this.lazyLoadOptions;
    }

    public void setStitchContent(boolean stitchContent) {
        this.stitchContent = stitchContent;
    }

    @Override
    public Boolean isEnablePatterns() {
        return enablePatterns;
    }

    @Override
    public VisualGridSelector getVGTargetSelector() {
        return null;
    }

    @Override
    public ICheckSettings enablePatterns(Boolean enablePatterns) {
        CheckSettings clone = this.clone();
        clone.enablePatterns = enablePatterns;
        return clone;
    }

    @Override
    public ICheckSettings enablePatterns() {
        CheckSettings clone = this.clone();
        clone.enablePatterns = true;
        return clone;
    }

    @Deprecated
    @Override
    public ICheckSettings scriptHook(String hook) {
        return beforeRenderScreenshotHook(hook);
    }

    @Override
    public ICheckSettings beforeRenderScreenshotHook(String hook) {
        CheckSettings clone = this.clone();
        clone.scriptHooks.put(BEFORE_CAPTURE_SCREENSHOT, hook);
        return clone;
    }

    @Override
    public ICheckSettings ignoreDisplacements(Boolean ignoreDisplacements) {
        CheckSettings clone = this.clone();
        clone.ignoreDisplacements = ignoreDisplacements;
        return clone;
    }

    @Override
    public ICheckSettings ignoreDisplacements() {
        return this.ignoreDisplacements(true);
    }

    protected void accessibility_(GetAccessibilityRegion accessibilityRegionProvider)
    {
        this.accessibilityRegions.add(accessibilityRegionProvider);
    }

    protected void accessibility_(Region rect, AccessibilityRegionType regionType)
    {
        accessibility_(new AccessibilityRegionByRectangle(rect, regionType));
    }


    @Override
    public ICheckSettings accessibility(Region region, AccessibilityRegionType regionType) {
        CheckSettings clone = clone();
        clone.accessibility_(region, regionType);
        return clone;
    }

    protected void accessibility(GetAccessibilityRegion accessibilityRegionProvider) {
        accessibilityRegions.add(accessibilityRegionProvider);
    }

    @Override
    public GetRegion[] getAccessibilityRegions() {
        return this.accessibilityRegions.toArray(new GetRegion[0]);
    }

    public ICheckSettings ocrRegion(BaseOcrRegion ocrRegion) {
        CheckSettings clone = clone();
        clone.ocrRegion = ocrRegion;
        return clone;
    }

    @Override
    public BaseOcrRegion getOcrRegion() {
        return ocrRegion;
    }

    public ICheckSettings variationGroupId(String variationGroupId) {
        CheckSettings clone = clone();
        clone.variationGroupId = variationGroupId;
        return clone;
    }

    @Override
    public String getVariationGroupId() {
        return variationGroupId;
    }

    /**
     * Internal Usage
     */
    @JsonIgnore
    @Override
    public PositionProvider getStepPositionProvider() {
        return null;
    }

    @Override
    public ICheckSettings visualGridOptions(VisualGridOption... options) {
        CheckSettings clone = clone();
        clone.ufgOptions.clear();
        if (options != null) {
            clone.ufgOptions.addAll(Arrays.asList(options));
            clone.ufgOptions.remove(null);
        }

        return clone;
    }

    @Override
    public Boolean isCheckWindow() {
        return getTargetRegion() == null && getVGTargetSelector() == null;
    }

    @Override
    public ICheckSettings waitBeforeCapture(Integer milliSec) {
        CheckSettings clone = clone();
        clone.waitBeforeCapture = milliSec;
        return clone;
    }

    public List<VisualGridOption> getVisualGridOptions() {
        return ufgOptions;
    }

    public Boolean isDisableBrowserFetching() {
        return disableBrowserFetching;
    }

    public ICheckSettings setDisableBrowserFetching(Boolean disableBrowserFetching) {
        CheckSettings clone = this.clone();
        clone.disableBrowserFetching = disableBrowserFetching;
        return clone;
    }

    public Integer getWaitBeforeCapture() {
        return waitBeforeCapture;
    }

    public ICheckSettings lazyLoad() {
        CheckSettings clone = this.clone();
        clone.lazyLoadOptions = new LazyLoadOptions();
        return clone;
    }

    public ICheckSettings lazyLoad(LazyLoadOptions lazyLoadOptions) {
        CheckSettings clone = this.clone();
        clone.lazyLoadOptions = lazyLoadOptions;
        return clone;
    }

    public LazyLoadOptions getLazyLoadOptions() {
        return this.lazyLoadOptions;
    }

    // added in v3

    public AutProxySettings getAutProxy() {
        return autProxy;
    }

    public ICheckSettings autProxy(AutProxySettings autProxy) {
        CheckSettings clone = this.clone();
        clone.autProxy = autProxy;
        return clone;
    }

    public AccessibilitySettings getAccessibilityValidation() {
        return accessibilitySettings;
    }

    public ICheckSettings setAccessibilityValidation(AccessibilitySettings accessibilitySettings) {
        if (accessibilitySettings == null) {
            return this;
        }

        // TODO do we need such validation here?
        if (accessibilitySettings.getLevel() == null || accessibilitySettings.getGuidelinesVersion() == null) {
            throw new IllegalArgumentException("AccessibilitySettings should have the following properties: ‘level,version’");
        }

        CheckSettings clone = this.clone();
//        this.defaultMatchSettings.setAccessibilitySettings(accessibilitySettings);
        clone.accessibilitySettings = accessibilitySettings;
        return clone;
    }

    public StitchMode getStitchMode() {
        return stitchMode;
    }

    public ICheckSettings stitchMode(StitchMode stitchMode) {
        CheckSettings clone = this.clone();
        clone.stitchMode = stitchMode;
        return clone;
    }

    public Boolean getHideScrollBars() {
        return hideScrollBars;
    }

    public ICheckSettings setHideScrollBars(Boolean hideScrollBars) {
        CheckSettings clone = this.clone();
        clone.hideScrollBars = hideScrollBars;
        return clone;
    }

    public Boolean getHideCaret() {
        return hideCaret;
    }

    public ICheckSettings setHideCaret(Boolean hideCaret) {
        CheckSettings clone = this.clone();
        clone.hideCaret = hideCaret;
        return clone;
    }

    public Integer getOverlap() {
        return overlap;
    }

    public ICheckSettings setOverlap(Integer overlap) {
        CheckSettings clone = this.clone();
        clone.overlap = overlap;
        return clone;
    }

    public ICheckSettings setLayoutBreakpoints(Boolean shouldSet) {
        CheckSettings clone = this.clone();
        clone.isDefaultLayoutBreakpointsSet = shouldSet;
        clone.layoutBreakpoints.clear();
        return clone;
    }

    public Boolean isDefaultLayoutBreakpointsSet() {
        return isDefaultLayoutBreakpointsSet;
    }

    public ICheckSettings setLayoutBreakpoints(int... breakpoints) {
        CheckSettings clone = this.clone();
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

    public List<Integer> getLayoutBreakpoints() {
        return layoutBreakpoints;
    }

    public ICheckSettings pageId(String pageId) {
        CheckSettings clone = this.clone();
        clone.pageId = pageId;
        return clone;
    }

    public String getPageId() {
        return this.pageId;
    }
}
