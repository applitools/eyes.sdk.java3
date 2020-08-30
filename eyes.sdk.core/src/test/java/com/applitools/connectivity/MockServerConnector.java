package com.applitools.connectivity;

import com.applitools.connectivity.api.Response;
import com.applitools.eyes.*;
import com.applitools.eyes.visualgrid.model.*;
import org.apache.http.HttpStatus;

import java.util.*;

public class MockServerConnector extends ServerConnector {

    public boolean asExpected;
    public List<RenderRequest> renderRequests = new ArrayList<>();

    @Override
    public void closeBatch(String batchId) {
    }

    @Override
    public void deleteSession(TestResults testResults) {
        logger.log(String.format("deleting session: %s", testResults.getId()));
    }

    @Override
    public TestResults stopSession(RunningSession runningSession, boolean isAborted, boolean save) {
        logger.log(String.format("ending session: %s", runningSession.getSessionId()));
        return new TestResults();
    }

    @Override
    public Response uploadData(byte[] bytes, RenderingInfo renderingInfo, final String targetUrl, String contentType, final String mediaType) {
        return new Response(logger) {
            @Override
            public int getStatusCode() {
                return HttpStatus.SC_OK;
            }

            @Override
            public String getStatusPhrase() {
                return "";
            }

            @Override
            public String getHeader(String s, boolean b) {
                return "";
            }

            @Override
            protected void readEntity() {

            }

            @Override
            public void close() {

            }
        };
    }

    @Override
    public RenderingInfo getRenderInfo() {
        return new RenderingInfo("", "", "", "", 0 , 0);
    }

    @Override
    public List<RunningRender> render(RenderRequest... renderRequests) {
        this.renderRequests.addAll(Arrays.asList(renderRequests));
        RunningRender runningRender = new RunningRender();
        runningRender.setRenderId(UUID.randomUUID().toString());
        runningRender.setRenderStatus(RenderStatus.RENDERED);
        return Collections.singletonList(runningRender);
    }

    @Override
    public List<RenderStatusResults> renderStatusById(String... renderIds) {
        RenderStatusResults renderStatusResults = new RenderStatusResults();
        renderStatusResults.setRenderId(renderIds[0]);
        renderStatusResults.setStatus(RenderStatus.RENDERED);
        return Collections.singletonList(renderStatusResults);
    }

    @Override
    public MatchResult matchWindow(RunningSession runningSession, MatchWindowData data) {
        MatchResult result = new MatchResult();
        result.setAsExpected(this.asExpected);
        return result;
    }

    @Override
    public RunningSession startSession(SessionStartInfo sessionStartInfo)
    {
        logger.log(String.format("starting session: %s", sessionStartInfo));

        RunningSession newSession = new RunningSession();
        newSession.setIsNew(false);
        newSession.setSessionId(UUID.randomUUID().toString());
        return newSession;
    }

    @Override
    public Map<String, DeviceSize> getDevicesSizes(String path)
    {
        return new HashMap<>();
    }

    @Override
    public Map<String, String> getUserAgents()
    {
        return new HashMap<>();
    }
}
