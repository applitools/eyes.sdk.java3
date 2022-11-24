package com.applitools.eyes.universal.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenSettingsDto {
    @JsonIgnore
    private Boolean isDisabled;
    private String serverUrl;
    private String apiKey;
    private ProxyDto proxy;
    private String appName;
    private Integer connectionTimeout;
    private Boolean removeSession;
    private String agentId;
    private String testName;
    private String displayName;
    private String userTestId;
    private String sessionType;
    private List<CustomPropertyDto> properties;
    private BatchDto batch;
    private Boolean keepBatchOpen;
    private String environmentName;
    private AppEnvironmentDto environment;
    private String branchName;
    private String parentBranchName;
    private String baselineEnvName;
    private String baselineBranchName;
    private Boolean compareWithParentBranch;
    private Boolean ignoreBaseline;
    private Boolean ignoreGitBranching;
    private Boolean saveDiffs;
    private Integer abortIdleTestTimeout;

    public OpenSettingsDto() {
        environment = new AppEnvironmentDto();
    }

    public Boolean getDisabled() {
        return isDisabled;
    }

    public void setDisabled(Boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    public String getServerUrl() { return serverUrl; }

    public void setServerUrl(String serverUrl) { this.serverUrl = serverUrl; }

    public String getApiKey() { return apiKey; }

    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public ProxyDto getProxy() { return proxy; }

    public void setProxy(ProxyDto proxySettings) { this.proxy = proxySettings; }

    public String getAppName() { return appName; }

    public void setAppName(String appName) { this.appName = appName; }

    public Integer getConnectionTimeout() { return connectionTimeout; }

    public void setConnectionTimeout(Integer connectionTimeout) { this.connectionTimeout = connectionTimeout; }

    public Boolean getRemoveSession() { return removeSession; }

    public void setRemoveSession(Boolean removeSession) { this.removeSession = removeSession; }

    public String getAgentId() { return agentId; }

    public void setAgentId(String agentId) { this.agentId = agentId; }

    public String getTestName() { return testName; }

    public void setTestName(String testName) { this.testName = testName; }

    public String getDisplayName() { return displayName; }

    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getUserTestId() { return userTestId; }

    public void setUserTestId(String userTestId) { this.userTestId = userTestId; }

    public String getSessionType() { return sessionType; }

    public void setSessionType(String sessionType) { this.sessionType = sessionType; }

    public List<CustomPropertyDto> getProperties() { return properties; }

    public void setProperties(List<CustomPropertyDto> properties) { this.properties = properties; }

    public BatchDto getBatch() { return batch; }

    public void setBatch(BatchDto batch) { this.batch = batch; }

    public Boolean getKeepBatchOpen() { return keepBatchOpen; }

    public void setKeepBatchOpen(Boolean keepBatchOpen) { this.keepBatchOpen = keepBatchOpen; }

    public String getEnvironmentName() { return environmentName; }

    public void setEnvironmentName(String environmentName) { this.environmentName = environmentName; }

    public AppEnvironmentDto getEnvironment() { return environment; }

    public void setEnvironment(AppEnvironmentDto environment) { this.environment = environment; }

    public String getBranchName() { return branchName; }

    public void setBranchName(String branchName) { this.branchName = branchName; }

    public String getParentBranchName() { return parentBranchName; }

    public void setParentBranchName(String parentBranchName) { this.parentBranchName = parentBranchName; }

    public String getBaselineEnvName() { return baselineEnvName; }

    public void setBaselineEnvName(String baselineEnvName) { this.baselineEnvName = baselineEnvName; }

    public String getBaselineBranchName() { return baselineBranchName; }

    public void setBaselineBranchName(String baselineBranchName) { this.baselineBranchName = baselineBranchName; }

    public Boolean getCompareWithParentBranch() { return compareWithParentBranch; }

    public void setCompareWithParentBranch(Boolean compareWithParentBranch) { this.compareWithParentBranch = compareWithParentBranch; }

    public Boolean getIgnoreBaseline() { return ignoreBaseline; }

    public void setIgnoreBaseline(Boolean ignoreBaseline) { this.ignoreBaseline = ignoreBaseline; }

    public Boolean getIgnoreGitBranching() { return ignoreGitBranching; }

    public void setIgnoreGitBranching(Boolean ignoreGitBranching) { this.ignoreGitBranching = ignoreGitBranching; }

    public Boolean getSaveDiffs() { return saveDiffs; }

    public void setSaveDiffs(Boolean saveDiffs) { this.saveDiffs = saveDiffs; }

    public Integer getAbortIdleTestTimeout() { return abortIdleTestTimeout; }

    public void setAbortIdleTestTimeout(Integer abortIdleTestTimeout) { this.abortIdleTestTimeout = abortIdleTestTimeout; }
}
