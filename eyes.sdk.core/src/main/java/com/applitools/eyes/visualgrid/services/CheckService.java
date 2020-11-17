package com.applitools.eyes.visualgrid.services;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.*;
import com.applitools.utils.GeneralUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CheckService extends EyesService<MatchWindowData, MatchResult> {

    // Queue for tests that finished uploading and waiting for match window
    private final List<Pair<String, MatchWindowData>> matchWindowQueue = Collections.synchronizedList(new ArrayList<Pair<String, MatchWindowData>>());

    public CheckService(Logger logger, ServerConnector serverConnector) {
        super(logger, serverConnector);
    }

    @Override
    public void run() {
        while (!inputQueue.isEmpty()) {
            final Pair<String, MatchWindowData> nextInput = inputQueue.remove(0);
            final MatchWindowData matchWindowData = nextInput.getRight();
            tryUploadImage(matchWindowData, new ServiceTaskListener<Void>() {
                @Override
                public void onComplete(Void output) {
                    matchWindowQueue.add(Pair.of(nextInput.getLeft(), matchWindowData));
                }

                @Override
                public void onFail(Throwable t) {
                    logger.log(String.format("Failed completing task on input %s", nextInput));
                    errorQueue.add(Pair.of(nextInput.getLeft(), t));
                }
            });
        }

        while (!matchWindowQueue.isEmpty()) {
            final Pair<String, MatchWindowData> nextInput = matchWindowQueue.remove(0);
            final MatchWindowData matchWindowData = nextInput.getRight();
            TaskListener<MatchResult> listener = new TaskListener<MatchResult>() {
                @Override
                public void onComplete(MatchResult taskResponse) {
                    outputQueue.add(Pair.of(nextInput.getLeft(), taskResponse));
                }

                @Override
                public void onFail() {
                    logger.log(String.format("Failed completing task on input %s", nextInput));
                    errorQueue.add(Pair.of(nextInput.getLeft(), (Throwable) new EyesException("Match window failed")));
                }
            };

            try {
                serverConnector.matchWindow(listener, matchWindowData);
            } catch (Throwable t) {
                GeneralUtils.logExceptionStackTrace(logger, t);
                listener.onFail();
            }
        }
    }

    private void tryUploadImage(MatchWindowData data, final ServiceTaskListener<Void> taskListener) {
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
                    logger.verbose("Got null url from upload");
                    onFail();
                    return;
                }

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
            serverConnector.uploadImage(uploadListener, appOutput.getScreenshotBytes());
        } catch (Throwable t) {
            taskListener.onFail(t);
        }
    }
}
