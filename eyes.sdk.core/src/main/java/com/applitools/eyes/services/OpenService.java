package com.applitools.eyes.services;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.*;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.TraceLevel;
import com.applitools.eyes.logging.Type;
import com.applitools.eyes.visualgrid.services.ServiceTaskListener;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class OpenService extends EyesService<SessionStartInfo, RunningSession> {
    int TIME_TO_WAIT_FOR_OPEN = 60 * 60 * 1000;

    private final int eyesConcurrency;
    private final AtomicInteger currentTestAmount = new AtomicInteger();
    private boolean isServerConcurrencyLimitReached = false;

    private final Set<String> inProgressTests = Collections.synchronizedSet(new HashSet<String>());

    public OpenService(Logger logger, ServerConnector serverConnector, int eyesConcurrency) {
        super(logger, serverConnector);
        this.eyesConcurrency = eyesConcurrency;
    }

    @Override
    public void run() {
        while (!inputQueue.isEmpty() && !isConcurrencyLimitReached()) {
            currentTestAmount.incrementAndGet();
            logger.log(TraceLevel.Info, new HashSet<String>(), Stage.OPEN, null, Pair.of("testAmount", currentTestAmount.get()));

            final Pair<String, SessionStartInfo> nextInput = inputQueue.remove(0);
            inProgressTests.add(nextInput.getLeft());
            operate(nextInput.getLeft(), nextInput.getRight(), new ServiceTaskListener<RunningSession>() {
                @Override
                public void onComplete(RunningSession output) {
                    inProgressTests.remove(nextInput.getLeft());
                    outputQueue.add(Pair.of(nextInput.getLeft(), output));
                }

                @Override
                public void onFail(Throwable t) {
                    inProgressTests.remove(nextInput.getLeft());
                    errorQueue.add(Pair.of(nextInput.getLeft(), t));
                }
            });
        }
    }

    public void operate(final String testId, final SessionStartInfo sessionStartInfo, final ServiceTaskListener<RunningSession> listener) {
        final AtomicInteger timePassed = new AtomicInteger(0);
        final AtomicInteger sleepDuration = new AtomicInteger(2 * 1000);
        TaskListener<RunningSession> taskListener = new TaskListener<RunningSession>() {
            @Override
            public void onComplete(RunningSession runningSession) {
                logger.log(testId, Stage.OPEN, Pair.of("runningSession", runningSession));
                if (runningSession.isConcurrencyFull()) {
                    isServerConcurrencyLimitReached = true;
                    onFail();
                    return;
                }

                isServerConcurrencyLimitReached = false;
                listener.onComplete(runningSession);
            }

            @Override
            public void onFail() {
                if (timePassed.get() > TIME_TO_WAIT_FOR_OPEN) {
                    isServerConcurrencyLimitReached = false;
                    listener.onFail(new EyesException("Timeout in start session"));
                    return;
                }

                try {
                    Thread.sleep(sleepDuration.get());
                    timePassed.set(timePassed.get() + sleepDuration.get());
                    if (timePassed.get() >= 30 * 1000) {
                        sleepDuration.set(10 * 1000);
                    } else if (timePassed.get() >= 10 * 1000) {
                        sleepDuration.set(5 * 1000);
                    }

                    logger.log(testId, Stage.OPEN, Type.RETRY);
                    serverConnector.startSession(this, sessionStartInfo);
                } catch (Throwable e) {
                    listener.onFail(e);
                }
            }
        };

        try {
            logger.log(testId, Stage.OPEN, Pair.of("sessionStartInfo", sessionStartInfo));
            serverConnector.startSession(taskListener, sessionStartInfo);
        } catch (Throwable t) {
            listener.onFail(t);
        }
    }

    public void decrementConcurrency() {
        int currentAmount = this.currentTestAmount.decrementAndGet();
        logger.log(TraceLevel.Info, new HashSet<String>(), Stage.CLOSE, null, Pair.of("testAmount", currentAmount));
    }

    public boolean isConcurrencyLimitReached() {
        return isServerConcurrencyLimitReached || currentTestAmount.get() >= this.eyesConcurrency;
    }
}
