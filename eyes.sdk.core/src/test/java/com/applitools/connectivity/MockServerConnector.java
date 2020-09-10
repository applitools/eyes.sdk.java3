package com.applitools.connectivity;

import com.applitools.eyes.*;
import com.applitools.eyes.visualgrid.model.*;

import java.net.URI;
import java.util.*;
import java.util.concurrent.Future;

public class MockServerConnector extends ServerConnector {

    public boolean asExpected;
    public List<RenderRequest> renderRequests = new ArrayList<>();

    @Override
    public void closeBatch(String batchId) {
        logger.log(String.format("closing batch: %s", batchId));
    }

    @Override
    public void deleteSession(final TaskListener<Void> listener, TestResults testResults) {
        logger.log(String.format("deleting session: %s", testResults.getId()));
        listener.onComplete(null);
    }

    @Override
    public void stopSession(final TaskListener<TestResults> listener, RunningSession runningSession, boolean isAborted, boolean save) {
        logger.log(String.format("ending session: %s", runningSession.getSessionId()));
        TestResults testResults = new TestResults();
        testResults.setStatus(TestResultsStatus.Passed);
        listener.onComplete(testResults);
    }

    @Override
    public void uploadData(final TaskListener<String> listener, final byte[] bytes, final String contentType, final String mediaType) {
        listener.onComplete("");
    }

    @Override
    public RenderingInfo getRenderInfo() {
        return new RenderingInfo("", "", "", "", 0 , 0);
    }

    @Override
    public void render(final TaskListener<List<RunningRender>> listener, RenderRequest... renderRequests) {
        this.renderRequests.addAll(Arrays.asList(renderRequests));

        List<RunningRender> runningRenders = new ArrayList<>();
        for (int i = 0; i < renderRequests.length; i++) {
            final RunningRender runningRender = new RunningRender();
            runningRender.setRenderId(UUID.randomUUID().toString());
            runningRender.setRenderStatus(RenderStatus.RENDERED);
            runningRenders.add(runningRender);
        }

        listener.onComplete(runningRenders);
    }

    @Override
    public void renderStatusById(final TaskListener<List<RenderStatusResults>> listener, String... renderIds) {
        final RenderStatusResults renderStatusResults = new RenderStatusResults();
        renderStatusResults.setRenderId(renderIds[0]);
        renderStatusResults.setStatus(RenderStatus.RENDERED);
        renderStatusResults.setDomLocation("https://dom.com");
        renderStatusResults.setImageLocation("https://image.com");
        listener.onComplete(Collections.singletonList(renderStatusResults));
    }

    @Override
    public void matchWindow(final TaskListener<MatchResult> listener, RunningSession runningSession, MatchWindowData data) {
        final MatchResult result = new MatchResult();
        result.setAsExpected(this.asExpected);
        listener.onComplete(result);
    }

    @Override
    public void startSession(final TaskListener<RunningSession> listener, SessionStartInfo sessionStartInfo) {
        logger.log(String.format("starting session: %s", sessionStartInfo));

        final RunningSession newSession = new RunningSession();
        newSession.setIsNew(false);
        newSession.setSessionId(UUID.randomUUID().toString());
        listener.onComplete(newSession);
    }

    @Override
    public void checkResourceStatus(final TaskListener<Boolean[]> listener, String renderId, HashObject... hashes) {
        listener.onComplete(new Boolean[0]);
    }

    @Override
    public Future<?> renderPutResource(final String renderID, final RGridResource resource,
                                       final TaskListener<Void> listener) {
        listener.onComplete(null);
        return null;
    }

    @Override
    public Future<?> downloadResource(final URI url, final String userAgent, final String refererUrl,
                                      final TaskListener<RGridResource> listener) {
        listener.onComplete(RGridResource.createEmpty(url.toString()));
        return null;
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

    @Override
    public void getJobInfo(TaskListener<JobInfo[]> listener, RenderRequest[] browserInfos) {
        listener.onComplete(new JobInfo[]{new JobInfo()});
    }
}
