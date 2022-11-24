package com.applitools.eyes.universal.dto;


/**
 * response payload
 */
public class ResponsePayload<T> {

  private T result;
  private ErrorDto error;

  public ResponsePayload() {
  }

  public ResponsePayload(T result) {
    this.result = result;
  }

  public ResponsePayload(T result, ErrorDto error) {
    this.result = result;
    this.error = error;
  }

  public T getResult() {
    return result;
  }

  public void setResult(T result) {
    this.result = result;
  }

  public ErrorDto getError() {
    return error;
  }

  public void setError(ErrorDto error) {
    this.error = error;
  }

  @Override
  public String toString() {
    return "ResponsePayload{" +
        "result=" + result +
        ", error=" + error +
        '}';
  }
}
