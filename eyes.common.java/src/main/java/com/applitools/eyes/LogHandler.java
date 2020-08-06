package com.applitools.eyes;

/**
 * Handles log messages produces by the Eyes API.
 */
public interface LogHandler {
    void open();
    void onMessage(boolean verbose, String logString);
    void close();

    boolean isOpen();
}
