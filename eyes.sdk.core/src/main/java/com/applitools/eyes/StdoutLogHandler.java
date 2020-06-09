package com.applitools.eyes;

import com.applitools.utils.GeneralUtils;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Writes log messages to the standard output stream.
 */
public class StdoutLogHandler implements LogHandler {

    private final boolean isVerbose;

    /**
     * Creates a new StdoutLogHandler instance.
     *
     * @param isVerbose Whether to handle or ignore verbose log messages.
     */
    public StdoutLogHandler(boolean isVerbose) {
        this.isVerbose = isVerbose;
    }

    /**
     * Does nothing.
     */
    public void open() {}

    /**
     * Creates a new StdoutLogHandler that ignores verbose log messages.
     */
    public StdoutLogHandler() {
        this(false);
    }

    public synchronized void onMessage(boolean verbose, String message) {
        if (!verbose || this.isVerbose) {
            String currentTime = GeneralUtils.toISO8601DateTime(
                    Calendar.getInstance(TimeZone.getTimeZone("UTC")));

            System.out.println(currentTime + " Eyes: " + message);
        }
    }

    /**
     * Does nothing.
     */
    public void close() {}

    @Override
    public boolean isOpen() {
        return true;
    }
}