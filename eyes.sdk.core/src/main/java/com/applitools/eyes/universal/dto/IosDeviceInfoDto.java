package com.applitools.eyes.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * ios device info
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IosDeviceInfoDto {
  private String deviceName;
  private String screenOrientation;
  private String iosVersion;

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
    return iosVersion;
  }

  public void setVersion(String iosVersion) {
    this.iosVersion = iosVersion;
  }

}
