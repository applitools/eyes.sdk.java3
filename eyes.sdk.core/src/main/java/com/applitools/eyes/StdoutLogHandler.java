package com.applitools.eyes;

import com.applitools.eyes.logging.ClientEvent;
import com.applitools.eyes.logging.TraceLevel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        super(isVerbose ? TraceLevel.Debug : TraceLevel.Notice);
    }

    /**
     * Creates a new StdoutLogHandler that ignores verbose log messages.
     */
    public StdoutLogHandler() {
        this(false);
    }

    public StdoutLogHandler(TraceLevel level) {
        super(level);
    }

    /**
     * Does nothing.
     */
    public void open() {}

    @Override
    public void onMessageInner(ClientEvent event) {
        try {
            System.out.println(new ObjectMapper().writeValueAsString(event));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
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

    @Override
    public boolean equals(Object other) {
        return other instanceof StdoutLogHandler;
    }

    @Override
    public int hashCode() {
        return StdoutLogHandler.class.hashCode();
    }
}