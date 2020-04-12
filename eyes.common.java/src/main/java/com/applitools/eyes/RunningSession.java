/*
 * Applitools SDK for Selenium integration.
 */
package com.applitools.eyes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Encapsulates model for the session currently running in the agent.
 */
@JsonIgnoreProperties({"isNewSession", "$id", "steps"})
public class RunningSession {
    private Boolean isNew;
    private String id;
    private String url;
    private String baselineId;
    private String batchId;
    private String sessionId;

    public RunningSession() {
        isNew = false;
    }

    public Boolean getIsNew() {
        return isNew;
    }

    public void setIsNew(Boolean isNew) {
        this.isNew = isNew;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBaselineId() {
        return baselineId;
    }

    public void setBaselineId(String baselineId) {
        this.baselineId = baselineId;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}