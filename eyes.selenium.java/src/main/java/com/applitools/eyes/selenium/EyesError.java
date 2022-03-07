package com.applitools.eyes.selenium;

/**
 * Eyes Error
 */
public class EyesError {
  private String message;
  private String stack;

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
}
