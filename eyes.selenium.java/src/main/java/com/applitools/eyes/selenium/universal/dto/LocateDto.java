package com.applitools.eyes.selenium.universal.dto;

import com.applitools.eyes.selenium.Reference;

/**
 * used to perform a locate action
 */
public class LocateDto {
  /**
   * reference received from "Core.openEyes" command
   */
  private Reference eyes;


  /**
   * visual locator settings
   */
  private VisualLocatorSettingsDto settings;


  /**
   * configuration object that will be associated with a new eyes object, it could be overridden later
   */
  private ConfigurationDto config;

  public LocateDto(Reference eyes, VisualLocatorSettingsDto settings, ConfigurationDto config) {
    this.eyes = eyes;
    this.settings = settings;
    this.config = config;
  }

  public Reference getEyes() {
    return eyes;
  }

  public void setEyes(Reference eyes) {
    this.eyes = eyes;
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
