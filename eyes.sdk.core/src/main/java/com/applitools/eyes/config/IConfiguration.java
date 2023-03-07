package com.applitools.eyes.config;

import com.applitools.eyes.*;

import java.net.URI;

public interface IConfiguration {
    Boolean getSaveNewTests();

    Configuration setSaveNewTests(Boolean saveNewTests);

    Boolean getSaveFailedTests();

    Configuration setSaveFailedTests(Boolean saveFailedTests);

    ImageMatchSettings getDefaultMatchSettings();

    Configuration setDefaultMatchSettings(ImageMatchSettings defaultMatchSettings);

    Integer getMatchTimeout();

    Configuration setMatchTimeout(Integer matchTimeout);

    String getHostApp();

    Configuration setHostApp(String hostApp);

    String getHostOS();

    Configuration setHostOS(String hostOS);

    Integer getStitchOverlap();

    StitchOverlap getOverlap();

    Configuration setStitchOverlap(Integer stitchingOverlap);

    Configuration setStitchOverlap(StitchOverlap stitchOverlap);

    BatchInfo getBatch();

    Configuration setBatch(BatchInfo batch);

    String getBranchName();

    Configuration setBranchName(String branchName);

    String getAgentId();

    Configuration setAgentId(String agentId);

    String getParentBranchName();

    Configuration setParentBranchName(String parentBranchName);

    String getBaselineBranchName();

    Configuration setBaselineBranchName(String baselineBranchName);

    String getBaselineEnvName();

    String getEnvironmentName();

    Configuration setEnvironmentName(String environmentName);

    Configuration setBaselineEnvName(String baselineEnvName);

    Boolean getSaveDiffs();

    Configuration setSaveDiffs(Boolean saveDiffs);

    String getAppName();

    Configuration setAppName(String appName);

    String getTestName();

    Configuration setTestName(String testName);

    RectangleSize getViewportSize();

    Configuration setViewportSize(RectangleSize viewportSize);

    SessionType getSessionType();

    Configuration setSessionType(SessionType sessionType);

    @Deprecated
    FailureReports getFailureReports();

    @Deprecated
    Configuration setFailureReports(FailureReports failureReports);

    Boolean isSendDom();

    Configuration setSendDom(Boolean sendDom);

    Boolean getIgnoreCaret();

    Configuration setIgnoreCaret(Boolean value);

    String getApiKey();

    Configuration setApiKey(String apiKey);

    URI getServerUrl();

    Configuration setServerUrl(String serverUrl);

    AbstractProxySettings getProxy();

    Configuration setProxy(AbstractProxySettings proxy);

    MatchLevel getMatchLevel();

    Configuration setMatchLevel(MatchLevel matchLevel);

    Boolean getIgnoreDisplacements();

    Configuration setIgnoreDisplacements(Boolean isIgnoreDisplacements);

    AccessibilitySettings getAccessibilityValidation();

    Configuration setAccessibilityValidation(AccessibilitySettings accessibilitySettings);

    Boolean getUseDom();

    Configuration setUseDom(Boolean useDom);

    Boolean getEnablePatterns();

    Configuration setEnablePatterns(Boolean enablePatterns);
}
