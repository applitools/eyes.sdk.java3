package com.applitools.eyes.universal.dto.request;

import com.applitools.eyes.universal.Reference;
import com.applitools.eyes.universal.dto.CloseSettingsDto;
import com.applitools.eyes.universal.dto.ConfigurationDto;

/**
 * command close request dto
 */
public class CommandCloseRequestDto {

  /**
   * reference received from "Core.openEyes" command
   */
  private Reference eyes;

  /**
   * close settings
   */
  private CloseSettingsDto settings;

  /**
   * configuration object
   */
  private ConfigurationDto config;

  public CommandCloseRequestDto() {
  }

  public CommandCloseRequestDto(Reference eyes, CloseSettingsDto settings, ConfigurationDto config) {
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

  public CloseSettingsDto getSettings() {
    return settings;
  }

  public void setSettings(CloseSettingsDto settings) {
    this.settings = settings;
  }

  public ConfigurationDto getConfig() {
    return config;
  }

  public void setConfig(ConfigurationDto config) {
    this.config = config;
  }
}
