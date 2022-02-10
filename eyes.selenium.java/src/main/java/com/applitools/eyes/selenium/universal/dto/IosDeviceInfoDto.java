package com.applitools.eyes.selenium.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * ios device info
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IosDeviceInfoDto {
  private String deviceName;
  private String screenOrientation;
  private String version;
  private RectangleSizeDto size;

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

  public RectangleSizeDto getSize() {
    return size;
  }

  public void setSize(RectangleSizeDto size) {
    this.size = size;
  }
}
