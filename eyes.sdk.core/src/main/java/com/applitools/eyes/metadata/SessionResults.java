package com.applitools.eyes.metadata;

import com.applitools.eyes.AppEnvironment;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "revision",
    "runningSessionId",
    "isAborted",
    "isStarred",
    "startInfo",
    "batchId",
    "secretToken",
    "state",
    "status",
    "isDefaultStatus",
    "startedAt",
    "duration",
    "isDifferent",
    "env",
    "branch",
    "expectedAppOutput",
    "actualAppOutput",
    "baselineId",
    "baselineRevId",
    "scenarioId",
    "scenarioName",
    "appId",
    "baselineModelId",
    "baselineEnvId",
    "baselineEnv",
    "appName",
    "baselineBranchName",
    "isNew"
})
public class SessionResults {

    @JsonProperty("id")
    private String id;
    @JsonProperty("revision")
    private Integer revision;
    @JsonProperty("runningSessionId")
    private String runningSessionId;
    @JsonProperty("isAborted")
    private Boolean isAborted;
    @JsonProperty("isStarred")
    private Boolean isStarred;
    @JsonProperty("startInfo")
    private StartInfo startInfo;
    @JsonProperty("batchId")
    private String batchId;
    @JsonProperty("secretToken")
    private String secretToken;
    @JsonProperty("state")
    private String state;
    @JsonProperty("status")
    private String status;
    @JsonProperty("isDefaultStatus")
    private Boolean isDefaultStatus;
    @JsonProperty("startedAt")
    private String startedAt;
    @JsonProperty("duration")
    private Integer duration;
    @JsonProperty("isDifferent")
    private Boolean isDifferent;
    @JsonProperty("env")
    private AppEnvironment env;
    @JsonProperty("branch")
    private Branch branch;
    @JsonProperty("expectedAppOutput")
    private ExpectedAppOutput[] expectedAppOutput = null;
    @JsonProperty("actualAppOutput")
    private ActualAppOutput[] actualAppOutput = null;
    @JsonProperty("baselineId")
    private String baselineId;
    @JsonProperty("baselineRevId")
    private String baselineRevId;
    @JsonProperty("scenarioId")
    private String scenarioId;
    @JsonProperty("scenarioName")
    private String scenarioName;
    @JsonProperty("appId")
    private String appId;
    @JsonProperty("baselineModelId")
    private String baselineModelId;
    @JsonProperty("baselineEnvId")
    private String baselineEnvId;
    @JsonProperty("baselineEnv")
    private AppEnvironment baselineEnv;
    @JsonProperty("appName")
    private String appName;
    @JsonProperty("baselineBranchName")
    private String baselineBranchName;
    @JsonProperty("isNew")
    private Boolean isNew;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("revision")
    public Integer getRevision() {
        return revision;
    }

    @JsonProperty("revision")
    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    @JsonProperty("runningSessionId")
    public String getRunningSessionId() {
        return runningSessionId;
    }

    @JsonProperty("runningSessionId")
    public void setRunningSessionId(String runningSessionId) {
        this.runningSessionId = runningSessionId;
    }

    @JsonProperty("isAborted")
    public Boolean getIsAborted() {
        return isAborted;
    }

    @JsonProperty("isAborted")
    public void setIsAborted(Boolean isAborted) {
        this.isAborted = isAborted;
    }

    @JsonProperty("isStarred")
    public Boolean getIsStarred() {
        return isStarred;
    }

    @JsonProperty("isStarred")
    public void setIsStarred(Boolean isStarred) {
        this.isStarred = isStarred;
    }

    @JsonProperty("startInfo")
    public StartInfo getStartInfo() {
        return startInfo;
    }

    @JsonProperty("startInfo")
    public void setStartInfo(StartInfo startInfo) {
        this.startInfo = startInfo;
    }

    @JsonProperty("batchId")
    public String getBatchId() {
        return batchId;
    }

    @JsonProperty("batchId")
    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    @JsonProperty("secretToken")
    public String getSecretToken() {
        return secretToken;
    }

    @JsonProperty("secretToken")
    public void setSecretToken(String secretToken) {
        this.secretToken = secretToken;
    }

    @JsonProperty("state")
    public String getState() {
        return state;
    }

