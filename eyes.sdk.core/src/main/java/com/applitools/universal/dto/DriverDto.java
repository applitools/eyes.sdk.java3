package com.applitools.universal.dto;

import java.util.Map;

/**
 * custom driver
 */
public class DriverDto {
  private String sessionId;
  private String serverUrl;
  private Map<String, Object> capabilities;

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public String getServerUrl() {
    return serverUrl;
  }

  public void setServerUrl(String serverUrl) {
    this.serverUrl = serverUrl;
  }

  public Map<String, Object> getCapabilities() {
    return capabilities;
  }

  public void setCapabilities(Map<String, Object> capabilities) {
    this.capabilities = capabilities;
  }

}
