package com.applitools.eyes.universal.dto;

import com.applitools.eyes.universal.Reference;

/**
 * extract text regions dto
 */
public class LocateTextDto {

  /**
   * the target
   */
  private ITargetDto target;

  /**
   * ocr search settings
   */
  private OCRSearchSettingsDto settings;

  /**
   * configuration object that will be associated with a new eyes object, it could be overridden later
   */
  private ConfigurationDto config;

  public LocateTextDto(ITargetDto target, OCRSearchSettingsDto settings, ConfigurationDto config) {
    this.settings = settings;
    this.config = config;
    this.target = target;
  }

  public ConfigurationDto getConfig() {
    return config;
  }

  public void setConfig(ConfigurationDto config) {
    this.config = config;
  }

  public OCRSearchSettingsDto getSettings() {
    return settings;
  }

  public void setSettings(OCRSearchSettingsDto settings) {
    this.settings = settings;
  }

  public ITargetDto getTarget() {
    return target;
  }

  public void setTarget(ITargetDto target) {
    this.target = target;
  }
}
