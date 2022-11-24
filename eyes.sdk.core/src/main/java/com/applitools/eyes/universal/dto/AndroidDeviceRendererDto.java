package com.applitools.eyes.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Android device renderer
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AndroidDeviceRendererDto implements IBrowsersInfo {
  private AndroidDeviceInfoDto androidDeviceInfo;

  public AndroidDeviceRendererDto() {
  }

  public AndroidDeviceRendererDto(AndroidDeviceInfoDto androidDeviceInfo) {
    this.androidDeviceInfo = androidDeviceInfo;
  }

  public AndroidDeviceInfoDto getAndroidDeviceInfo() {
    return androidDeviceInfo;
  }

  public void setAndroidDeviceInfo(AndroidDeviceInfoDto androidDeviceInfo) {
    this.androidDeviceInfo = androidDeviceInfo;
  }
}
