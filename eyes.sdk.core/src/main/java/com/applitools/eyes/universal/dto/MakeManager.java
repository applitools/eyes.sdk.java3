package com.applitools.eyes.universal.dto;

import com.applitools.eyes.settings.EyesManagerSettings;
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
   * manager settings
   */
  private EyesManagerSettings settings;

  public MakeManager(String type, EyesManagerSettings settings) {
    this.type = type;
    this.settings = settings;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public EyesManagerSettings getSettings() {
    return settings;
  }

  public void setSettings(EyesManagerSettings settings) {
    this.settings = settings;
  }

  @Override
  public String toString() {
    return "MakeManager{" +
        "type='" + type + '\'' +
        ", settings=" + settings +
        '}';
  }
}
