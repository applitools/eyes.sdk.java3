package com.applitools.eyes.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ECCapabilities {

    private String eyesServerUrl;
    private String apiKey;
    private String sessionName;
    private Boolean useSelfHealing;
    private Boolean tunnel;
    private Integer timeout;
    private Integer inactivityTimeout;
    private Integer requestDriverTimeout;
    private Integer selfHealingMaxRetryTime;

    public String getEyesServerUrl() {
        return eyesServerUrl;
    }

    public void setEyesServerUrl(String eyesServerUrl) {
        this.eyesServerUrl = eyesServerUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public Boolean getUseSelfHealing() {
        return useSelfHealing;
    }

    public void setUseSelfHealing(Boolean useSelfHealing) {
        this.useSelfHealing = useSelfHealing;
    }

    public Boolean getTunnel() {
        return tunnel;
    }

    public void setTunnel(Boolean tunnel) {
        this.tunnel = tunnel;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getInactivityTimeout() {
        return inactivityTimeout;
    }

    public void setInactivityTimeout(Integer inactivityTimeout) {
        this.inactivityTimeout = inactivityTimeout;
    }

    public Integer getRequestDriverTimeout() {
        return requestDriverTimeout;
    }

    public void setRequestDriverTimeout(Integer requestDriverTimeout) {
        this.requestDriverTimeout = requestDriverTimeout;
    }

    public Integer getSelfHealingMaxRetryTime() {
        return selfHealingMaxRetryTime;
    }

    public void setSelfHealingMaxRetryTime(Integer selfHealingMaxRetryTime) {
        this.selfHealingMaxRetryTime = selfHealingMaxRetryTime;
    }
}
