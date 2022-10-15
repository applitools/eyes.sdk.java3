package com.applitools.eyes.settings;

import com.applitools.eyes.*;

import java.util.List;

/**
 * The interface for a command settings.
 */
public interface IOpenSettings {

    IOpenSettings setIsDisabled(Boolean isDisabled);

    /**
     * Sets the server URL.
     * @param serverUrl A string of the server's URL.
     * @return An updated clone of this settings object.
     */
    IOpenSettings setServerUrl(String serverUrl);

    /**
     * Sets the API key.
     * @param apiKey A string representing the API key.
     * @return An updated clone of this settings object.
     */
    IOpenSettings setApiKey(String apiKey);

    /**
     * Sets the proxy.
     * @param proxySettings the proxy settings.
     * @return An updated clone of this settings object.
     */
    IOpenSettings setProxy(AbstractProxySettings proxySettings);

    /**
     * Sets the connection timeout.
     * @param connectionTimeout Integer representing the timeout
     * @return An updated clone of this settings object.
     */
    IOpenSettings setConnectionTimeout(Integer connectionTimeout);

    /**
     * Sets if the session should be removed
     * @param removeSession should remove session.
     * @return An updated clone of this settings object.
     */
    IOpenSettings setRemoveSession(Boolean removeSession);

    IOpenSettings setAgentId(String agentId);

    /**
     * Sets the application name.
     * This is one of the baseline parameters.
     * @param appName the application's name
     * @return An updated clone of this settings object.
     */
    IOpenSettings setAppName(String appName);

    /**
     * Sets the test name
     * @param testName the test's name
     * @return An updated clone of this settings object.
     */
    IOpenSettings setTestName(String testName);

    IOpenSettings setDisplayName(String displayName);

    IOpenSettings setUserTestId(String userTestId);

    IOpenSettings setSessionType(SessionType sessionType);

    IOpenSettings setProperties(List<PropertyData> properties);

    void addProperty(String name, String value);

    IOpenSettings setBatch(BatchInfo batch);

    IOpenSettings setKeepBatchOpen(Boolean keepBatchOpen);

    IOpenSettings setEnvironmentName(String environmentName);

    IOpenSettings setEnvironment(AppEnvironment environment);

    IOpenSettings setBranchName(String branchName);

    IOpenSettings setParentBranchName(String parentBranchName);

    IOpenSettings setBaselineEnvName(String baselineEnvName);

    IOpenSettings setBaselineBranchName(String baselineBranchName);

    IOpenSettings setCompareWithParentBranch(Boolean compareWithParentBranch);

    IOpenSettings setIgnoreBaseline(Boolean ignoreBaseline);

    IOpenSettings setIgnoreGitBranching(Boolean ignoreGitBranching);

    IOpenSettings setSaveDiffs(Boolean saveDiffs);

    IOpenSettings setAbortIdleTestTimeout(Integer idleTestTimeout);

}
