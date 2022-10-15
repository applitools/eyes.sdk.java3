package com.applitools.eyes.selenium.universal.dto.request;

import com.applitools.eyes.selenium.Reference;
import com.applitools.eyes.selenium.universal.dto.CloseSettingsDto;

/**
 * command close manager request dto
 */
public class CommandCloseManagerRequestDto {
  private Reference manager;
  private CloseSettingsDto settings;

  public CommandCloseManagerRequestDto() {
  }

  public CommandCloseManagerRequestDto(Reference manager, Boolean throwErr) {
    this.manager = manager;
    CloseSettingsDto closeSettingsDto = new CloseSettingsDto();
    closeSettingsDto.setThrowErr(throwErr);
    this.settings = closeSettingsDto;
  }

  public Reference getManager() {
    return manager;
  }

  public void setManager(Reference manager) {
    this.manager = manager;
  }

  public CloseSettingsDto getSettings() {
    return settings;
  }

  public void setSettings(CloseSettingsDto settings) {
    this.settings = settings;
  }
}
