package com.applitools.eyes.config;

import com.applitools.eyes.*;

import java.net.URI;

public interface IConfiguration
{
    boolean getSaveNewTests();

    Configuration setSaveNewTests(boolean saveNewTests);

    boolean getSaveFailedTests();

    Configuration setSaveFailedTests(boolean saveFailedTests);

    ImageMatchSettings getDefaultMatchSettings();

    Configuration setDefaultMatchSettings(ImageMatchSettings defaultMatchSettings);

    int getMatchTimeout();

    Configuration setMatchTimeout(int matchTimeout);

    String getHostApp();

    Configuration setHostApp(String hostApp);

    String getHostOS();

    Configuration setHostOS(String hostOS);

    int getStitchOverlap();

    Configuration setStitchOverlap(int stitchingOverlap);

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

    Configuration setSendDom(boolean sendDom);

    boolean getIgnoreCaret();

    Configuration setIgnoreCaret(boolean value);

    String getApiKey();

    Configuration setApiKey(String apiKey);

    URI getServerUrl();

    Configuration setServerUrl(String serverUrl);

    AbstractProxySettings getProxy();

    Configuration setProxy(AbstractProxySettings proxy);

    MatchLevel getMatchLevel();

    Configuration setMatchLevel(MatchLevel matchLevel);

    boolean getIgnoreDisplacements();

    Configuration setIgnoreDisplacements(boolean isIgnoreDisplacements);

    AccessibilitySettings getAccessibilityValidation();

    Configuration setAccessibilityValidation(AccessibilitySettings accessibilitySettings);

    boolean getUseDom();

    Configuration setUseDom(boolean useDom);

    boolean getEnablePatterns();

    Configuration setEnablePatterns(boolean enablePatterns);

    int getTimeToWaitForOpen();

    Configuration setTimeToWaitForOpen(int timeToWaitForOpen);
}
