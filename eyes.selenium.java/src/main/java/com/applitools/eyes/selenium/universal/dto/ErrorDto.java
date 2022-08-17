package com.applitools.eyes.selenium.universal.dto;

/**
 * error
 */
public class ErrorDto {
  private String message;
  private String stack;
  private String reason;
  private InfoDto info;

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

  @Override
  public String toString() {
    return "ErrorDto{" +
        "message='" + message + '\'' +
        '}';
  }
}
