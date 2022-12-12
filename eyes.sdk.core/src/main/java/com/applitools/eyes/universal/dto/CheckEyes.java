package com.applitools.eyes.universal.dto;

import com.applitools.eyes.universal.Reference;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * used to perform a check/match action
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CheckEyes {
  /**
   * reference received from "Core.openEyes" command
   */
  private Reference eyes;

  /**
   * check type
   */
  private String type;

  /**
   * check target
   */
  private ITargetDto target;

  /**
   * check settings
   */
  private CheckSettingsDto settings;

  /**
   * configuration object that will be associated with a new eyes object, it could be overridden later
   */
  private ConfigurationDto config;

  public CheckEyes(Reference eyes, ITargetDto target, CheckSettingsDto settings, ConfigurationDto config, String type) {
    this.eyes = eyes;
    this.target = target;
    this.settings = settings;
    this.config = config;
    this.type = type;
  }

  public Reference getEyes() {
    return eyes;
  }

  public void setEyes(Reference eyes) {
    this.eyes = eyes;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public CheckSettingsDto getSettings() {
    return settings;
  }

  public void setSettings(CheckSettingsDto settings) {
    this.settings = settings;
  }

  public ConfigurationDto getConfig() {
    return config;
  }

  public void setConfig(ConfigurationDto config) {
    this.config = config;
  }

  public ITargetDto getTarget() { return target; }

  public void setTarget(ITargetDto target) { this.target = target; }
}
