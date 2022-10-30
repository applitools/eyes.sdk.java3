package com.applitools.eyes.selenium.universal.dto;

import com.applitools.eyes.AbstractProxySettings;
import com.applitools.eyes.universal.dto.ITargetDto;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

/**
 * the driver target
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DriverTargetDto implements ITargetDto {

    private String serverUrl;
    private AbstractProxySettings proxy;
    private String sessionId;
    private Map<String, Object> capabilities;

    public void setServerUrl(String serverUrl) { this.serverUrl = serverUrl; }

    public String getServerUrl() { return serverUrl; }


    public AbstractProxySettings getProxy() {
        return proxy;
    }

    public void setProxy(AbstractProxySettings proxy) {
        this.proxy = proxy;
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
