package com.applitools.eyes;

import com.applitools.utils.ArgumentGuard;

/**
 * Logs trace messages.
 */
public class Logger {
    private LogHandler logHandler;

    public Logger() {
        logHandler = new NullLogHandler(); // Default.
    }

    /**
     * @return The currently set log handler.
     */
    public LogHandler getLogHandler() {
        return logHandler;
    }

    /**
     * Sets the log handler.
     * @param handler The log handler to set. If you want a log handler which
     *                does nothing, use {@link
     *                com.applitools.eyes.NullLogHandler}.
     */
    public void setLogHandler(LogHandler handler) {
        ArgumentGuard.notNull(handler, "handler");

        logHandler = handler;
    }

    /**
     *
     * @return The name of the method which called the logger, if possible,
     * or an empty string.
     */
    private String getPrefix() {
        StackTraceElement[] stackTraceElements =
                Thread.currentThread().getStackTrace();

        String prefix = "";
        // getStackTrace()<-getPrefix()<-log()/verbose()<-"actual caller"
        if (stackTraceElements != null && stackTraceElements.length >= 4) {
            prefix = stackTraceElements[3].getMethodName() + "():";
        }

        return prefix;
    }

    /**
     * Writes a verbose write message.
     * @param message The message to log as verbose.
     */
    public void verbose(String message) {
        logHandler.onMessage(true, getPrefix() + message);
    }

    /**
     * Writes a (non-verbose) write message.
     * @param message The message to log.
     */
    public void log(String message) {
        logHandler.onMessage(false, getPrefix() + message);
    }
}
