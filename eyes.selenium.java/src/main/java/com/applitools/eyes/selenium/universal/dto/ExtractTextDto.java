package com.applitools.eyes.selenium.universal.dto;

import java.util.List;

import com.applitools.eyes.selenium.Reference;

/**
 * extract text dto
 */
public class ExtractTextDto {
  /**
   * reference received from "Core.openEyes" command
   */
  private Reference eyes;


  /**
   * ocr extract settings
   */
  private List<OCRExtractSettingsDto> regions;


  /**
   * configuration object that will be associated with a new eyes object, it could be overridden later
   */
  private ConfigurationDto config;

  public ExtractTextDto(Reference eyes, List<OCRExtractSettingsDto> regions, ConfigurationDto config) {
    this.eyes = eyes;
    this.regions = regions;
    this.config = config;
  }

  public Reference getEyes() {
    return eyes;
  }

  public void setEyes(Reference eyes) {
    this.eyes = eyes;
  }

  public List<OCRExtractSettingsDto> getRegions() {
    return regions;
  }

  public void setRegions(List<OCRExtractSettingsDto> regions) {
    this.regions = regions;
  }

  public ConfigurationDto getConfig() {
    return config;
  }

  public void setConfig(ConfigurationDto config) {
    this.config = config;
  }

}
