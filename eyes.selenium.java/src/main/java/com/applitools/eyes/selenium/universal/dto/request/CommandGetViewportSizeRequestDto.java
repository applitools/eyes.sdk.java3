package com.applitools.eyes.selenium.universal.dto.request;

import com.applitools.eyes.selenium.universal.dto.DriverDto;

/**
 * command get viewport size request dto
 */
public class CommandGetViewportSizeRequestDto {
  private DriverDto driver;

  public CommandGetViewportSizeRequestDto() {
  }

  public CommandGetViewportSizeRequestDto(DriverDto driver) {
    this.driver = driver;
  }

  public DriverDto getDriver() {
    return driver;
  }

  public void setDriver(DriverDto driver) {
    this.driver = driver;
  }

  @Override
  public String toString() {
    return "CommandGetViewportSizeRequestDto{" +
        "driver=" + driver +
        '}';
  }
}
