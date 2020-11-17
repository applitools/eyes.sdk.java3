package com.applitools.eyes.services;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.EyesException;
import com.applitools.eyes.Logger;
import com.applitools.eyes.TaskListener;
import com.applitools.eyes.visualgrid.model.RenderRequest;
import com.applitools.eyes.visualgrid.model.RenderStatus;
import com.applitools.eyes.visualgrid.model.RenderStatusResults;
import com.applitools.eyes.visualgrid.model.RunningRender;
import com.applitools.utils.GeneralUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class RenderService extends EyesService<RenderRequest, RenderStatusResults> {
    int RENDER_STATUS_POLLING_TIMEOUT = 60 * 60 * 1000;

    private final AtomicBoolean isTimeElapsed = new AtomicBoolean(false);

    // Queue for tests that are in a render process
    private final List<Pair<String, String>> renderingQueue = Collections.synchronizedList(new ArrayList<Pair<String, String>>());

    private class TimeoutTask extends TimerTask {
        @Override
        public void run() {
            logger.verbose("Rendering task is Timed out!");
            isTimeElapsed.set(true);
        }
    }

    public RenderService(Logger logger, ServerConnector serverConnector) {
        super(logger, serverConnector);
    }

    @Override
    public void run() {
        sendAllRenderRequests();

        if (renderingQueue.isEmpty()) {
            return;
        }

        List<Pair<String, String>> list;
        synchronized (renderingQueue) {
            list = new ArrayList<>(renderingQueue);
            renderingQueue.clear();
        }

        final List<String> testIds = new ArrayList<>();
        final List<String> renderIds = new ArrayList<>();
        for (Pair<String, String> pair : list) {
            testIds.add(pair.getLeft());
            renderIds.add(pair.getRight());
        }

        try {
            pollRenderingStatus(testIds, renderIds);
        } catch (Throwable t) {
            setRenderErrorToTasks(testIds, t);
        }
    }

    private void sendAllRenderRequests() {
        if (inputQueue.isEmpty()) {
            return;
        }

        final RenderRequest[] asArray = new RenderRequest[inputQueue.size()];
        final List<String> testIds = new ArrayList<>();
        for (int i = 0; i < inputQueue.size(); i++) {
            asArray[i] = inputQueue.get(i).getRight();
            testIds.add(inputQueue.get(i).getLeft());
        }
        inputQueue.clear();

        final TaskListener<List<RunningRender>> renderListener = new TaskListener<List<RunningRender>>() {
            @Override
            public void onComplete(List<RunningRender> runningRenders) {
                if (runningRenders == null || runningRenders.size() != asArray.length) {
                    onFail();
                    return;
                }

                try {
                    for (RunningRender runningRender : runningRenders) {
                        logger.verbose(String.format("RunningRender: %s", runningRender));
                        RenderStatus renderStatus = runningRender.getRenderStatus();
                        if (!renderStatus.equals(RenderStatus.RENDERED) && !renderStatus.equals(RenderStatus.RENDERING)) {
                            setRenderErrorToTasks(testIds, new EyesException(
                                    String.format("Invalid response for render request. Status: %s", renderStatus)));
                            return;
                        }
                    }

                    for (int i = 0; i < runningRenders.size(); i++) {
                        renderingQueue.add(Pair.of(testIds.get(i), runningRenders.get(i).getRenderId()));
                    }
                } catch (Throwable t) {
                    setRenderErrorToTasks(testIds, t);
                }
            }

            @Override
            public void onFail() {
                setRenderErrorToTasks(testIds, new EyesException("Invalid response for render request"));
            }
        };

        try {
            serverConnector.render(renderListener, asArray);
        } catch (Throwable t) {
            setRenderErrorToTasks(testIds, t);
        }
    }

    private void pollRenderingStatus(final List<String> testIds, final List<String> renderIds) {
        logger.verbose("renderIds : " + renderIds);
        final Timer timer = new Timer("VG_StopWatch", true);
        timer.schedule(new TimeoutTask(), RENDER_STATUS_POLLING_TIMEOUT);
        serverConnector.renderStatusById(new TaskListener<List<RenderStatusResults>>() {
            @Override
            public void onComplete(List<RenderStatusResults> renderStatusResultsList) {
                if (renderStatusResultsList == null || renderStatusResultsList.size() != renderIds.size()) {
                    onFail();
                    return;
                }

                for (int i = 0; i < renderStatusResultsList.size(); i++) {
                    RenderStatusResults renderStatusResults = renderStatusResultsList.get(i);
                    if (renderStatusResults == null) {
                        renderStatusResults = RenderStatusResults.createError(renderIds.get(i));
                    }

                    logger.verbose(String.format("renderStatusResults: %s", renderStatusResults));
                    RenderStatus renderStatus = renderStatusResults.getStatus();
                    if (!renderStatus.equals(RenderStatus.RENDERED) && !renderStatus.equals(RenderStatus.ERROR)) {
                        continue;
                    }

                    String renderId = renderIds.get(i);
                    String testId = testIds.get(i);
                    logger.verbose(String.format("Setting render result. TestId: %s, RenderId: %s, Result: %s", testId, renderId, renderStatusResults));
                    String error = renderStatusResults.getError();
                    if (error != null) {
                        synchronized (errorQueue) {
                            Throwable t = new EyesException(error);
                            errorQueue.add(Pair.of(testId, t));
                        }
                    } else {
                        synchronized (outputQueue) {
                            outputQueue.add(Pair.of(testId, renderStatusResults));
                        }
                    }

                    renderIds.remove(i);
                    testIds.remove(i);
                    renderStatusResultsList.remove(i);
                    i--;
                }

                if (renderIds.isEmpty()) {
                    timer.cancel();
                    return;
                }

                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                }

                if (!isTimeElapsed.get()) {
                    serverConnector.renderStatusById(this, renderIds.toArray(new String[0]));
                    return;
                }

                onFail();
            }

            @Override
            public void onFail() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                }

                if (!renderIds.isEmpty() && !isTimeElapsed.get()) {
                    serverConnector.renderStatusById(this, renderIds.toArray(new String[0]));
                    return;
                }

                timer.cancel();
                if (renderIds.isEmpty()) {
                    return;
                }

                logger.verbose(String.format("renderIds that didn't complete in time: %s", Arrays.toString(renderIds.toArray())));
                for (int i = 0; i < testIds.size(); i++) {
                    String renderId = renderIds.get(i);
                    String testId = testIds.get(i);
                    Throwable t = new EyesException(String.format("Render timeout. TestId: %s, RenderId: %s", testId, renderId));
                    synchronized (errorQueue) {
                        errorQueue.add(Pair.of(testId, t));
                    }
                }
            }
        }, renderIds.toArray(new String[0]));
    }

    private void setRenderErrorToTasks(List<String> testIds, Throwable t) {
        synchronized (errorQueue) {
            for (String id : testIds) {
                errorQueue.add(Pair.of(id, t));
            }
        }
    }
}
