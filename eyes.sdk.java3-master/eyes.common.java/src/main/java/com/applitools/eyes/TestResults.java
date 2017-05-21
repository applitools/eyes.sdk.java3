package com.applitools.eyes;

import com.applitools.utils.ArgumentGuard;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Eyes test results.
 */
@JsonIgnoreProperties({"$id", "isPassed"})
public class TestResults {
    private int steps;
    private int matches;
    private int mismatches;
    private int missing;
    private int exactMatches;
    private int strictMatches;
    private int contentMatches;
    private int layoutMatches;
    private int noneMatches;
    private String url;
    private boolean isNew;

    /**
     * @return The total number of test steps.
     */
    public int getSteps() {
        return steps;
    }

    /**
     * @return The total number of test steps that matched the baseline.
     */
    public int getMatches() {
        return matches;
    }

    /**
     * @return The total number of test steps that did not match the baseline.
     */
    public int getMismatches() {
        return mismatches;
    }

    /**
     * @return The total number of baseline test steps that were missing in
     * the test.
     */
    public int getMissing() {
        return missing;
    }

    /**
     * @return The total number of test steps that exactly matched the baseline.
     */
    @SuppressWarnings("UnusedDeclaration")
    public int getExactMatches() {
        return exactMatches;
    }

    /**
     * @return The total number of test steps that strictly matched the
     * baseline.
     */
    @SuppressWarnings("UnusedDeclaration")
    public int getStrictMatches() {
        return strictMatches;
    }

    /**
     * @return The total number of test steps that matched the baseline by
     * content.
     */
    @SuppressWarnings("UnusedDeclaration")
    public int getContentMatches() {
        return contentMatches;
    }

    /**
     * @return The total number of test steps that matched the baseline by
     * layout.
     */
    @SuppressWarnings("UnusedDeclaration")
    public int getLayoutMatches() {
        return layoutMatches;
    }

    /**
     * @return The total number of test steps that matched the baseline without
     * performing any comparison.
     */
    @SuppressWarnings("UnusedDeclaration")
    public int getNoneMatches() {
        return noneMatches;
    }

    /**
     * @return The URL where test results can be viewed.
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return Whether or not this is a new test.
     */
    public boolean isNew() {
        return isNew;
    }

    /**
     * @return Whether or not this test passed.
     */
    @SuppressWarnings("UnusedDeclaration")
    public boolean isPassed() {
        return (!isNew() && getMismatches() == 0 && getMissing() == 0);
    }

    /**
     * @param steps The number of visual checkpoints in the test.
     */
    @SuppressWarnings("UnusedDeclaration")
    void setSteps(int steps) {
        ArgumentGuard.greaterThanOrEqualToZero(steps, "steps");
        this.steps = steps;
    }

    /**
     * @param matches The number of visual matches in the test.
     */
    @SuppressWarnings("UnusedDeclaration")
    void setMatches(int matches) {
        ArgumentGuard.greaterThanOrEqualToZero(matches, "matches");
        this.matches = matches;
    }

    /**
     * @param mismatches The number of mismatches in the test.
     */
    @SuppressWarnings("UnusedDeclaration")
    void setMismatches(int mismatches) {
        ArgumentGuard.greaterThanOrEqualToZero(mismatches, "mismatches");
        this.mismatches = mismatches;
    }

    /**
     * @param missing The number of visual checkpoints that were available in
     *                the baseline but were not found in the current test.
     */
    @SuppressWarnings("UnusedDeclaration")
    void setMissing(int missing) {
        ArgumentGuard.greaterThanOrEqualToZero(missing, "missing");
        this.missing = missing;
    }

    /**
     * @param exactMatches The number of matches performed with match
     *                     level set to
     *                     {@link com.applitools.eyes.MatchLevel#EXACT}
     */
    @SuppressWarnings("UnusedDeclaration")
    void setExactMatches(int exactMatches) {
        ArgumentGuard.greaterThanOrEqualToZero(exactMatches, "exactMatches");
        this.exactMatches = exactMatches;
    }

    /**
     * @param strictMatches The number of matches performed with match
     *                     level set to
     *                     {@link com.applitools.eyes.MatchLevel#STRICT}
     */
    @SuppressWarnings("UnusedDeclaration")
    void setStrictMatches(int strictMatches) {
        ArgumentGuard.greaterThanOrEqualToZero(strictMatches, "strictMatches");
        this.strictMatches = strictMatches;
    }

    /**
     * @param contentMatches The number of matches performed with match
     *                     level set to
     *                     {@link com.applitools.eyes.MatchLevel#CONTENT}
     */
    @SuppressWarnings("UnusedDeclaration")
    void setContentMatches(int contentMatches) {
        ArgumentGuard.greaterThanOrEqualToZero(contentMatches, "contentMatches");
        this.contentMatches = contentMatches;
    }

    /**
     * @param layoutMatches The number of matches performed with match
     *                     level set to
     *                     {@link com.applitools.eyes.MatchLevel#LAYOUT}
     */
    @SuppressWarnings("UnusedDeclaration")
    void setLayoutMatches(int layoutMatches) {
        ArgumentGuard.greaterThanOrEqualToZero(layoutMatches, "layoutMatches");
        this.layoutMatches = layoutMatches;
    }

    /**
     * @param noneMatches The number of matches performed with match
     *                     level set to
     *                     {@link com.applitools.eyes.MatchLevel#NONE}
     */
    @SuppressWarnings("UnusedDeclaration")
    void setNoneMatches(int noneMatches) {
        ArgumentGuard.greaterThanOrEqualToZero(noneMatches, "noneMatches");
        this.noneMatches = noneMatches;
    }

    /**
     * @param url The URL of the test results.
     */
    void setUrl(String url) {
        this.url = url;
    }

    /**
     * @param isNew Whether or not this test has an existing baseline.
     */
    void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    @Override
    public String toString() {
        String isNewTestStr = isNew ? "New test" : "Existing test";
        return isNewTestStr + " [ steps: " + getSteps()
                + ", matches: " + getMatches()
                + ", mismatches:" + getMismatches() + ", missing: "
                + getMissing() + "] , URL: " + getUrl();
    }
}