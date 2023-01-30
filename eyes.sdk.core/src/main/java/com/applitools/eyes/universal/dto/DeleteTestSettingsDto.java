package com.applitools.eyes.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * delete test settings.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    /**
     * The api key.
     */
    private String apiKey;

    /**
     * Eyes server url.
     */
    private String serverUrl;

    /**
     * Proxy settings.
     */
    private ProxyDto proxy;

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

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public ProxyDto getProxy() {
        return proxy;
    }

    public void setProxy(ProxyDto proxy) {
        this.proxy = proxy;
    }
}
