package com.applitools.eyes.selenium.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * ChromeEmulationDeviceRendererDto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChromeEmulationDeviceRendererDto implements IBrowsersInfo {
  private ChromeEmulationInfoDto chromeEmulationInfo;

  public ChromeEmulationDeviceRendererDto(ChromeEmulationInfoDto chromeEmulationInfo) {
    this.chromeEmulationInfo = chromeEmulationInfo;
  }

  public ChromeEmulationDeviceRendererDto() {
  }

  public ChromeEmulationInfoDto getChromeEmulationInfo() {
    return chromeEmulationInfo;
  }

  public void setChromeEmulationInfo(ChromeEmulationInfoDto chromeEmulationInfo) {
    this.chromeEmulationInfo = chromeEmulationInfo;
  }
}
