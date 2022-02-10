package com.applitools.eyes.selenium.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * render browser info
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RenderBrowserInfoDto {
  private RectangleSizeDto viewportSize;
  private String browserType;
  private String platform;
  private EmulationBaseInfoDto emulationInfo;
  private IosDeviceInfoDto iosDeviceInfo;
  private String sizeMode;
  private String baselineEnvName;

  public RectangleSizeDto getViewportSize() {
    return viewportSize;
  }

  public void setViewportSize(RectangleSizeDto viewportSize) {
    this.viewportSize = viewportSize;
  }

  public String getBrowserType() {
    return browserType;
  }

  public void setBrowserType(String browserType) {
    this.browserType = browserType;
  }

  public String getPlatform() {
    return platform;
  }

  public void setPlatform(String platform) {
    this.platform = platform;
  }

  public IosDeviceInfoDto getIosDeviceInfo() {
    return iosDeviceInfo;
  }

  public void setIosDeviceInfo(IosDeviceInfoDto iosDeviceInfo) {
    this.iosDeviceInfo = iosDeviceInfo;
  }

  public String getSizeMode() {
    return sizeMode;
  }

  public void setSizeMode(String sizeMode) {
    this.sizeMode = sizeMode;
  }

  public String getBaselineEnvName() {
    return baselineEnvName;
  }

  public void setBaselineEnvName(String baselineEnvName) {
    this.baselineEnvName = baselineEnvName;
  }

  public EmulationBaseInfoDto getEmulationInfo() {
    return emulationInfo;
  }

  public void setEmulationInfo(EmulationBaseInfoDto emulationInfo) {
    this.emulationInfo = emulationInfo;
  }
}
