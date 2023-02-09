package com.applitools.eyes.universal.dto.request;

import com.applitools.eyes.settings.GetResultsSettings;
import com.applitools.eyes.universal.Reference;

/**
 * command close manager request dto
 */
public class CommandCloseManagerRequestDto {
  private Reference manager;
  private GetResultsSettings settings;

  public CommandCloseManagerRequestDto() {
  }

  public CommandCloseManagerRequestDto(Reference manager, GetResultsSettings settings) {
    this.manager = manager;
    this.settings = settings;
  }

  public Reference getManager() {
    return manager;
  }

  public void setManager(Reference manager) {
    this.manager = manager;
  }

  public GetResultsSettings getSettings() {
    return settings;
  }

  public void setSettings(GetResultsSettings settings) {
    this.settings = settings;
  }
}
