package com.applitools.eyes.selenium.universal.dto;

/**
 * @author Kanan
 */
public class SessionUrlsDto {
  private String batch;
  private String session;

  public SessionUrlsDto() {
  }

  public String getBatch() {
    return batch;
  }

  public void setBatch(String batch) {
    this.batch = batch;
  }

  public String getSession() {
    return session;
  }

  public void setSession(String session) {
    this.session = session;
  }
}
