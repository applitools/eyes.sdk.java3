package com.applitools.eyes.selenium.universal.dto;

import com.applitools.eyes.AbstractProxySettings;
import com.applitools.eyes.universal.dto.ITargetDto;

import java.util.Map;

/**
 * the driver target
 */
public class DriverTargetDto implements ITargetDto {

    private String serverUrl;
    private AbstractProxySettings proxySettings;
    private String sessionId;
    private Map<String, Object> capabilities;

    public void setServerUrl(String serverUrl) { this.serverUrl = serverUrl; }

    public String getServerUrl() { return serverUrl; }


    public AbstractProxySettings getProxySettings() {
        return proxySettings;
    }

    public void setProxySettings(AbstractProxySettings proxySettings) {
        this.proxySettings = proxySettings;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Map<String, Object> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(Map<String, Object> capabilities) {
        this.capabilities = capabilities;
    }
}
