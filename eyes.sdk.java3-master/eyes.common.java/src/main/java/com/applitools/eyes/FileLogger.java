/*
 * Applitools software.
 */
package com.applitools.eyes;

import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;

import java.io.*;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Writes log messages to a file.
 */
@SuppressWarnings("UnusedDeclaration")
public class FileLogger implements LogHandler {

    private final boolean isVerbose;
    private final String filename;
    private final boolean append;
    private BufferedWriter file;

    /**
     * Creates a new FileHandler instance.
     * @param filename The file in which to save the logs.
     * @param append Whether to append the logs if the current file exists,
     *               or to overwrite the existing file.
     * @param isVerbose Whether to handle or ignore verbose log messages.
     */
    public FileLogger(String filename, boolean append, boolean isVerbose) {
        ArgumentGuard.notNullOrEmpty(filename, "filename");
        this.filename = filename;
        this.append = append;
        this.isVerbose = isVerbose;
        file = null;
    }

    /**
     * See {@link #FileLogger(String, boolean, boolean)}.
     * {@code filename} defaults to {@code eyes.log}, append defaults to
     * {@code true}.
     *
     * @param isVerbose Whether to handle or ignore verbose log messages.
     */
    @SuppressWarnings("UnusedDeclaration")
    public FileLogger(boolean isVerbose) {
        this("eyes.log", true, isVerbose);
    }

    /**
     * Open the log file for writing.
     */
    public void open() {
        try {
            if (file != null) {
                //noinspection EmptyCatchBlock
                try {
                    file.close();
                } catch (Exception e) {}
            }
            file = new BufferedWriter(new FileWriter(new File(filename),
                    append));
        } catch (IOException e) {
            throw new EyesException("Failed to create log file!", e);
        }
    }

    /**
     * Handle a message to be logged.
     * @param verbose Whether this message is flagged as verbose or not.
     * @param logString The string to log.
     */
    public void onMessage(boolean verbose, String logString) {
        if (file != null && (!verbose || this.isVerbose)) {

            String currentTime = GeneralUtils.toISO8601DateTime(
                    Calendar.getInstance(TimeZone.getTimeZone("UTC")));

            try {
                file.write(currentTime + " Eyes: " + logString);
                file.newLine();
                file.flush();
            } catch (IOException e) {
                throw new EyesException("Failed to write log to file!", e);
            }
        }
    }

    /**
     * Close the log file for writing.
     */
    public void close() {
        //noinspection EmptyCatchBlock
        try {
            if (file !=null) {
                file.close();
            }
        } catch (IOException e) {}
        file = null;
    }
}
