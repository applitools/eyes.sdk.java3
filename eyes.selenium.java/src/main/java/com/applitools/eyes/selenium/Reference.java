package com.applitools.eyes.selenium;

import com.google.gson.annotations.SerializedName;

/**
 * reference
 */
public class Reference {

  @SerializedName("applitools-ref-id")
  private String applitoolsRefId;

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
