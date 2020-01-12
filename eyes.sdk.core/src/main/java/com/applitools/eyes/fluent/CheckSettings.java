package com.applitools.eyes.fluent;

import com.applitools.ICheckSettings;
import com.applitools.eyes.*;
import com.applitools.eyes.visualgrid.model.VisualGridSelector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Match settings object to use in the various Eyes.Check methods.
 */
public class CheckSettings implements ICheckSettings, ICheckSettingsInternal {
    // For Rendering Grid
    protected static final String BEFORE_CAPTURE_SCREENSHOT = "beforeCaptureScreenshot";

    private Region targetRegion;
    private MatchLevel matchLevel = null;
    private Boolean ignoreCaret = null;
    private Boolean stitchContent = null;
    private List<GetRegion> ignoreRegions = new ArrayList<>();
    private List<GetRegion> layoutRegions = new ArrayList<>();
    private List<GetRegion> strictRegions = new ArrayList<>();
    private List<GetRegion> contentRegions = new ArrayList<>();
    private List<GetFloatingRegion> floatingRegions = new ArrayList<>();
    private int timeout = -1;
    protected String name;
    protected Boolean enablePatterns;
    protected Boolean sendDom = null;
    protected Boolean useDom;
    protected Map<String, String> scriptHooks = new HashMap<>();
    protected Boolean ignoreDisplacements;
    protected List<IGetAccessibilityRegion> accessibilityRegions = new ArrayList<>();

    protected CheckSettings() { }

    protected CheckSettings(Region region) {
        this.targetRegion = region;
    }

    /**
     * For internal use only.
     * @param timeout timeout
     */
    public CheckSettings(int timeout) {
        this.timeout = timeout;
    }

    protected void ignore_(Region region) {
        this.ignore_(new SimpleRegionByRectangle(region));
    }

    protected void ignore_(GetRegion regionProvider) {
        ignoreRegions.add(regionProvider);
    }

    protected void layout_(Region region) {
        this.layout_(new SimpleRegionByRectangle(region));
    }

    protected void layout_(GetRegion regionProvider) {
        layoutRegions.add(regionProvider);
    }

    protected void content_(Region region) {
        this.content_(new SimpleRegionByRectangle(region));
    }

    protected void content_(GetRegion regionProvider) {
        contentRegions.add(regionProvider);
    }

    protected void strict_(Region region) {
        this.strict_(new SimpleRegionByRectangle(region));
    }

    protected void strict_(GetRegion regionProvider) {
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
    public ICheckSettings timeout(int timeoutMilliseconds) {
        CheckSettings clone = clone();
        clone.timeout = timeoutMilliseconds;
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
        clone.matchLevel = MatchLevel.CONTENT;
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
    public ICheckSettings ignoreCaret(boolean ignoreCaret) {
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
        return useDom != null ? useDom : Boolean.FALSE;
    }

    @Override
    public ICheckSettings useDom(boolean useDom) {
        CheckSettings clone = this.clone();
        clone.useDom = useDom;
        return clone;
    }


    @Override
    public ICheckSettings sendDom(boolean sendDom) {
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
    public int getTimeout() {
        return this.timeout;
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
    public GetFloatingRegion[] getFloatingRegions() {
        return this.floatingRegions.toArray(new GetFloatingRegion[0]);
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
        clone.timeout = this.timeout;
        clone.ignoreCaret = this.ignoreCaret;
        clone.name = this.name;

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
    }

    public void setStitchContent(boolean stitchContent) {
        this.stitchContent = stitchContent;
    }

    @Override
    public boolean isEnablePatterns() {
        return enablePatterns != null ? enablePatterns : false;
    }

    @Override
    public VisualGridSelector getVGTargetSelector() {
        return null;
    }

    @Override
    public ICheckSettings enablePatterns(boolean enablePatterns) {
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
        ICheckSettings clone = this.clone();
        ((CheckSettings) clone).scriptHooks.put(BEFORE_CAPTURE_SCREENSHOT, hook);
        return clone;
    }

    @Override
    public ICheckSettings ignoreDisplacements(boolean ignoreDisplacements) {
        ICheckSettings clone = this.clone();
        ((CheckSettings) clone).ignoreDisplacements = ignoreDisplacements;
        return clone;
    }

    @Override
    public ICheckSettings ignoreDisplacements() {
        return this.ignoreDisplacements(true);
    }

    protected void accessibility_(IGetAccessibilityRegion accessibilityRegionProvider)
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

    protected void accessibility(IGetAccessibilityRegion accessibilityRegionProvider)
    {
        accessibilityRegions.add(accessibilityRegionProvider);
    }

    @Override
    public IGetAccessibilityRegion[] getAccessibilityRegions() {
        return this.accessibilityRegions.toArray(new IGetAccessibilityRegion[0]);
    }
}
