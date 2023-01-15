package com.applitools.eyes.universal;

import com.applitools.eyes.fluent.GetRegion;
import com.applitools.eyes.universal.dto.TRegion;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * reference
 */
public class Reference extends TRegion implements GetRegion {

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
