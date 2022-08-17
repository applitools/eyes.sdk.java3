package com.applitools.eyes.selenium;

import com.applitools.eyes.selenium.universal.dto.InfoDto;

/**
 * Eyes Error
 */
public class EyesError {
  private String message;
  private String stack;
  private String reason;
  private InfoDto info;

  public EyesError() {
  }

  public EyesError(String message, String stack) {
    this.message = message;
    this.stack = stack;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getStack() {
    return stack;
  }

  public void setStack(String stack) {
    this.stack = stack;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public InfoDto getInfo() {
    return info;
  }

  public void setInfo(InfoDto info) {
    this.info = info;
  }
}
