package com.applitools.universal.dto;

/**
 * event
 */
public class EventDto<T> extends Command {

  /**
   * event data
   */
  private T payload;

  public T getPayload() {
    return payload;
  }

  public void setPayload(T payload) {
    this.payload = payload;
  }

}
