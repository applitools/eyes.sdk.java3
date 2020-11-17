package com.applitools.eyes.visualgrid.services;

import com.applitools.eyes.AbstractProxySettings;
import com.applitools.eyes.IBatchCloser;
import com.applitools.eyes.Logger;
import com.applitools.eyes.TestResultContainer;

import java.net.URI;
import java.util.List;
import java.util.Map;

public interface IRenderingEyes {

    boolean isEyesClosed();

    Logger getLogger();

    IBatchCloser getBatchCloser();

    String getBatchId();

    Map<String, RunningTest> getAllRunningTests();

    List<TestResultContainer> getAllTestResults();

    boolean isCompleted();

    URI getServerUrl();

    String getApiKey();

    AbstractProxySettings getProxy();
}
