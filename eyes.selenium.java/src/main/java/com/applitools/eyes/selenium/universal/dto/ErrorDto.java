package com.applitools.eyes.selenium.universal.dto;

/**
 * error
 */
public class ErrorDto {
  private String message;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public String toString() {
    return "ErrorDto{" +
        "message='" + message + '\'' +
        '}';
  }
}
