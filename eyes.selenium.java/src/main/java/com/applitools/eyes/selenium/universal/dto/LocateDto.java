package com.applitools.eyes.selenium.universal.dto;

import com.applitools.eyes.selenium.Reference;

/**
 * used to perform a locate action
 */
public class LocateDto {

  private ITargetDto target;

  /**
   * visual locator settings
   */
  private VisualLocatorSettingsDto settings;


  /**
   * configuration object that will be associated with a new eyes object, it could be overridden later
   */
  private ConfigurationDto config;

  public LocateDto(ITargetDto target, VisualLocatorSettingsDto settings, ConfigurationDto config) {
    this.target = target;
    this.settings = settings;
    this.config = config;
  }

  public ITargetDto getTarget() {
    return target;
  }

  public void setTarget(ITargetDto target) {
    this.target = target;
  }

  public VisualLocatorSettingsDto getSettings() {
    return settings;
  }

  public void setSettings(VisualLocatorSettingsDto settings) {
    this.settings = settings;
  }

  public ConfigurationDto getConfig() {
    return config;
  }

  public void setConfig(ConfigurationDto config) {
    this.config = config;
  }
}
