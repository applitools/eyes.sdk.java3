package com.applitools.eyes.selenium.universal.dto;


import com.applitools.eyes.selenium.Reference;

/**
 * create an eyes object
 */
public class OpenEyes {

  /**
   * reference received from "Core.makeManager" command
   */
  private Reference manager;

  /**
   * the driver target
   */
  private DriverTargetDto target;

  /**
   * the OpenSettings object that will be associated with a new eyes object
   */
  private OpenSettingsDto settings;

  /**
   * configuration object that will be associated with a new eyes object, it could be overridden later
   */
  private ConfigurationDto config;

  public OpenEyes(Reference manager, DriverTargetDto target, OpenSettingsDto settings, ConfigurationDto config) {
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

  public DriverTargetDto getTarget() {
    return target;
  }

  public void setTarget(DriverTargetDto target) {
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
