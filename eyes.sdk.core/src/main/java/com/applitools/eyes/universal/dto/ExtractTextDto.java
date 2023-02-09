package com.applitools.eyes.universal.dto;

import com.applitools.eyes.universal.Reference;
import java.util.List;


/**
 * extract text dto
 */
public class ExtractTextDto {

  /**
   * the target
   */
  private ITargetDto target;

  /**
   * ocr extract settings
   */
  private List<OCRExtractSettingsDto> settings;


  /**
   * configuration object that will be associated with a new eyes object, it could be overridden later
   */
  private ConfigurationDto config;

  public ExtractTextDto(ITargetDto target, List<OCRExtractSettingsDto> regions, ConfigurationDto config) {
    this.settings = regions;
    this.config = config;
    this.target = target;
  }

  public List<OCRExtractSettingsDto> getSettings() {
    return settings;
  }

  public void setSettings(List<OCRExtractSettingsDto> settings) {
    this.settings = settings;
  }

  public ConfigurationDto getConfig() {
    return config;
  }

  public void setConfig(ConfigurationDto config) {
    this.config = config;
  }

  public ITargetDto getTarget() {
    return target;
  }

  public void setTarget(ITargetDto target) {
    this.target = target;
  }
}
