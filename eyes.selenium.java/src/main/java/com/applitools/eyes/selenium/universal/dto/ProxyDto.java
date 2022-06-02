package com.applitools.eyes.selenium.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * abstract proxy settings dto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProxyDto {
  private String url;
  private String username;
  private String password;
  private Boolean isHttpOnly;

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
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

  public Boolean getIsHttpOnly() {
    return isHttpOnly;
  }

  public void setIsHttpOnly(Boolean isHttpOnly) {
    this.isHttpOnly = isHttpOnly;
  }
}
