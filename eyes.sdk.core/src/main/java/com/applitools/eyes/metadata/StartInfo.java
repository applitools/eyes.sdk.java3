package com.applitools.eyes.metadata;

import com.applitools.eyes.AppEnvironment;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "sessionType",
    "isTransient",
    "ignoreBaseline",
    "appIdOrName",
    "compareWithParentBranch",
    "scenarioIdOrName",
    "batchInfo",
    "environment",
    "matchLevel",
    "defaultMatchSettings",
    "agentId",
    "properties"
})
public class StartInfo {

    @JsonProperty("sessionType")
    private String sessionType;
    @JsonProperty("isTransient")
    private Boolean isTransient;
    @JsonProperty("ignoreBaseline")
    private Boolean ignoreBaseline;
    @JsonProperty("appIdOrName")
    private String appIdOrName;
    @JsonProperty("compareWithParentBranch")
    private Boolean compareWithParentBranch;
    @JsonProperty("scenarioIdOrName")
    private String scenarioIdOrName;
    @JsonProperty("batchInfo")
    private BatchInfo batchInfo;
    @JsonProperty("environment")
    private AppEnvironment environment;
    @JsonProperty("matchLevel")
    private String matchLevel;
    @JsonProperty("defaultMatchSettings")
    private ImageMatchSettings defaultMatchSettings;
    @JsonProperty("agentId")
    private String agentId;
    @JsonProperty("properties")
    private Object[] properties = null;

    @JsonProperty("sessionType")
    public String getSessionType() {
        return sessionType;
    }

    @JsonProperty("sessionType")
    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    @JsonProperty("isTransient")
    public Boolean getIsTransient() {
        return isTransient;
    }

    @JsonProperty("isTransient")
    public void setIsTransient(Boolean isTransient) {
        this.isTransient = isTransient;
    }

    @JsonProperty("ignoreBaseline")
    public Boolean getIgnoreBaseline() {
        return ignoreBaseline;
    }

    @JsonProperty("ignoreBaseline")
    public void setIgnoreBaseline(Boolean ignoreBaseline) {
        this.ignoreBaseline = ignoreBaseline;
    }

    @JsonProperty("appIdOrName")
    public String getAppIdOrName() {
        return appIdOrName;
    }

    @JsonProperty("appIdOrName")
    public void setAppIdOrName(String appIdOrName) {
        this.appIdOrName = appIdOrName;
    }

    @JsonProperty("compareWithParentBranch")
    public Boolean getCompareWithParentBranch() {
        return compareWithParentBranch;
    }

    @JsonProperty("compareWithParentBranch")
    public void setCompareWithParentBranch(Boolean compareWithParentBranch) {
        this.compareWithParentBranch = compareWithParentBranch;
    }

    @JsonProperty("scenarioIdOrName")
    public String getScenarioIdOrName() {
        return scenarioIdOrName;
    }

    @JsonProperty("scenarioIdOrName")
    public void setScenarioIdOrName(String scenarioIdOrName) {
        this.scenarioIdOrName = scenarioIdOrName;
    }

    @JsonProperty("batchInfo")
    public BatchInfo getBatchInfo() {
        return batchInfo;
    }

    @JsonProperty("batchInfo")
    public void setBatchInfo(BatchInfo batchInfo) {
        this.batchInfo = batchInfo;
    }

    @JsonProperty("environment")
    public AppEnvironment getEnvironment() {
        return environment;
    }

    @JsonProperty("environment")
    public void setEnvironment(AppEnvironment environment) {
        this.environment = environment;
    }

    @JsonProperty("matchLevel")
    public String getMatchLevel() {
        return matchLevel;
    }

    @JsonProperty("matchLevel")
    public void setMatchLevel(String matchLevel) {
        this.matchLevel = matchLevel;
    }

    @JsonProperty("defaultMatchSettings")
    public ImageMatchSettings getDefaultMatchSettings() {
        return defaultMatchSettings;
    }

    @JsonProperty("defaultMatchSettings")
    public void setDefaultMatchSettings(ImageMatchSettings defaultMatchSettings) {
        this.defaultMatchSettings = defaultMatchSettings;
    }

    @JsonProperty("agentId")
    public String getAgentId() {
        return agentId;
    }

    @JsonProperty("agentId")
    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    @JsonProperty("properties")
    public Object[] getProperties() {
        return properties;
    }

    @JsonProperty("properties")
    public void setProperties(Object[] properties) {
        this.properties = properties;
    }

}
