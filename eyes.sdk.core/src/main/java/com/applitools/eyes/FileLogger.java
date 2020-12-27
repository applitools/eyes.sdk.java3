/*
 * Applitools software.
 */
package com.applitools.eyes;

import com.applitools.eyes.logging.ClientEvent;
import com.applitools.eyes.logging.TraceLevel;
import com.applitools.utils.ArgumentGuard;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

/**
 * Writes log messages to a file.
 */
public class FileLogger extends LogHandler {
    private final String filename;
    private final boolean append;
    private BufferedWriter fileWriter;

    public FileLogger(String filename, boolean append, boolean isVerbose) {
        super(isVerbose ? TraceLevel.Debug : TraceLevel.Notice);
        ArgumentGuard.notNullOrEmpty(filename, "filename");
        this.filename = filename;
        this.append = append;
        fileWriter = null;
    }

    public FileLogger(String filename, boolean append, TraceLevel level) {
        super(level);
        ArgumentGuard.notNullOrEmpty(filename, "filename");
        this.filename = filename;
        this.append = append;
        fileWriter = null;
    }

    public FileLogger(boolean isVerbose) {
        this(isVerbose ? TraceLevel.Debug : TraceLevel.Notice);
    }

    public FileLogger(TraceLevel level) {
        this("eyes.log", true, level);
    }

    /**
     * Open the log file for writing.
     */
    public void open() {
        if (fileWriter != null) {
            return;
        }

        try {
            File file = new File(filename);
            File path = file.getParentFile();
            if (path != null && !path.exists()) {
                System.out.println("No Folder");
                boolean success = path.mkdirs();
                if (success) {
                    System.out.println("Folder created");
                } else {
                    System.out.printf("Failed creating folder %s%n", path.getAbsolutePath());
                }
            }

            fileWriter = new BufferedWriter(new FileWriter(file, append));
        } catch (IOException e) {
            throw new EyesException("Failed to create log file!", e);
        }
    }

    @Override
    public void onMessageInner(ClientEvent clientEvent) {
        if (fileWriter != null) {
            try {
                synchronized (fileWriter) {
                    fileWriter.write(new ObjectMapper().writeValueAsString(clientEvent));
                    fileWriter.newLine();
                    fileWriter.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Close the log file for writing.
     */
    public void close() {
        try {
            if (fileWriter != null) {
                fileWriter.close();
            }
        } catch (IOException ignored) {
        }
        fileWriter = null;
    }

    @Override
    public boolean isOpen() {
        return fileWriter != null;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof FileLogger) {
            return ((FileLogger) other).filename.equals(filename);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(filename);
    }
}
