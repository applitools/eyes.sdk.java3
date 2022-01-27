package com.applitools.eyes.selenium.universal.dto;


import com.applitools.eyes.selenium.Reference;

/**
 * response payload
 */
public class ResponsePayload {

  private Reference result;
  private ErrorDto error;

  public ResponsePayload(Reference result, ErrorDto error) {
    this.result = result;
    this.error = error;
  }

  public Reference getResult() {
    return result;
  }

  public void setResult(Reference result) {
    this.result = result;
  }

  public ErrorDto getError() {
    return error;
  }

  public void setError(ErrorDto error) {
    this.error = error;
  }
}
