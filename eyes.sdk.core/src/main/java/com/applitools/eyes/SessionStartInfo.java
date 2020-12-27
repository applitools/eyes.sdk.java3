/*
 * Applitools software.
 */
package com.applitools.eyes;

import com.applitools.utils.ArgumentGuard;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.List;

/**
 * Encapsulates model required to start session using the Session API.
 */

/*
 * We name the root value "startInfo" since this is a requirement of
 * the agent's web API.
 */
@JsonRootName(value = "startInfo")
public class SessionStartInfo {
    @JsonIgnore
    private String testId;
    private String agentId;
    private SessionType sessionType;
    private String appIdOrName;
    private String verId;
    private String scenarioIdOrName;
    private BatchInfo batchInfo;
    private String baselineEnvName;
    private String environmentName;
    private Object environment;
    private String branchName;
    private String parentBranchName;
    private String baselineBranchName;
    private Boolean saveDiffs;
    private ImageMatchSettings defaultMatchSettings;
    private List<PropertyData> properties;
    private String agentSessionId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer timeout;
    private final int concurrencyVersion = 2;

    public SessionStartInfo(String testId, String agentId, SessionType sessionType,
                            String appIdOrName, String verId,
                            String scenarioIdOrName, BatchInfo batchInfo,
                            String baselineEnvName, String environmentName,
                            Object environment,
                            ImageMatchSettings defaultMatchSettings,
                            String branchName, String parentBranchName, String baselineBranchName,
                            Boolean saveDiffs, List<PropertyData> properties, String agentSessionId, Integer timeout) {
        ArgumentGuard.notNullOrEmpty(agentId, "agentId");
        ArgumentGuard.notNullOrEmpty(appIdOrName, "appIdOrName");
        ArgumentGuard.notNullOrEmpty(scenarioIdOrName, "scenarioIdOrName");
        ArgumentGuard.notNull(batchInfo, "batchInfo");
        ArgumentGuard.notNull(environment, "environment");
        ArgumentGuard.notNull(defaultMatchSettings, "defaultMatchSettings");
        this.testId = testId;
        this.agentId = agentId;
        this.sessionType = sessionType;
        this.appIdOrName = appIdOrName;
        this.verId = verId;
        this.scenarioIdOrName = scenarioIdOrName;
        this.batchInfo = batchInfo;
        this.baselineEnvName = baselineEnvName;
        this.environmentName = environmentName;
        this.environment = environment;
        this.defaultMatchSettings = defaultMatchSettings;
        this.branchName = branchName;
        this.parentBranchName = parentBranchName;
        this.baselineBranchName = baselineBranchName;
        this.saveDiffs = saveDiffs;
        this.properties = properties;
        this.agentSessionId = agentSessionId;
        this.timeout = timeout;
    }

    public SessionStartInfo() {}

    public String getAgentId() {
        return agentId;
    }

    public SessionType getSessionType() {
        return sessionType;
    }

    public String getAppIdOrName() {
        return appIdOrName;
    }

    public String getVerId() {
        return verId;
    }

    public String getScenarioIdOrName() {
        return scenarioIdOrName;
    }

    public BatchInfo getBatchInfo() {
        return batchInfo;
    }

    public String getBaselineEnvName() {
        return baselineEnvName;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public Object getEnvironment() {
        return environment;
    }

    public String getBranchName() {
        return branchName;
    }

    public String getParentBranchName() {
        return parentBranchName;
    }

    public String getBaselineBranchName() {
        return baselineBranchName;
    }

    public Boolean getSaveDiffs() {
        return saveDiffs;
    }

    public ImageMatchSettings getDefaultMatchSettings() {
        return defaultMatchSettings;
    }

    public List<PropertyData> getProperties() {
        return properties;
    }

    public String getAgentSessionId() {
        return agentSessionId;
    }

    public int getConcurrencyVersion() {
        return concurrencyVersion;
    }

    public Integer getTimeout() {
        return timeout;
    }

    @Override
    public String toString() {
        return "SessionStartInfo{" +
                "agentId='" + agentId + '\'' +
                ", sessionType=" + sessionType +
                ", appIdOrName='" + appIdOrName + '\'' +
                ", verId='" + verId + '\'' +
                ", scenarioIdOrName='" + scenarioIdOrName + '\'' +
                ", batchInfo=" + batchInfo +
                ", baselineEnvName='" + baselineEnvName + '\'' +
                ", environmentName='" + environmentName + '\'' +
                ", environment=" + environment +
                ", branchName='" + branchName + '\'' +
                ", parentBranchName='" + parentBranchName + '\'' +
                ", baselineBranchName='" + baselineBranchName + '\'' +
                ", saveDiffs=" + saveDiffs +
                ", defaultMatchSettings=" + defaultMatchSettings +
                ", properties=" + properties +
                ", agentSessionId='" + agentSessionId + '\'' +
                ", timeout=" + timeout +
                ", concurrencyVersion=" + concurrencyVersion +
                '}';
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }
}
