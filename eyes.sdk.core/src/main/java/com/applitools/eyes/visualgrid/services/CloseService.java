package com.applitools.eyes.visualgrid.services;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.*;
import org.apache.commons.lang3.tuple.Pair;

public class CloseService extends EyesService<SessionStopInfo, TestResults> {

    public CloseService(Logger logger, ServerConnector serverConnector) {
        super(logger, serverConnector);
    }

    @Override
    public void run() {
        while (!inputQueue.isEmpty()) {
            final Pair<String, SessionStopInfo> nextInput = inputQueue.remove(0);
            operate(nextInput.getRight(), new ServiceTaskListener<TestResults>() {
                @Override
                public void onComplete(TestResults output) {
                    outputQueue.add(Pair.of(nextInput.getLeft(), output));
                }

                @Override
                public void onFail(Throwable t) {
                    logger.log(String.format("Failed completing task on input %s", nextInput));
                    errorQueue.add(Pair.of(nextInput.getLeft(), t));
                }
            });
        }
    }

    private void operate(final SessionStopInfo sessionStopInfo, final ServiceTaskListener<TestResults> listener) {
        if (sessionStopInfo == null) {
            TestResults testResults = new TestResults();
            testResults.setStatus(TestResultsStatus.NotOpened);
            listener.onComplete(testResults);
            return;
        }

        TaskListener<TestResults> taskListener = new TaskListener<TestResults>() {
            @Override
            public void onComplete(TestResults testResults) {
                logger.log("Session stopped successfully");
                testResults.setNew(sessionStopInfo.getRunningSession().getIsNew());
                testResults.setUrl(sessionStopInfo.getRunningSession().getUrl());
                logger.verbose(testResults.toString());
                testResults.setServerConnector(serverConnector);
                listener.onComplete(testResults);
            }

            @Override
            public void onFail() {
                listener.onFail(new EyesException("Failed closing test"));
            }
        };

        try {
            serverConnector.stopSession(taskListener, sessionStopInfo);
        } catch (Throwable t) {
            listener.onFail(t);
        }
    }
}
