package com.applitools.universal.dto;

/**
 * create a manager object
 */
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
  private Boolean isLegacy;

  public MakeManager(String type, Integer concurrency, Boolean legacy) {
    this.type = type;
    this.concurrency = concurrency;
    this.isLegacy = legacy;
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

  public Boolean getIsLegacy() {
    return isLegacy;
  }

  public void setIsLegacy(Boolean isLegacy) {
    this.isLegacy = isLegacy;
  }

  @Override
  public String toString() {
    return "MakeManager{" +
        "type='" + type + '\'' +
        ", concurrency=" + concurrency +
        ", isLegacy=" + isLegacy +
        '}';
  }
}
