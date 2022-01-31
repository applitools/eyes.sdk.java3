package com.applitools.eyes.selenium;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * reference
 */
public class Reference {

  @JsonProperty("applitools-ref-id")
  private String applitoolsRefId;

  public Reference() {

  }

  public Reference(String applitoolsRefId) {
    this.applitoolsRefId = applitoolsRefId;
  }

  public String getApplitoolsRefId() {
    return applitoolsRefId;
  }

  public void setApplitoolsRefId(String applitoolsRefId) {
    this.applitoolsRefId = applitoolsRefId;
  }

  @Override
  public String toString() {
    return "Reference{" +
        "applitoolsRefId='" + applitoolsRefId + '\'' +
        '}';
  }
}
