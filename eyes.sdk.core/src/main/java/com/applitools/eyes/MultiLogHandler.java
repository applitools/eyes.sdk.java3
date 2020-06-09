package com.applitools.eyes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiLogHandler implements LogHandler {

    private List<LogHandler> logHandlers = new ArrayList<>();

    public MultiLogHandler(LogHandler... logHandlers) {
        this.logHandlers.addAll(Arrays.asList(logHandlers));
    }

    @Override
    public void open() {
        for (LogHandler logHandler : logHandlers) {
            logHandler.open();
        }
    }

    @Override
    public void onMessage(boolean verbose, String logString) {
        for (LogHandler logHandler : logHandlers) {
            logHandler.onMessage(verbose, logString);
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
