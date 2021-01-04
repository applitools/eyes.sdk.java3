package com.applitools.eyes.selenium;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.AbstractProxySettings;
import com.applitools.eyes.IBatchCloser;
import com.applitools.eyes.LogHandler;

public interface IClassicRunner {

    void setServerConnector(ServerConnector serverConnector);

    void setProxy(AbstractProxySettings proxySettings);

    void setLogHandler(LogHandler logHandler);

    void setServerUrl(String serverUrl);

    void setApiKey(String apiKey);

    void setAgentId(String agentId);

    void addBatch(String batchId, IBatchCloser batchCloser);
}
