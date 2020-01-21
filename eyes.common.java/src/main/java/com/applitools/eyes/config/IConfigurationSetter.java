package com.applitools.eyes.config;

import com.applitools.eyes.*;

public interface IConfigurationSetter {

    IConfigurationSetter setSaveNewTests(boolean saveNewTests);

    IConfigurationSetter setSaveFailedTests(boolean saveFailedTests);

    IConfigurationSetter setDefaultMatchSettings(ImageMatchSettings defaultMatchSettings);

    IConfigurationSetter setMatchTimeout(int matchTimeout);

    IConfigurationSetter setHostApp(String hostApp);

    IConfigurationSetter setHostOS(String hostOS);

    IConfigurationSetter setStitchOverlap(int stitchingOverlap);

    IConfigurationSetter setBatch(BatchInfo batch);

    IConfigurationSetter setBranchName(String branchName);

    IConfigurationSetter setAgentId(String agentId);

    IConfigurationSetter setParentBranchName(String parentBranchName);

    IConfigurationSetter setBaselineBranchName(String baselineBranchName);

    IConfigurationSetter setBaselineEnvName(String baselineEnvName);

    IConfigurationSetter setEnvironmentName(String environmentName);

    IConfigurationSetter setSaveDiffs(Boolean saveDiffs);

    IConfigurationSetter setAppName(String appName);

    IConfigurationSetter setTestName(String testName);

    IConfigurationSetter setViewportSize(RectangleSize viewportSize);

    IConfigurationSetter setSessionType(SessionType sessionType);

    IConfigurationSetter setFailureReports(FailureReports failureReports);

    IConfigurationSetter setSendDom(boolean sendDom);

    IConfigurationSetter setIgnoreCaret(boolean value);

    IConfigurationSetter setApiKey(String apiKey);

    IConfigurationSetter setServerUrl(String serverUrl);

    IConfigurationSetter setProxy(AbstractProxySettings proxy);

    IConfigurationSetter setMatchLevel(MatchLevel matchLevel);

    IConfigurationSetter setIgnoreDisplacements(boolean isIgnoreDisplacements);

    IConfigurationSetter setAccessibilityValidation(AccessibilityLevel accessibilityValidation);

    IConfigurationSetter setUseDom(boolean useDom);

    IConfigurationSetter setEnablePatterns(boolean enablePatterns);
}
