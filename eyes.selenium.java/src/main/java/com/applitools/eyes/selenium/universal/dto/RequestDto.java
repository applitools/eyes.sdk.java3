package com.applitools.eyes.selenium.universal.dto;

/**
 * request
 */
public class RequestDto<T> extends Command {

  /**
   * used to associate response with actual request.
   */
  private String key;

  /**
   * input data
   */
  private T payload;

  public RequestDto() {

  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public T getPayload() {
    return payload;
  }

  public void setPayload(T payload) {
    this.payload = payload;
  }

}
