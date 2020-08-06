package com.applitools.eyes;

/**
 * Ignores all log messages.
 */
public class NullLogHandler implements LogHandler {

    public static final NullLogHandler instance = new NullLogHandler();

    public void onMessage(boolean verbose, String logString) {}

    public void open() {}

    public void close() {}

    @Override
    public boolean isOpen() {
        return true;
    }
}