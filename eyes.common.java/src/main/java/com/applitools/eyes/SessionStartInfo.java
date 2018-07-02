/*
 * Applitools software.
 */
package com.applitools.eyes;

import com.applitools.eyes.config.Configuration;
import com.applitools.utils.ArgumentGuard;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.List;

/**
 * Encapsulates data required to start session using the Session API.
 */

/*
 * We name the root value "startInfo" since this is a requirement of
 * the agent's web API.
 */
@SuppressWarnings("UnusedDeclaration")
@JsonRootName(value = "startInfo")
public class SessionStartInfo {
    private String agentId;
    private SessionType sessionType;
    private String appIdOrName;
    private String verId;
    private String scenarioIdOrName;
    private BatchInfo batchInfo;
    private String baselineEnvName;
    private String environmentName;
    private AppEnvironment environment;
    private String branchName;
    private String parentBranchName;
    private String baselineBranchName;
    private Boolean saveDiffs;
    private ImageMatchSettings defaultMatchSettings;
    private List<PropertyData> properties;

    public SessionStartInfo(String agentId, SessionType sessionType,
                            String appIdOrName, String verId,
                            String scenarioIdOrName, BatchInfo batchInfo,
                            String baselineEnvName, String environmentName,
                            AppEnvironment environment,
                            ImageMatchSettings defaultMatchSettings,
                            String branchName, String parentBranchName, String baselineBranchName,
                            Boolean saveDiffs,
                            List<PropertyData> properties) {
        ArgumentGuard.notNullOrEmpty(agentId, "agentId");
        ArgumentGuard.notNullOrEmpty(appIdOrName, "appIdOrName");
        ArgumentGuard.notNullOrEmpty(scenarioIdOrName, "scenarioIdOrName");
        ArgumentGuard.notNull(batchInfo, "batchInfo");
        ArgumentGuard.notNull(environment, "environment");
        ArgumentGuard.notNull(defaultMatchSettings, "defaultMatchSettings");
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
    }


    public SessionStartInfo(Configuration config,
                            String agentId,
                            String verId,
                            AppEnvironment environment,
                            ImageMatchSettings defaultMatchSettings,
                            List<PropertyData> properties) {
        ArgumentGuard.notNull(config, "config");
        ArgumentGuard.notNullOrEmpty(agentId, "agentId");
        ArgumentGuard.notNull(environment, "environment");
        ArgumentGuard.notNull(defaultMatchSettings, "defaultMatchSettings");
        this.agentId = agentId;
        this.sessionType = config.getSessionType();
        this.appIdOrName = config.getAppName();
        this.verId = verId;
        this.scenarioIdOrName = config.getTestName();
        this.batchInfo = config.getBatch();
        this.baselineEnvName = config.getBaselineEnvName();
        this.environmentName = config.getEnvironmentName();
        this.environment = environment;
        this.defaultMatchSettings = defaultMatchSettings;
        this.branchName = config.getBranchName();
        this.parentBranchName = config.getParentBranchName();
        this.baselineBranchName = config.getBaselineBranchName();
        this.saveDiffs = config.getSaveDiffs();
        this.properties = properties;
    }

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

    public AppEnvironment getEnvironment() {
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
}
