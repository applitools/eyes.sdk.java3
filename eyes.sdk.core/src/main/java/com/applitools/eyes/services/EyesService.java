package com.applitools.eyes.services;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.Logger;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public abstract class EyesService<INPUT, OUTPUT> {
    private static final int TIME_BETWEEN_STATUS_REPORT = 5 * 60 * 1000;

    protected Logger logger;
    protected ServerConnector serverConnector;

    protected final List<Pair<String, INPUT>> inputQueue = new ArrayList<>();
    protected final List<Pair<String, OUTPUT>> outputQueue = Collections.synchronizedList(new ArrayList<Pair<String, OUTPUT>>());
    protected final List<Pair<String, Throwable>> errorQueue = Collections.synchronizedList(new ArrayList<Pair<String, Throwable>>());

    public EyesService(Logger logger, ServerConnector serverConnector) {
        this.logger = logger;
        this.serverConnector = serverConnector;
        Timer timer = new Timer("Service Status Report", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logServiceStatus();
            }
        }, TIME_BETWEEN_STATUS_REPORT, TIME_BETWEEN_STATUS_REPORT);
    }

    public void setLogger(Logger logger) {
        serverConnector.setLogger(logger);
        this.logger = logger;
    }

    public void setServerConnector(ServerConnector serverConnector) {
        this.serverConnector = serverConnector;
    }

    public abstract void logServiceStatus();

    public abstract void run();

    public void addInput(String id, INPUT input) {
        inputQueue.add(Pair.of(id, input));
    }

    public List<Pair<String, OUTPUT>> getSucceededTasks() {
        synchronized (outputQueue) {
            List<Pair<String, OUTPUT>> succeededTasks = new ArrayList<>(outputQueue);
            outputQueue.clear();
            return succeededTasks;
        }
    }

    public List<Pair<String, Throwable>> getFailedTasks() {
        synchronized (errorQueue) {
            List<Pair<String, Throwable>> failedTasks = new ArrayList<>(errorQueue);
            errorQueue.clear();
            return failedTasks;
        }
    }
}
