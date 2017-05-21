package com.applitools.eyes;

/**
 * Encapsulates settings for the "Exact" match level.
 */
public class ExactMatchSettings {

    /**
     * Minimal non-ignorable pixel intensity difference.
     */
    private int minDiffIntensity;

    /**
     * Minimal non-ignorable diff region width.
     */
    private int minDiffWidth;

    /**
     * Minimal non-ignorable diff region height.
     */
    private int minDiffHeight;

    /**
     * The ratio of differing pixels above which images are considered
     * mismatching.
     */
    private float matchThreshold;


    @SuppressWarnings("UnusedDeclaration")
    /**
     *
     * @return The minimal non-ignorable pixel intensity difference.
     */
    public int getMinDiffIntensity() {
        return minDiffIntensity;
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     *
     * @param minDiffIntensity The minimal non-ignorable pixel intensity
     *                         difference.
     */
    public void setMinDiffIntensity(int minDiffIntensity) {
        this.minDiffIntensity = minDiffIntensity;
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     *
     * @return The minimal non-ignorable diff region width.
     */
    public int getMinDiffWidth() {
        return minDiffWidth;
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     *
     * @param minDiffWidth The minimal non-ignorable diff region width.
     */
    public void setMinDiffWidth(int minDiffWidth) {
        this.minDiffWidth = minDiffWidth;
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     *
     * @return The minimal non-ignorable diff region height.
     */
    public int getMinDiffHeight() {
        return minDiffHeight;
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     *
     * @param minDiffHeight The minimal non-ignorable diff region height.
     */
    public void setMinDiffHeight(int minDiffHeight) {
        this.minDiffHeight = minDiffHeight;
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     *
     * @return The ratio of differing pixels above which images are
     * considered mismatching.
     */
    public float getMatchThreshold() {
        return matchThreshold;
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     *
     * @param matchThreshold The ratio of differing pixels above which images
     *                       are considered mismatching.
     */
    public void setMatchThreshold(float matchThreshold) {
        this.matchThreshold = matchThreshold;
    }

    public String toString() {
        return String.format("[min diff intensity: %d, min diff width %d, " +
                "min diff height %d, match threshold: %f]", minDiffIntensity,
                minDiffWidth, minDiffHeight, matchThreshold);
    }
}
