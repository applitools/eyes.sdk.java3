package com.applitools.eyes.universal.dto;

import com.applitools.eyes.universal.Reference;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * create an eyes object
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenEyes {

  /**
   * reference received from "Core.makeManager" command
   */
  private Reference manager;

  /**
   * the driver target
   */
  private ITargetDto target;

  /**
   * the OpenSettings object that will be associated with a new eyes object
   */
  private OpenSettingsDto settings;

  /**
   * configuration object that will be associated with a new eyes object, it could be overridden later
   */
  private ConfigurationDto config;

  public OpenEyes(Reference manager, ITargetDto target, OpenSettingsDto settings, ConfigurationDto config) {
    this.manager = manager;
    this.target = target;
    this.settings = settings;
    this.config = config;
  }

  public Reference getManager() {
    return manager;
  }

  public void setManager(Reference manager) {
    this.manager = manager;
  }

  public ITargetDto getTarget() {
    return target;
  }

  public void setTarget(ITargetDto target) {
    this.target = target;
  }

  public OpenSettingsDto getSettings() { return settings; }

  public void setSettings(OpenSettingsDto settings) { this.settings = settings; }

  public ConfigurationDto getConfig() {
    return config;
  }

  public void setConfig(ConfigurationDto config) {
    this.config = config;
  }

  @Override
  public String toString() {
    return "OpenEyes{" +
        "manager=" + manager +
        ", target=" + target +
        ", settings=" + settings +
        ", config=" + config +
        '}';
  }
}
