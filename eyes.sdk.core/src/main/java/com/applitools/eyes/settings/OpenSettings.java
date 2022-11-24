package com.applitools.eyes.settings;

import com.applitools.eyes.*;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenSettings implements IOpenSettings {

    private Boolean isDisabled = false;
    private String serverUrl = "https://eyes.applitools.com/";
    private String apiKey;
    private AbstractProxySettings proxy;
    private String appName;
    private Integer connectionTimeout;
    private Boolean removeSession;
    private String agentId;
    private String testName;
    private String displayName;
    private String userTestId;
    private SessionType sessionType;
    private List<PropertyData> properties = new ArrayList<>();
    private BatchInfo batch;
    private Boolean keepBatchOpen;
    private String environmentName;
    private AppEnvironment environment = new AppEnvironment();
    private String branchName = GeneralUtils.getEnvString("APPLITOOLS_BRANCH");
    private String parentBranchName = GeneralUtils.getEnvString("APPLITOOLS_PARENT_BRANCH");
    private String baselineEnvName;
    private String baselineBranchName = GeneralUtils.getEnvString("APPLITOOLS_BASELINE_BRANCH");
    private Boolean compareWithParentBranch;
    private Boolean ignoreBaseline;
    private Boolean ignoreGitBranching;
    private Boolean saveDiffs;
    private Integer abortIdleTestTimeout;

    @Override
    public IOpenSettings setIsDisabled(Boolean isDisabled) {
        OpenSettings clone = this.clone();
        clone.isDisabled = isDisabled;
        return clone;
    }

    @Override
    public IOpenSettings setServerUrl(String serverUrl) {
        OpenSettings clone = this.clone();
        clone.serverUrl = serverUrl;
        return clone;
    }

    @Override
    public IOpenSettings setApiKey(String apiKey) {
        OpenSettings clone = this.clone();
        clone.apiKey = apiKey;
        return clone;
    }

    @Override
    public IOpenSettings setProxy(AbstractProxySettings proxySettings) {
        OpenSettings clone = this.clone();
        clone.proxy = proxySettings;
        return clone;
    }

    @Override
    public IOpenSettings setAppName(String appName) {
        OpenSettings clone = this.clone();
        clone.appName = appName;
        return clone;
    }

    @Override
    public IOpenSettings setConnectionTimeout(Integer connectionTimeout) {
        OpenSettings clone = this.clone();
        clone.connectionTimeout = connectionTimeout;
        return clone;
    }

    @Override
    public IOpenSettings setRemoveSession(Boolean removeSession) {
        OpenSettings clone = this.clone();
        clone.removeSession = removeSession;
        return clone;
    }

    @Override
    public IOpenSettings setAgentId(String agentId) {
        OpenSettings clone = this.clone();
        clone.agentId = agentId;
        return clone;
    }

    @Override
    public IOpenSettings setTestName(String testName) {
        OpenSettings clone = this.clone();
        clone.testName = testName;
        return clone;
    }

    @Override
    public IOpenSettings setDisplayName(String displayName) {
        OpenSettings clone = this.clone();
        clone.displayName = displayName;
        return clone;
    }

    @Override
    public IOpenSettings setUserTestId(String userTestId) {
        OpenSettings clone = this.clone();
        clone.userTestId = userTestId;
        return clone;
    }

    @Override
    public IOpenSettings setSessionType(SessionType sessionType) {
        OpenSettings clone = this.clone();
        clone.sessionType = sessionType;
        return clone;
    }

    @Override
    public IOpenSettings setProperties(List<PropertyData> properties) {
        OpenSettings clone = this.clone();
        clone.properties = properties;
        return clone;
    }

    @Override
    public void addProperty(String name, String value) {
        PropertyData pd = new PropertyData(name, value);
        this.properties.add(pd);
    }

    @Override
    public IOpenSettings setBatch(BatchInfo batch) {
        OpenSettings clone = this.clone();
        clone.batch = batch;
        return clone;
    }

    @Override
    public IOpenSettings setKeepBatchOpen(Boolean keepBatchOpen) {
        OpenSettings clone = this.clone();
        clone.keepBatchOpen = keepBatchOpen;
        return clone;
    }

    @Override
    public IOpenSettings setEnvironmentName(String environmentName) {
        OpenSettings clone = this.clone();
        clone.environmentName = environmentName;
        return clone;
    }

    @Override
    public IOpenSettings setEnvironment(AppEnvironment environment) {
        OpenSettings clone = this.clone();
        clone.environment = environment;
        return clone;
    }

    @Override
    public IOpenSettings setBranchName(String branchName) {
        OpenSettings clone = this.clone();
        clone.branchName = branchName;
        return clone;
    }

    @Override
    public IOpenSettings setParentBranchName(String parentBranchName) {
        OpenSettings clone = this.clone();
        clone.parentBranchName = parentBranchName;
        return clone;
    }

    @Override
    public IOpenSettings setBaselineEnvName(String baselineEnvName) {
        OpenSettings clone = this.clone();
        clone.baselineEnvName = baselineEnvName;
        return clone;
    }

    @Override
    public IOpenSettings setBaselineBranchName(String baselineBranchName) {
        OpenSettings clone = this.clone();
        clone.baselineBranchName = baselineBranchName;
        return clone;
    }

    @Override
    public IOpenSettings setCompareWithParentBranch(Boolean compareWithParentBranch) {
        OpenSettings clone = this.clone();
        clone.compareWithParentBranch = compareWithParentBranch;
        return clone;
    }

    @Override
    public IOpenSettings setIgnoreBaseline(Boolean ignoreBaseline) {
        OpenSettings clone = this.clone();
        clone.ignoreBaseline = ignoreBaseline;
        return clone;
    }

    @Override
    public IOpenSettings setIgnoreGitBranching(Boolean ignoreGitBranching) {
        OpenSettings clone = this.clone();
        clone.ignoreGitBranching = ignoreGitBranching;
        return clone;
    }

    @Override
    public IOpenSettings setSaveDiffs(Boolean saveDiffs) {
        OpenSettings clone = this.clone();
        clone.saveDiffs = saveDiffs;
        return clone;
    }

    @Override
    public IOpenSettings setAbortIdleTestTimeout(Integer idleTestTimeout) {
        OpenSettings clone = this.clone();
        clone.abortIdleTestTimeout = idleTestTimeout;
        return clone;
    }

    public OpenSettings clone() {
        OpenSettings clone = new OpenSettings();

        clone.isDisabled = this.isDisabled;
        clone.serverUrl = this.serverUrl;
        clone.apiKey = this.apiKey;
        clone.proxy = this.proxy;
        clone.appName = this.appName;
        clone.connectionTimeout = this.connectionTimeout;
        clone.removeSession = this.removeSession;
        clone.agentId = this.agentId;
        clone.testName = this.testName;
        clone.displayName = this.displayName;
        clone.userTestId = this.userTestId;
        clone.sessionType = this.sessionType;
        clone.properties = this.properties;
        clone.batch = this.batch;
        clone.keepBatchOpen = this.keepBatchOpen;
        clone.environmentName = this.environmentName;
        clone.environment = this.environment;
        clone.branchName = this.branchName;
        clone.parentBranchName = this.parentBranchName;
        clone.baselineEnvName = this.baselineEnvName;
        clone.baselineBranchName = this.baselineBranchName;
        clone.compareWithParentBranch = this.compareWithParentBranch;
        clone.ignoreBaseline = this.ignoreBaseline;
        clone.ignoreGitBranching = this.ignoreGitBranching;
        clone.saveDiffs = this.saveDiffs;
        clone.abortIdleTestTimeout = this.abortIdleTestTimeout;

        return clone;
    }

    public Boolean getIsDisabled() { return isDisabled; }

    public String getServerUrl() {
        return serverUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public AbstractProxySettings getProxy() {
        return proxy;
    }

    public String getAppName() {
        return appName;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public Boolean getRemoveSession() {
        return removeSession;
    }

    public String getAgentId() {
        return agentId;
    }

    public String getTestName() {
        return testName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUserTestId() {
        return userTestId;
    }

    public SessionType getSessionType() {
        return sessionType;
    }

    public List<PropertyData> getProperties() {
        return properties;
    }

    public BatchInfo getBatch() {
        return batch;
    }

    public Boolean getKeepBatchOpen() {
        return keepBatchOpen;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public AppEnvironment getEnvironment() {
        return environment;
    }

    public String getBranchName() {
        return branchName;
    }

    public String getParentBranchName() {
        return parentBranchName;
    }

    public String getBaselineEnvName() {
        return baselineEnvName;
    }

    public String getBaselineBranchName() {
        return baselineBranchName;
    }

    public Boolean getCompareWithParentBranch() {
        return compareWithParentBranch;
    }

    public Boolean getIgnoreBaseline() {
        return ignoreBaseline;
    }

    public Boolean getIgnoreGitBranching() {
        return ignoreGitBranching;
    }

    public Boolean getSaveDiffs() {
        return saveDiffs;
    }

    public Integer getAbortIdleTestTimeout() {
        return abortIdleTestTimeout;
    }
}
