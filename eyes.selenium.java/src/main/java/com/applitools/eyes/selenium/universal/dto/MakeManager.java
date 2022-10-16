package com.applitools.eyes.selenium.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * create a manager object
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MakeManager {

  /**
   * manager type
   */
  private String type;

  /**
   * concurrency
   */
  private Integer concurrency;

  /**
   * isLegacy
   */
  private Boolean legacy;

  public MakeManager(String type, Integer concurrency, Boolean legacy) {
    this.type = type;
    this.concurrency = concurrency;
    this.legacy = legacy;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Integer getConcurrency() {
    return concurrency;
  }

  public void setConcurrency(Integer concurrency) {
    this.concurrency = concurrency;
  }

  public Boolean getLegacy() {
    return legacy;
  }

  public void setLegacy(Boolean legacy) {
    this.legacy = legacy;
  }

  @Override
  public String toString() {
    return "MakeManager{" +
        "type='" + type + '\'' +
        ", concurrency=" + concurrency +
        ", isLegacy=" + legacy +
        '}';
  }
}
