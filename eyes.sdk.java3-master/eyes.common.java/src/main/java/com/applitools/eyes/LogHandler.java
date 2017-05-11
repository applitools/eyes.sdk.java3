package com.applitools.eyes;

/**
 * Handles log messages produces by the Eyes API.
 */
public interface LogHandler {
    public void open();
    public void onMessage(boolean verbose, String logString);
    public void close();
}
