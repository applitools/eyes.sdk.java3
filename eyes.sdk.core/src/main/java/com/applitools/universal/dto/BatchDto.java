package com.applitools.universal.dto;

import java.util.List;
import java.util.Map;

/**
 * batch dto
 */
public class BatchDto {
  private String id;
  private String batchSequenceName;
  private String name;
  private String startedAt;
  private Boolean notifyOnCompletion;
  private Boolean isCompleted;
  private List<Map<String, String>> properties;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getBatchSequenceName() {
    return batchSequenceName;
  }

  public void setBatchSequenceName(String batchSequenceName) {
    this.batchSequenceName = batchSequenceName;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getStartedAt() {
    return startedAt;
  }

  public void setStartedAt(String startedAt) {
    this.startedAt = startedAt;
  }

  public Boolean getNotifyOnCompletion() {
    return notifyOnCompletion;
  }

  public void setNotifyOnCompletion(Boolean notifyOnCompletion) {
    this.notifyOnCompletion = notifyOnCompletion;
  }

  public Boolean getCompleted() {
    return isCompleted;
  }

  public void setCompleted(Boolean completed) {
    isCompleted = completed;
  }

  public List<Map<String, String>> getProperties() {
    return properties;
  }

  public void setProperties(List<Map<String, String>> properties) {
    this.properties = properties;
  }
}
