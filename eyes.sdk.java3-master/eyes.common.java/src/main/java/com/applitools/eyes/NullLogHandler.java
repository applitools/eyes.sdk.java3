package com.applitools.eyes;

/**
 * Ignores all log messages.
 */
public class NullLogHandler implements LogHandler {

    public void onMessage(boolean verbose, String logString) {}

    public void open() {}

    public void close() {}
}