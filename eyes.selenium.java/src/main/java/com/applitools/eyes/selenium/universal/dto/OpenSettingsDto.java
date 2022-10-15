package com.applitools.eyes.selenium.universal.dto;

import com.applitools.eyes.*;

import java.util.List;

public class OpenSettingsDto {
    private String serverUrl;
    private String apiKey;
    private AbstractProxySettings proxySettings;
    private String appName;
    private Integer connectionTimeout;
    private Boolean removeSession;
    private String agentId;
    private String testName;
    private String displayName;
    private String userTestId;
    private SessionType sessionType;
    private List<PropertyData> properties;
    private BatchInfo batch;
    private Boolean keepBatchOpen;
    private String environmentName;
    private AppEnvironment environment;
    private String branchName;
    private String parentBranchName;
    private String baselineEnvName;
    private String baselineBranchName;
    private Boolean compareWithParentBranch;
    private Boolean ignoreBaseline;
    private Boolean ignoreGitBranching;
    private Boolean saveDiffs;
    private Integer abortIdleTestTimeout;

    public String getServerUrl() { return serverUrl; }

    public void setServerUrl(String serverUrl) { this.serverUrl = serverUrl; }

    public String getApiKey() { return apiKey; }

    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public AbstractProxySettings getProxySettings() { return proxySettings; }

    public void setProxy(AbstractProxySettings proxySettings) { this.proxySettings = proxySettings; }

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

    public SessionType getSessionType() { return sessionType; }

    public void setSessionType(SessionType sessionType) { this.sessionType = sessionType; }

    public List<PropertyData> getProperties() { return properties; }

    public void setProperties(List<PropertyData> properties) { this.properties = properties; }

    public BatchInfo getBatch() { return batch; }

    public void setBatch(BatchInfo batch) { this.batch = batch; }

    public Boolean getKeepBatchOpen() { return keepBatchOpen; }

    public void setKeepBatchOpen(Boolean keepBatchOpen) { this.keepBatchOpen = keepBatchOpen; }

    public String getEnvironmentName() { return environmentName; }

    public void setEnvironmentName(String environmentName) { this.environmentName = environmentName; }

    public AppEnvironment getEnvironment() { return environment; }

    public void setEnvironment(AppEnvironment environment) { this.environment = environment; }

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
