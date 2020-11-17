package com.applitools.eyes.services;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.*;
import com.applitools.eyes.visualgrid.services.ServiceTaskListener;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CloseService extends EyesService<SessionStopInfo, TestResults> {

    private final Set<String> inProgressTests = Collections.synchronizedSet(new HashSet<String>());

    public CloseService(Logger logger, ServerConnector serverConnector) {
        super(logger, serverConnector);
    }

    @Override
    public void run() {
        while (!inputQueue.isEmpty()) {
            final Pair<String, SessionStopInfo> nextInput = inputQueue.remove(0);
            inProgressTests.add(nextInput.getLeft());
            operate(nextInput.getRight(), new ServiceTaskListener<TestResults>() {
                @Override
                public void onComplete(TestResults output) {
                    inProgressTests.remove(nextInput.getLeft());
                    outputQueue.add(Pair.of(nextInput.getLeft(), output));
                }

                @Override
                public void onFail(Throwable t) {
                    inProgressTests.remove(nextInput.getLeft());
                    logger.log(String.format("Failed completing task on input %s", nextInput));
                    errorQueue.add(Pair.of(nextInput.getLeft(), t));
                }
            });
        }
    }

    public void operate(final SessionStopInfo sessionStopInfo, final ServiceTaskListener<TestResults> listener) {
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
