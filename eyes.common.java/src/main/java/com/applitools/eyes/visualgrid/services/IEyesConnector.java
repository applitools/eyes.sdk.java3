package com.applitools.eyes.visualgrid.services;

import com.applitools.ICheckSettings;
import com.applitools.eyes.*;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.visualgrid.model.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.Future;

public interface IEyesConnector {
    void log(String massage);

    com.applitools.eyes.config.Configuration setProxy(AbstractProxySettings proxy);

    void setLogHandler(LogHandler logHandler);

    com.applitools.eyes.config.Configuration setServerUrl(String serverUrl) throws URISyntaxException;

    URI getServerUrl();

    void open(Configuration configuration, String appName, String testName);

    TestResults close(boolean throwExceptionOn);

    TestResults abortIfNotClosed();

    Future<?> getResource(URI url, String userAgent, String refererUrl, TaskListener<RGridResource> listener);

    RenderingInfo getRenderingInfo();

    Future<?> renderPutResource(RunningRender runningRender, RGridResource resource, String userAgent, TaskListener<Boolean> listener);

    List<RunningRender> render(RenderRequest... renderRequests);

    List<RenderStatusResults> renderStatusById(String... renderIds);

    MatchResult matchWindow(String resultImageURL, String domLocation, ICheckSettings checkSettings,
                            List<? extends IRegion> regions, List<VisualGridSelector[]> regionSelectors, Location location,
                            String renderId, String source);

    void setRenderInfo(RenderingInfo renderingInfo);

    com.applitools.eyes.config.Configuration setBatch(BatchInfo batchInfo);

    void setUserAgent(String userAgent);

    String getApiKey();

    com.applitools.eyes.config.Configuration setApiKey(String apiKey);

    void setBranchName(String branchName);

    void setParentBranchName(String parentBranchName);

    void setDevice(String device);

    RectangleSize getDeviceSize();

    void setDeviceSize(RectangleSize deviceSize);

    RunningSession getSession();

    void addProperty(String name, String value);

    void clearProperties();
}
