package com.applitools.eyes.selenium.universal.dto.request;

import com.applitools.eyes.selenium.Reference;

/**
 * command close request dto
 */
public class CommandCloseRequestDto {

  private Reference eyes;

  public CommandCloseRequestDto() {
  }

  public CommandCloseRequestDto(Reference eyes) {
    this.eyes = eyes;
  }

  public Reference getEyes() {
    return eyes;
  }

  public void setEyes(Reference eyes) {
    this.eyes = eyes;
  }
}
