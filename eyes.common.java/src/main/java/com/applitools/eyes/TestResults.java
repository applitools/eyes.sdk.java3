package com.applitools.eyes;

import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.Iso8610CalendarDeserializer;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Calendar;

/**
 * Eyes test results.
 */
@SuppressWarnings("WeakerAccess")
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
    private TestResultsStatus status;
    private String name;
    private String secretToken;
    private String id;
    private String appName;
    private String batchName;
    private String batchId;
    private String branchName;
    private String hostOS;
    private String hostApp;
    private RectangleSize hostDisplaySize;
    @JsonDeserialize(using = Iso8610CalendarDeserializer.class)
    private Calendar startedAt;
    private int duration;
    private boolean isDifferent;
    private boolean isAborted;
    private SessionUrls appUrls;
    private SessionUrls apiUrls;
    private StepInfo[] stepsInfo;
    private IServerConnector serverConnector;

    public StepInfo[] getStepsInfo() {
        return stepsInfo;
    }

    public void setStepsInfo(StepInfo[] stepsInfo) {
        this.stepsInfo = stepsInfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecretToken() {
        return secretToken;
    }

    public void setSecretToken(String secretToken) {
        this.secretToken = secretToken;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getHostOS() {
        return hostOS;
    }

    public void setHostOS(String hostOS) {
        this.hostOS = hostOS;
    }

    public String getHostApp() {
        return hostApp;
    }

    public void setHostApp(String hostApp) {
        this.hostApp = hostApp;
    }

    public RectangleSize getHostDisplaySize() {
        return hostDisplaySize;
    }

    public void setHostDisplaySize(RectangleSize hostDisplaySize) {
        this.hostDisplaySize = hostDisplaySize;
    }

    public Calendar getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Calendar startedAt) {
        this.startedAt = startedAt;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @JsonGetter("isDifferent")
    public boolean isDifferent() {
        return isDifferent;
    }

    @JsonSetter("isDifferent")
    public void setDifferent(boolean different) {
        isDifferent = different;
    }

    @JsonGetter("isAborted")
    public boolean isAborted() {
        return isAborted;
    }

    @JsonSetter("isAborted")
    public void setAborted(boolean aborted) {
        isAborted = aborted;
    }

    public SessionUrls getAppUrls() {
        return appUrls;
    }

    public void setAppUrls(SessionUrls appUrls) {
        this.appUrls = appUrls;
    }

    public SessionUrls getApiUrls() {
        return apiUrls;
    }

    public void setApiUrls(SessionUrls apiUrls) {
        this.apiUrls = apiUrls;
    }

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
    @JsonGetter("isNew")
    public boolean isNew() {
        return isNew;
    }

    /**
     * @return Whether or not this test passed.
     */
    public boolean isPassed() {
        return status == TestResultsStatus.Passed;
    }

    /**
     * @return The result status.
     */
    public TestResultsStatus getStatus() {
        return status;
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
     *                      level set to
     *                      {@link com.applitools.eyes.MatchLevel#STRICT}
     */
    @SuppressWarnings("UnusedDeclaration")
    void setStrictMatches(int strictMatches) {
        ArgumentGuard.greaterThanOrEqualToZero(strictMatches, "strictMatches");
        this.strictMatches = strictMatches;
    }

    /**
     * @param contentMatches The number of matches performed with match
     *                       level set to
     *                       {@link com.applitools.eyes.MatchLevel#CONTENT}
     */
    @SuppressWarnings("UnusedDeclaration")
    void setContentMatches(int contentMatches) {
        ArgumentGuard.greaterThanOrEqualToZero(contentMatches, "contentMatches");
        this.contentMatches = contentMatches;
    }

    /**
     * @param layoutMatches The number of matches performed with match
     *                      level set to
     *                      {@link com.applitools.eyes.MatchLevel#LAYOUT}
     */
    @SuppressWarnings("UnusedDeclaration")
    void setLayoutMatches(int layoutMatches) {
        ArgumentGuard.greaterThanOrEqualToZero(layoutMatches, "layoutMatches");
        this.layoutMatches = layoutMatches;
    }

    /**
     * @param noneMatches The number of matches performed with match
     *                    level set to
     *                    {@link com.applitools.eyes.MatchLevel#NONE}
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
    @JsonSetter("isNew")
    void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    /**
     * @param status The new test result status.
     */
    void setStatus(TestResultsStatus status) {
        this.status = status;
    }

    void setServerConnector(IServerConnector serverConnector)
    {
        this.serverConnector = serverConnector;
    }

    public void delete(){
        serverConnector.deleteSession(this);
    }

    @Override
    public String toString() {
        String isNewTestStr = isNew ? "New test" : "Existing test";
        return isNewTestStr + " [ steps: " + getSteps()
                + ", test name: " + name
                + ", matches: " + getMatches()
                + ", mismatches:" + getMismatches() + ", missing: "
                + getMissing() + "] , URL: " + getUrl();
    }

}
