package com.applitools.eyes.universal.dto.request;

import com.applitools.eyes.universal.Reference;

/**
 * command close all eyes request dto
 */
public class CommandCloseAllEyesRequestDto {

  private Reference manager;

  public CommandCloseAllEyesRequestDto() {
  }

  public CommandCloseAllEyesRequestDto(Reference manager) {
    this.manager = manager;
  }

  public Reference getManager() {
    return manager;
  }

  public void setManager(Reference manager) {
    this.manager = manager;
  }

}
