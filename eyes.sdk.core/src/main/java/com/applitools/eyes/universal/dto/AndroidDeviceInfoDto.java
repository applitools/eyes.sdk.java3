package com.applitools.eyes.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * android device info dto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AndroidDeviceInfoDto {
  private String deviceName;
  private String screenOrientation;
  private String version;

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

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }
}
