package com.applitools.eyes.visualgrid.model;

public class ScriptResponse {
    FrameData value;
    Status status;
    String error;

    public ScriptResponse(FrameData value, Status status, String error) {
        this.value = value;
        this.status = status;
        this.error = error;
    }

    public ScriptResponse() {
    }

    public FrameData getValue() {
        return value;
    }

    public void setValue(FrameData value) {
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

    public enum Status {
        WIP("WIP"),
        ERROR("ERROR"),
        SUCCESS("SUCCESS");

        String status;

        Status(String status) {
            this.status = status;
        }
    }
}
