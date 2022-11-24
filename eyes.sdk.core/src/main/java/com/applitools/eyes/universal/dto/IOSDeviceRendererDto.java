package com.applitools.eyes.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * IOSDeviceRendererDto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IOSDeviceRendererDto implements IBrowsersInfo {
  private IosDeviceInfoDto iosDeviceInfo;

  public IOSDeviceRendererDto() {
  }

  public IOSDeviceRendererDto(IosDeviceInfoDto iosDeviceInfo) {
    this.iosDeviceInfo = iosDeviceInfo;
  }

  public IosDeviceInfoDto getIosDeviceInfo() {
    return iosDeviceInfo;
  }

  public void setIosDeviceInfo(IosDeviceInfoDto iosDeviceInfo) {
    this.iosDeviceInfo = iosDeviceInfo;
  }
}
