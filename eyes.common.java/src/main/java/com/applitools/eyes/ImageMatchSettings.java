package com.applitools.eyes;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Encapsulates match settings for the a session.
 */
public class ImageMatchSettings {
    private MatchLevel matchLevel;
    private ExactMatchSettings exact;
    private Boolean ignoreCaret;
    private Region ignoreRegions[];
    private FloatingMatchSettings floatingMatchSettings[];

    public ImageMatchSettings(MatchLevel matchLevel, ExactMatchSettings exact) {
        this.matchLevel = matchLevel;
        this.exact = exact;
        this.ignoreCaret = null;
    }

    /**
     * See {@link #ImageMatchSettings(MatchLevel, ExactMatchSettings)}.
     * {@code matchLevel} defaults to {@code STRICT},
     * {@code exact} defaults to {@code null}.
     */
    public ImageMatchSettings() {
        this(MatchLevel.STRICT, null);
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

    /**
     * Sets an array of regions to ignore.
     * @param ignoreRegions The array of regions to ignore.
     */
    @JsonSetter("Ignore")
    public void setIgnoreRegions(Region[] ignoreRegions) {
        this.ignoreRegions = ignoreRegions;
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
}
