package com.applitools.eyes;

import com.applitools.eyes.logging.TraceLevel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiLogHandler extends LogHandler {
    private final List<LogHandler> logHandlers = new ArrayList<>();

    public MultiLogHandler(LogHandler... logHandlers) {
        super(true);
        this.logHandlers.addAll(Arrays.asList(logHandlers));
    }

    @Override
    public void open() {
        for (LogHandler logHandler : logHandlers) {
            logHandler.open();
        }
    }

    @Override
    public void onMessage(TraceLevel level, String message) {
        for (LogHandler logHandler : logHandlers) {
            logHandler.onMessage(level, message);
        }
    }

    @Override
    public void onMessage(String message) {
        for (LogHandler logHandler : logHandlers) {
            logHandler.onMessage(message);
        }
    }

    @Override
    public void close() {
        for (LogHandler logHandler : logHandlers) {
            logHandler.close();
        }
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    public void addLogHandler(LogHandler logHandler) {
        logHandlers.add(logHandler);
    }
}
