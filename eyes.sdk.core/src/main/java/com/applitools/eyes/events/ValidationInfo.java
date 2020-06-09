package com.applitools.eyes.events;

public class ValidationInfo {

    private String validationId;
    private String tag;

    public String getValidationId() {
        return validationId;
    }

    public void setValidationId(String validationId) {
        this.validationId = validationId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String toJsonString() {
        return "{\"tag\":\"" + tag + "\", \"validationId\":\"" + validationId + "\"}";
    }
}
