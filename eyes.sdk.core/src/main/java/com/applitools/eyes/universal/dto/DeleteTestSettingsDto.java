package com.applitools.eyes.universal.dto;

/**
 * delete test settings.
 */
public class DeleteTestSettingsDto {

    /**
     * The test ID.
     */
    private String testId;

    /**
     * The Batch ID.
     */
    private String batchId;

    /**
     * The secret token.
     */
    private String secretToken;

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getSecretToken() {
        return secretToken;
    }

    public void setSecretToken(String secretToken) {
        this.secretToken = secretToken;
    }
}
