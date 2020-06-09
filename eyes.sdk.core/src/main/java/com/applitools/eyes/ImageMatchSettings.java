package com.applitools.eyes;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Encapsulates match settings for the a session.
 */
public class ImageMatchSettings {
    private MatchLevel matchLevel;
    private ExactMatchSettings exact;
    private boolean ignoreCaret;
    private Region[] ignoreRegions;
    private Region[] layoutRegions;
    private Region[] strictRegions;
    private Region[] contentRegions;
    private FloatingMatchSettings[] floatingMatchSettings;
    private boolean useDom;
    public boolean enablePatterns;
    public boolean ignoreDisplacements;
    private AccessibilityRegionByRectangle[] accessibility = new AccessibilityRegionByRectangle[0];
    private AccessibilitySettings accessibilitySettings;

    public ImageMatchSettings(MatchLevel matchLevel, ExactMatchSettings exact, boolean useDom) {
        this.matchLevel = matchLevel;
        this.exact = exact;
        this.ignoreCaret = false;
        this.useDom = useDom;
    }

    /**
     * See {@link #ImageMatchSettings(MatchLevel, ExactMatchSettings, boolean)}.
     * {@code matchLevel} defaults to {@code STRICT},
     * {@code exact} defaults to {@code null}.
     */
    public ImageMatchSettings() {
        this(MatchLevel.STRICT, null, false);
    }

    public ImageMatchSettings(ImageMatchSettings other) {
        if (other != null) {
            this.matchLevel = other.matchLevel;
            this.exact = other.exact == null ? null : new ExactMatchSettings(other.exact);
            this.ignoreCaret = other.ignoreCaret;
            this.ignoreRegions = other.ignoreRegions;
            this.layoutRegions = other.layoutRegions;
            this.strictRegions = other.strictRegions;
            this.contentRegions = other.contentRegions;
            this.floatingMatchSettings = other.floatingMatchSettings;
            this.useDom = other.useDom;
            this.enablePatterns = other.enablePatterns;
            this.ignoreDisplacements = other.ignoreDisplacements;
            this.accessibility = other.accessibility;
            this.accessibilitySettings = other.accessibilitySettings;
        } else {
            this.matchLevel = MatchLevel.STRICT;
        }
    }

    /**
     *
     * @return The "strictness" level of the match.
     */
    public MatchLevel getMatchLevel() {
        return matchLevel;
    }

    /**
     *
     * @param matchLevel The "strictness" level of the match.
     */
    public void setMatchLevel(MatchLevel matchLevel) {
        this.matchLevel = matchLevel;
    }

    /**
     *
     * @return The parameters for the "Exact" match settings.
     */
    public ExactMatchSettings getExact() {
        return exact;
    }

    /**
     *
     * @param exact The parameters for the "exact" match settings.
     */
    public void setExact(ExactMatchSettings exact) {
        this.exact = exact;
    }

    /**
     *
     * @return The parameters for the "IgnoreCaret" match settings.
     */
    public Boolean getIgnoreCaret() {
        return ignoreCaret;
    }

    public boolean isUseDom() {
        return useDom;
    }

    public void setUseDom(boolean useDom) {
        this.useDom = useDom;
    }

    /**
     * Sets an array of regions to ignore.
     * @param ignoreRegions The array of regions to ignore.
     */
    @JsonSetter("Ignore")
    public void setIgnoreRegions(Region[] ignoreRegions) {
        this.ignoreRegions = ignoreRegions;
    }

    /**
     * Sets an array of regions to check using the Layout method.
     * @param layoutRegions The array of regions to ignore.
     */
    @JsonSetter("Layout")
    public void setLayoutRegions(Region[] layoutRegions) {
        this.layoutRegions = layoutRegions;
    }

    /**
     * Sets an array of regions to check using the Strict method.
     * @param strictRegions The array of regions to ignore.
     */
    @JsonSetter("Strict")
    public void setStrictRegions(Region[] strictRegions) {
        this.strictRegions = strictRegions;
    }

    /**
     * Sets an array of regions to check using the Content method.
     * @param contentRegions The array of regions to ignore.
     */

    @JsonSetter("Content")
    public void setContentRegions(Region[] contentRegions) {
        this.contentRegions = contentRegions;
    }

    /**
     * Returns the array of regions to ignore.
     * @return the array of regions to ignore.
     */
    @JsonGetter("Ignore")
    public Region[] getIgnoreRegions() {
        return this.ignoreRegions;
    }

    /**
     * Returns the array of regions to check using the Strict method.
     * @return the array of regions to check using the Strict method.
     */
    @JsonGetter("Strict")
    public Region[] getStrictRegions() {
        return this.strictRegions;
    }


    /**
     * Returns the array of regions to check using the Layout method.
     * @return the array of regions to check using the Layout method.
     */
    @JsonGetter("Layout")
    public Region[] getLayoutRegions() {
        return this.layoutRegions;
    }


    /**
     * Returns the array of regions to check using the Content method.
     * @return the array of regions to check using the Content method.
     */
    @JsonGetter("Content")
    public Region[] getContentRegions() {
        return this.contentRegions;
    }

    /**
     * Sets an array of floating regions.
     * @param floatingRegions The array of floating regions.
     */
    @JsonSetter("Floating")
    public void setFloatingRegions(FloatingMatchSettings[] floatingRegions) {
        this.floatingMatchSettings = floatingRegions;
    }

    /**
     * Returns an array of floating regions.
     * @return an array of floating regions.
     */
    @JsonGetter("Floating")
    public FloatingMatchSettings[] getFloatingRegions() {
        return this.floatingMatchSettings;
    }

    /**
     * @param ignoreCaret The parameters for the "ignoreCaret" match settings.
     */
    public void setIgnoreCaret(Boolean ignoreCaret) {
        this.ignoreCaret = ignoreCaret;
    }


    public String toString() {
        return String.format("Match level: %s, Exact match settings: %s",
                matchLevel, exact);
    }

    public boolean isEnablePatterns() {
        return enablePatterns;
    }

    public void setEnablePatterns(boolean enablePatterns) {
        this.enablePatterns = enablePatterns;
    }

    public void setIgnoreDisplacements(boolean ignoreDisplacements) {
        this.ignoreDisplacements = ignoreDisplacements;
    }

    public boolean isIgnoreDisplacements() {
        return ignoreDisplacements;
    }

    public void setAccessibility(AccessibilityRegionByRectangle[] accessibilityValidation) {
        this.accessibility = accessibilityValidation;
    }

    public AccessibilitySettings getAccessibilitySettings() {
        return accessibilitySettings;
    }

    public void setAccessibilitySettings(AccessibilitySettings accessibilitySettings) {
        this.accessibilitySettings = accessibilitySettings;
    }

    public AccessibilityRegionByRectangle[] getAccessibility() {
        return accessibility;
    }
}
