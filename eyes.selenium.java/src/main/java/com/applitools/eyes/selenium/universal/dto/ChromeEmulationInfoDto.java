package com.applitools.eyes.selenium.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author Kanan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChromeEmulationInfoDto {
  private String deviceName;
  private String screenOrientation;

  public ChromeEmulationInfoDto() {
  }

  public ChromeEmulationInfoDto(String deviceName, String screenOrientation) {
    this.deviceName = deviceName;
    this.screenOrientation = screenOrientation;
  }

  public String getDeviceName() {
    return deviceName;
  }

  public void setDeviceName(String deviceName) {
    this.deviceName = deviceName;
  }

  public String getScreenOrientation() {
    return screenOrientation;
  }

  public void setScreenOrientation(String screenOrientation) {
    this.screenOrientation = screenOrientation;
  }
}
