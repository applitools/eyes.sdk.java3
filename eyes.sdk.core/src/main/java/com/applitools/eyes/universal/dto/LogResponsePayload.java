package com.applitools.eyes.universal.dto;


/**
 * log response payload
 */
public class LogResponsePayload<T> {

    private String level;
    private String message;

    public LogResponsePayload() {
    }

    public LogResponsePayload(String level) {
        this.level = level;
    }

    public LogResponsePayload(String level, String message) {
        this.level = level;
        this.message = message;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "LogResponsePayload{" +
                "level=" + level +
                ", message=" + message +
                '}';
    }
}
