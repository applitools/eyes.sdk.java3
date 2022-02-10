package com.applitools.eyes.selenium.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * abstract proxy settings dto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AbstractProxySettingsDto {
  private String uri;
  private String username;
  private String password;
  private Boolean isHttpOnly;

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Boolean getHttpOnly() {
    return isHttpOnly;
  }

  public void setHttpOnly(Boolean httpOnly) {
    isHttpOnly = httpOnly;
  }
}