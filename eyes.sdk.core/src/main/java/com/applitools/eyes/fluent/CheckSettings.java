package com.applitools.eyes.fluent;

import com.applitools.eyes.FloatingMatchSettings;
import com.applitools.eyes.MatchLevel;
import com.applitools.eyes.Region;

import java.util.ArrayList;
import java.util.List;

/**
 * The Match settings object to use in the various Eyes.Check methods.
 */
public class CheckSettings implements ICheckSettings, ICheckSettingsInternal {

    private Region targetRegion;
    private List<GetRegion> ignoreRegions = new ArrayList<>();
    private MatchLevel matchLevel;
    private Boolean ignoreCaret;
    private boolean stitchContent = false;
    private List<GetFloatingRegion> floatingRegions = new ArrayList<>();
    private int timeout = -1;

    protected CheckSettings() {
    }

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

    protected void ignore(Region region) {
        this.ignore(new IgnoreRegionByRectangle(region));
    }

    protected void ignore(GetRegion regionProvider) {
        ignoreRegions.add(regionProvider);
    }

    protected void floating_(Region region, int maxUpOffset, int maxDownOffset, int maxLeftOffset, int maxRightOffset) {
        this.floatingRegions.add(
                new FloatingRegionByRectangle(
                        new Region(
                                region.getLeft(),
                                region.getTop(),
                                region.getLeft() + region.getWidth(),
                                region.getTop() + region.getHeight()
                        ), maxUpOffset, maxDownOffset, maxLeftOffset, maxRightOffset)
        );
    }

    protected void floating(GetFloatingRegion regionProvider){
        this.floatingRegions.add(regionProvider);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings ignore(Region... regions) {
        for (Region r : regions) {
            ignore(r);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings fully() {
        this.stitchContent = true;
        return this;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings floating(int maxOffset, Region... regions) {
        for (Region r : regions) {
            this.floating_(r, maxOffset, maxOffset, maxOffset, maxOffset);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings floating(Region region, int maxUpOffset, int maxDownOffset, int maxLeftOffset, int maxRightOffset) {
        this.floating_(region, maxUpOffset, maxDownOffset, maxLeftOffset, maxRightOffset);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings timeout(int timeoutMilliseconds) {
        this.timeout = timeoutMilliseconds;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings layout() {
        this.matchLevel = MatchLevel.LAYOUT;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings exact() {
        this.matchLevel = MatchLevel.EXACT;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings strict() {
        this.matchLevel = MatchLevel.STRICT;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings content() {
        this.matchLevel = MatchLevel.CONTENT;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings matchLevel(MatchLevel matchLevel) {
        this.matchLevel = matchLevel;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings ignoreCaret(boolean ignoreCaret) {
        this.ignoreCaret = ignoreCaret;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings ignoreCaret() {
        this.ignoreCaret = true;
        return this;
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
    public boolean getStitchContent() {
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
    public GetFloatingRegion[] getFloatingRegions() {
        return this.floatingRegions.toArray(new GetFloatingRegion[0]);
    }

    @Override
    public Boolean getIgnoreCaret() {
        return this.ignoreCaret;
    }

    protected void updateTargetRegion(Region region) {
        this.targetRegion = region;
    }
}
