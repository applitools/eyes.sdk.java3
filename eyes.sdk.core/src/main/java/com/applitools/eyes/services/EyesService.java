package com.applitools.eyes.services;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.Logger;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class EyesService<INPUT, OUTPUT> {
    protected Logger logger;
    protected ServerConnector serverConnector;

    protected final List<Pair<String, INPUT>> inputQueue = new ArrayList<>();
    protected final List<Pair<String, OUTPUT>> outputQueue = Collections.synchronizedList(new ArrayList<Pair<String, OUTPUT>>());
    protected final List<Pair<String, Throwable>> errorQueue = Collections.synchronizedList(new ArrayList<Pair<String, Throwable>>());

    public EyesService(Logger logger, ServerConnector serverConnector) {
        this.logger = logger;
        this.serverConnector = serverConnector;
    }

    public void setLogger(Logger logger) {
        serverConnector.setLogger(logger);
        this.logger = logger;
    }

    public void setServerConnector(ServerConnector serverConnector) {
        this.serverConnector = serverConnector;
    }

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
