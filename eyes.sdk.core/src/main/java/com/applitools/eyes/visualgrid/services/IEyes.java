package com.applitools.eyes.visualgrid.services;

import com.applitools.ICheckSettings;
import com.applitools.eyes.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

public interface IEyes extends IEyesBase {

    void check(ICheckSettings checkSettings);

    void check(ICheckSettings... checkSettings);

    void check(String testName, ICheckSettings checkSettings);

    TestResults close(boolean throwEx);

    void serverUrl(String serverUrl);

    void apiKey(String apiKey);

    void proxy(AbstractProxySettings proxySettings);

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
