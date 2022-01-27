package com.applitools.eyes.selenium.universal.dto;

import com.applitools.eyes.selenium.Reference;

/**
 * extract text regions dto
 */
public class ExtractTextRegionsDto {

  /**
   * reference received from "Core.openEyes" command
   */
  private Reference eyes;

  /**
   * ocr search settings
   */
  private OCRSearchSettingsDto settings;


  /**
   * configuration object that will be associated with a new eyes object, it could be overridden later
   */
  private ConfigurationDto config;

  public ExtractTextRegionsDto(Reference eyes, OCRSearchSettingsDto settings, ConfigurationDto config) {
    this.eyes = eyes;
    this.settings = settings;
    this.config = config;
  }

  public ConfigurationDto getConfig() {
    return config;
  }

  public void setConfig(ConfigurationDto config) {
    this.config = config;
  }

  public Reference getEyes() {
    return eyes;
  }

  public void setEyes(Reference eyes) {
    this.eyes = eyes;
  }

  public OCRSearchSettingsDto getSettings() {
    return settings;
  }

  public void setSettings(OCRSearchSettingsDto settings) {
    this.settings = settings;
  }
}
