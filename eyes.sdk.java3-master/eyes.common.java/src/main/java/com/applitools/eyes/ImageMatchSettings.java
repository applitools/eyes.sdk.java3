package com.applitools.eyes;

/**
 * Encapsulates match settings for the a session.
 */
public class ImageMatchSettings {
    private MatchLevel matchLevel;
    private ExactMatchSettings exact;

    public ImageMatchSettings(MatchLevel matchLevel, ExactMatchSettings exact) {
        this.matchLevel = matchLevel;
        this.exact = exact;
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

    @SuppressWarnings("UnusedDeclaration")
    /**
     *
     * @return The parameters for the "Exact" match settings.
     */
    public ExactMatchSettings getExact() {
        return exact;
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     *
     * @param exact The parameters for the "exact" match settings.
     */
    public void setExact(ExactMatchSettings exact) {
        this.exact = exact;
    }

    public String toString() {
        return String.format("Match level: %s, Exact match settings: %s",
                matchLevel, exact);
    }
}
