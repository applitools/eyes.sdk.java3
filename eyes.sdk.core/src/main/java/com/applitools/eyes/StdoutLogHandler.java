package com.applitools.eyes;

import com.applitools.eyes.logging.TraceLevel;
import com.applitools.utils.GeneralUtils;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Writes log messages to the standard output stream.
 */
public class StdoutLogHandler extends LogHandler {

    /**
     * Creates a new StdoutLogHandler instance.
     *
     * @param isVerbose Whether to handle or ignore verbose log messages.
     */
    public StdoutLogHandler(boolean isVerbose) {
        super(isVerbose);
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

    public synchronized void onMessage(String message) {
        String currentTime = GeneralUtils.toISO8601DateTime(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
        System.out.println(currentTime + " Eyes: " + message);
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