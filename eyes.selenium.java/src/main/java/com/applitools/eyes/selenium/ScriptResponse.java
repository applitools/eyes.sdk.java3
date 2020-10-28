package com.applitools.eyes.selenium;

import com.fasterxml.jackson.databind.JsonNode;

public class ScriptResponse {
    JsonNode value;
    Status status;
    String error;
    boolean done;

    public ScriptResponse() {
    }

    public JsonNode getValue() {
        return value;
    }

    public void setValue(JsonNode value) {
        this.value = value;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }



    public enum Status {
        WIP("WIP"),
        ERROR("ERROR"),
        SUCCESS_CHUNKED("SUCCESS_CHUNKED"),
        SUCCESS("SUCCESS");

        String status;

        Status(String status) {
            this.status = status;
        }
    }
}
