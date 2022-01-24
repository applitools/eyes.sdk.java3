package com.applitools.universal.dto;

/**
 * response
 */
public class ResponseDto {

  /**
   * name of the request this response sent for
   */
  private String name;

  /**
   * name of the request this response sent for
   */
  private String key;

  /**
   * any output data
   */
  private ResponsePayload payload;

  public ResponseDto(String name, String key, ResponsePayload payload) {
    this.name = name;
    this.key = key;
    this.payload = payload;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public ResponsePayload getPayload() {
    return payload;
  }

  public void setPayload(ResponsePayload payload) {
    this.payload = payload;
  }

  @Override
  public String toString() {
    return "ResponseDto{" +
        "name='" + name + '\'' +
        ", key='" + key + '\'' +
        ", payload=" + payload +
        '}';
  }
}
