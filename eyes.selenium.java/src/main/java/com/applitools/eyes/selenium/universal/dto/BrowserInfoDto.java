package com.applitools.eyes.selenium.universal.dto;

/**
 * Browser info dto
 */
public class BrowserInfoDto {
  private ChromeEmulationInfoDto chromeEmulationInfo;
  private IosDeviceInfoDto iosDeviceInfo;
  private AndroidDeviceInfoDto androidDeviceInfoDto;
  private String name;
  private Integer width;
  private Integer height;

  public BrowserInfoDto() {
  }

  public BrowserInfoDto(ChromeEmulationInfoDto chromeEmulationInfo, IosDeviceInfoDto iosDeviceInfo, String name, Integer width,
      Integer height) {
    this.chromeEmulationInfo = chromeEmulationInfo;
    this.iosDeviceInfo = iosDeviceInfo;
    this.name = name;
    this.width = width;
    this.height = height;
  }

  public ChromeEmulationInfoDto getChromeEmulationInfo() {
    return chromeEmulationInfo;
  }

  public void setChromeEmulationInfo(ChromeEmulationInfoDto chromeEmulationInfo) {
    this.chromeEmulationInfo = chromeEmulationInfo;
  }

  public IosDeviceInfoDto getIosDeviceInfo() {
    return iosDeviceInfo;
  }

  public void setIosDeviceInfo(IosDeviceInfoDto iosDeviceInfo) {
    this.iosDeviceInfo = iosDeviceInfo;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getWidth() {
    return width;
  }

  public void setWidth(Integer width) {
    this.width = width;
  }

  public Integer getHeight() {
    return height;
  }

  public void setHeight(Integer height) {
    this.height = height;
  }

  public AndroidDeviceInfoDto getAndroidDeviceInfoDto() {
    return androidDeviceInfoDto;
  }

  public void setAndroidDeviceInfoDto(AndroidDeviceInfoDto androidDeviceInfoDto) {
    this.androidDeviceInfoDto = androidDeviceInfoDto;
  }
}
