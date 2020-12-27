package com.applitools.eyes.services;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.*;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.TraceLevel;
import com.applitools.eyes.logging.Type;
import com.applitools.eyes.visualgrid.services.ServiceTaskListener;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class CheckService extends EyesService<MatchWindowData, MatchResult> {

    // Queue for tests that finished uploading and waiting for match window
    private final List<Pair<String, MatchWindowData>> matchWindowQueue = Collections.synchronizedList(new ArrayList<Pair<String, MatchWindowData>>());

    private final Set<String> inUploadProcess = Collections.synchronizedSet(new HashSet<String>());
    private final Set<String> inMatchWindowProcess = Collections.synchronizedSet(new HashSet<String>());

    public CheckService(Logger logger, ServerConnector serverConnector) {
        super(logger, serverConnector);
    }

    @Override
    public void run() {
        while (!inputQueue.isEmpty()) {
            final Pair<String, MatchWindowData> nextInput = inputQueue.remove(0);
            final MatchWindowData matchWindowData = nextInput.getRight();
            inUploadProcess.add(nextInput.getLeft());
            tryUploadImage(nextInput.getLeft(), matchWindowData, new ServiceTaskListener<Void>() {
                @Override
                public void onComplete(Void output) {
                    inUploadProcess.remove(nextInput.getLeft());
                    matchWindowQueue.add(Pair.of(nextInput.getLeft(), matchWindowData));
                }

                @Override
                public void onFail(Throwable t) {
                    inUploadProcess.remove(nextInput.getLeft());
                    errorQueue.add(Pair.of(nextInput.getLeft(), t));
                }
            });
        }

        while (!matchWindowQueue.isEmpty()) {
            final Pair<String, MatchWindowData> nextInput = matchWindowQueue.remove(0);
            final MatchWindowData matchWindowData = nextInput.getRight();
            inMatchWindowProcess.add(nextInput.getLeft());
            ServiceTaskListener<MatchResult> listener = new ServiceTaskListener<MatchResult>() {
                @Override
                public void onComplete(MatchResult taskResponse) {
                    inMatchWindowProcess.remove(nextInput.getLeft());
                    outputQueue.add(Pair.of(nextInput.getLeft(), taskResponse));
                }

                @Override
                public void onFail(Throwable t) {
                    inMatchWindowProcess.remove(nextInput.getLeft());
                    errorQueue.add(Pair.of(nextInput.getLeft(), t));
                }
            };

            matchWindow(nextInput.getLeft(), matchWindowData, listener);
        }
    }

    public void tryUploadImage(final String testId, MatchWindowData data, final ServiceTaskListener<Void> taskListener) {
        final AppOutput appOutput = data.getAppOutput();
        if (appOutput.getScreenshotUrl() != null) {
            taskListener.onComplete(null);
            return;
        }

        // Getting the screenshot bytes
        TaskListener<String> uploadListener = new TaskListener<String>() {
            @Override
            public void onComplete(String s) {
                if (s == null) {
                    onFail();
                    return;
                }

                logger.log(TraceLevel.Info, Collections.singleton(testId), Stage.CHECK, Type.UPLOAD_COMPLETE, Pair.of("url", s));
                appOutput.setScreenshotUrl(s);
                taskListener.onComplete(null);
            }

            @Override
            public void onFail() {
                appOutput.setScreenshotUrl(null);
                taskListener.onFail(new EyesException("Failed uploading image"));
            }
        };

        try {
            logger.log(TraceLevel.Info, Collections.singleton(testId), Stage.CHECK, Type.UPLOAD_START, Pair.of("matchWindowData", matchWindowQueue));
            serverConnector.uploadImage(uploadListener, appOutput.getScreenshotBytes());
        } catch (Throwable t) {
            taskListener.onFail(t);
        }
    }

    public void matchWindow(final String testId, MatchWindowData data, final ServiceTaskListener<MatchResult> listener) {
        try {
            logger.log(TraceLevel.Info, Collections.singleton(testId), Stage.CHECK, Type.MATCH_START, Pair.of("matchWindowData", data));
            serverConnector.matchWindow(new TaskListener<MatchResult>() {
                @Override
                public void onComplete(MatchResult taskResponse) {
                    logger.log(testId, Stage.CHECK, Type.MATCH_COMPLETE, Pair.of("matchResult", taskResponse));
                    listener.onComplete(taskResponse);
                }

                @Override
                public void onFail() {
                    listener.onFail(new EyesException("Match window failed"));
                }
            }, data);
        } catch (Throwable t) {
            listener.onFail(t);
        }
    }
}
