/*
 * Applitools SDK for Selenium integration.
 */
package com.applitools.eyes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Encapsulates data for the session currently running in the agent.
 */
@JsonIgnoreProperties({"isNewSession", "$id", "steps"})
public class RunningSession {
    private boolean isNewSession;
    private String id;
    private String url;

    public RunningSession() {
        isNewSession = false;
    }


    public boolean getIsNewSession() {
        return isNewSession;
    }

    public void setIsNewSession(boolean isNewSession) {
        this.isNewSession = isNewSession;
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
}