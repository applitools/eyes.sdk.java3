package com.applitools.eyes.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * close batch settings dto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CloseBatchSettingsDto {

    /**
     * the batch ID
     */
    private String batchId;

    /**
     * the api key
     */
    private String apiKey;

    /**
     * proxy settings
     */
    private ProxyDto proxy;

    /**
     * eyes server
     */
    private String serverUrl;

    public CloseBatchSettingsDto(String batchId, String apiKey, String serverUrl, ProxyDto proxySettings) {
        this.batchId = batchId;
        this.apiKey = apiKey;
        this.serverUrl = serverUrl;
        this.proxy = proxySettings;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public ProxyDto getProxy() {
        return proxy;
    }

    public void setProxy(ProxyDto proxy) {
        this.proxy = proxy;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }
}
