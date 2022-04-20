package com.applitools.eyes.selenium.universal.dto;

/**
 * close batches dto
 */
public class CloseBatchesDto {
    private String batchIds;
    private String serverUrl;
    private String apiKey;
    private ProxyDto proxy;

    public CloseBatchesDto(String batchIds, String serverUrl, String apiKey, ProxyDto proxy) {
        this.batchIds = batchIds;
        this.serverUrl = serverUrl;
        this.apiKey = apiKey;
        this.proxy = proxy;
    }

    public String getBatchIds() {
        return batchIds;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public ProxyDto getProxy() {
        return proxy;
    }

}
