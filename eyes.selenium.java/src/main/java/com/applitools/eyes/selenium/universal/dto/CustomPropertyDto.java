package com.applitools.eyes.selenium.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author Kanan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomPropertyDto {
  private String name;
  private String value;

  public CustomPropertyDto() {
  }

  public CustomPropertyDto(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
