package com.applitools.eyes.config;

import com.applitools.eyes.*;

import java.net.URI;

public interface IConfigurationGetter {
    boolean getSaveNewTests();

    boolean getSaveFailedTests();

    ImageMatchSettings getDefaultMatchSettings();

    int getMatchTimeout();

    String getHostApp();

    String getHostOS();

    int getStitchOverlap();

    BatchInfo getBatch();

    String getBranchName();

    String getAgentId();

    String getParentBranchName();

    String getBaselineBranchName();

    String getBaselineEnvName();

    String getEnvironmentName();

    Boolean getSaveDiffs();

    String getAppName();

    String getTestName();

    RectangleSize getViewportSize();

    SessionType getSessionType();

    FailureReports getFailureReports();

    Boolean isSendDom();

    boolean getIgnoreCaret();

    String getApiKey();

    URI getServerUrl();

    AbstractProxySettings getProxy();

    MatchLevel getMatchLevel();

    boolean getIgnoreDisplacements();

    AccessibilityLevel getAccessibilityValidation();

    boolean getUseDom();

    boolean getEnablePatterns();
}
