package com.applitools.eyes;

/**
 * Container for URLs received by test results.
 */
public class SessionUrls {
    private String batch;
    private String session;

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }
}
