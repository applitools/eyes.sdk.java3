package com.applitools.eyes.selenium.universal.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * batch dto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BatchDto {
  private String id;
  private String name;
  private String sequenceName;
  private String startedAt;
  private Boolean notifyOnCompletion;
  private List<CustomPropertyDto> properties;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getSequenceName() {
    return sequenceName;
  }

  public void setSequenceName(String sequenceName) {
    this.sequenceName = sequenceName;
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

  public List<CustomPropertyDto> getProperties() {
    return properties;
  }

  public void setProperties(List<CustomPropertyDto> properties) {
    this.properties = properties;
  }
}