    @JsonProperty("state")
    public void setState(String state) {
        this.state = state;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("isDefaultStatus")
    public Boolean getIsDefaultStatus() {
        return isDefaultStatus;
    }

    @JsonProperty("isDefaultStatus")
    public void setIsDefaultStatus(Boolean isDefaultStatus) {
        this.isDefaultStatus = isDefaultStatus;
    }

    @JsonProperty("startedAt")
    public String getStartedAt() {
        return startedAt;
    }

    @JsonProperty("startedAt")
    public void setStartedAt(String startedAt) {
        this.startedAt = startedAt;
    }

    @JsonProperty("duration")
    public Integer getDuration() {
        return duration;
    }

    @JsonProperty("duration")
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    @JsonProperty("isDifferent")
    public Boolean getIsDifferent() {
        return isDifferent;
    }

    @JsonProperty("isDifferent")
    public void setIsDifferent(Boolean isDifferent) {
        this.isDifferent = isDifferent;
    }

    @JsonProperty("env")
    public AppEnvironment getEnv() {
        return env;
    }

    @JsonProperty("env")
    public void setEnv(AppEnvironment env) {
        this.env = env;
    }

    @JsonProperty("branch")
    public Branch getBranch() {
        return branch;
    }

    @JsonProperty("branch")
    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    @JsonProperty("expectedAppOutput")
    public ExpectedAppOutput[] getExpectedAppOutput() {
        return expectedAppOutput;
    }

    @JsonProperty("expectedAppOutput")
    public void setExpectedAppOutput(ExpectedAppOutput[] expectedAppOutput) {
        this.expectedAppOutput = expectedAppOutput;
    }

    @JsonProperty("actualAppOutput")
    public ActualAppOutput[] getActualAppOutput() {
        return actualAppOutput;
    }

    @JsonProperty("actualAppOutput")
    public void setActualAppOutput(ActualAppOutput[] actualAppOutput) {
        this.actualAppOutput = actualAppOutput;
    }

    @JsonProperty("baselineId")
    public String getBaselineId() {
        return baselineId;
    }

    @JsonProperty("baselineId")
    public void setBaselineId(String baselineId) {
        this.baselineId = baselineId;
    }

    @JsonProperty("baselineRevId")
    public String getBaselineRevId() {
        return baselineRevId;
    }

    @JsonProperty("baselineRevId")
    public void setBaselineRevId(String baselineRevId) {
        this.baselineRevId = baselineRevId;
    }

    @JsonProperty("scenarioId")
    public String getScenarioId() {
        return scenarioId;
    }

    @JsonProperty("scenarioId")
    public void setScenarioId(String scenarioId) {
        this.scenarioId = scenarioId;
    }

    @JsonProperty("scenarioName")
    public String getScenarioName() {
        return scenarioName;
    }

    @JsonProperty("scenarioName")
    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    @JsonProperty("appId")
    public String getAppId() {
        return appId;
    }

    @JsonProperty("appId")
    public void setAppId(String appId) {
        this.appId = appId;
    }

    @JsonProperty("baselineModelId")
    public String getBaselineModelId() {
        return baselineModelId;
    }

    @JsonProperty("baselineModelId")
    public void setBaselineModelId(String baselineModelId) {
        this.baselineModelId = baselineModelId;
    }

    @JsonProperty("baselineEnvId")
    public String getBaselineEnvId() {
        return baselineEnvId;
    }

    @JsonProperty("baselineEnvId")
    public void setBaselineEnvId(String baselineEnvId) {
        this.baselineEnvId = baselineEnvId;
    }

    @JsonProperty("baselineEnv")
    public AppEnvironment getBaselineEnv() {
        return baselineEnv;
    }

    @JsonProperty("baselineEnv")
    public void setBaselineEnv(AppEnvironment baselineEnv) {
        this.baselineEnv = baselineEnv;
    }

    @JsonProperty("appName")
    public String getAppName() {
        return appName;
    }

    @JsonProperty("appName")
    public void setAppName(String appName) {
        this.appName = appName;
    }

    @JsonProperty("baselineBranchName")
    public String getBaselineBranchName() {
        return baselineBranchName;
    }

    @JsonProperty("baselineBranchName")
    public void setBaselineBranchName(String baselineBranchName) {
        this.baselineBranchName = baselineBranchName;
    }

    @JsonProperty("isNew")
    public Boolean getIsNew() {
        return isNew;
    }

    @JsonProperty("isNew")
    public void setIsNew(Boolean isNew) {
        this.isNew = isNew;
    }

}
