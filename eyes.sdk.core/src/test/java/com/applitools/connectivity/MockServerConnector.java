package com.applitools.connectivity;

import com.applitools.connectivity.api.AsyncRequestCallback;
import com.applitools.connectivity.api.Response;
import com.applitools.eyes.*;
import com.applitools.eyes.visualgrid.model.*;
import org.apache.http.HttpStatus;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MockServerConnector extends ServerConnector {

    public boolean asExpected;
    public List<RenderRequest> renderRequests = new ArrayList<>();
    ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public void closeBatch(String batchId) {
    }

    @Override
    public void deleteSession(final TaskListener<Void> listener, TestResults testResults) {
        logger.log(String.format("deleting session: %s", testResults.getId()));
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                listener.onComplete(null);
            }
        });
    }

    @Override
    public void stopSession(final TaskListener<TestResults> listener, RunningSession runningSession, boolean isAborted, boolean save) {
        logger.log(String.format("ending session: %s", runningSession.getSessionId()));
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                listener.onComplete(new TestResults());
            }
        });
    }

    @Override
    public void uploadData(final AsyncRequestCallback callback, byte[] bytes, RenderingInfo renderingInfo, final String targetUrl, String contentType, final String mediaType) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                callback.onComplete(new Response(logger) {
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
                });
            }
        });
    }

    @Override
    public RenderingInfo getRenderInfo() {
        return new RenderingInfo("", "", "", "", 0 , 0);
    }

    @Override
    public void render(final TaskListener<List<RunningRender>> listener, RenderRequest... renderRequests) {
        this.renderRequests.addAll(Arrays.asList(renderRequests));
        final RunningRender runningRender = new RunningRender();
        runningRender.setRenderId(UUID.randomUUID().toString());
        runningRender.setRenderStatus(RenderStatus.RENDERED);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                listener.onComplete(Collections.singletonList(runningRender));
            }
        });
    }

    @Override
    public void renderStatusById(final TaskListener<List<RenderStatusResults>> listener, String... renderIds) {
        final RenderStatusResults renderStatusResults = new RenderStatusResults();
        renderStatusResults.setRenderId(renderIds[0]);
        renderStatusResults.setStatus(RenderStatus.RENDERED);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                listener.onComplete(Collections.singletonList(renderStatusResults));
            }
        });
    }

    @Override
    public void matchWindow(final TaskListener<MatchResult> listener, RunningSession runningSession, MatchWindowData data) {
        final MatchResult result = new MatchResult();
        result.setAsExpected(this.asExpected);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                listener.onComplete(result);
            }
        });
    }

    @Override
    public void startSession(final TaskListener<RunningSession> listener, SessionStartInfo sessionStartInfo) {
        logger.log(String.format("starting session: %s", sessionStartInfo));

        final RunningSession newSession = new RunningSession();
        newSession.setIsNew(false);
        newSession.setSessionId(UUID.randomUUID().toString());
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                listener.onComplete(newSession);
            }
        });
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
