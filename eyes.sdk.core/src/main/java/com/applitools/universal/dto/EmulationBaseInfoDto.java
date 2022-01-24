package com.applitools.universal.dto;

/**
 * emulation base info dto
 */
public class EmulationBaseInfoDto {
  private String screenOrientation;
  private RectangleSizeDto size;
  private String deviceName;
  private Integer width;
  private Integer height;
  private Double deviceScaleFactor;

  public String getScreenOrientation() {
    return screenOrientation;
  }

  public void setScreenOrientation(String screenOrientation) {
    this.screenOrientation = screenOrientation;
  }

  public RectangleSizeDto getSize() {
    return size;
  }

  public void setSize(RectangleSizeDto size) {
    this.size = size;
  }

  public String getDeviceName() {
    return deviceName;
  }

  public void setDeviceName(String deviceName) {
    this.deviceName = deviceName;
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

  public Double getDeviceScaleFactor() {
    return deviceScaleFactor;
  }

  public void setDeviceScaleFactor(Double deviceScaleFactor) {
    this.deviceScaleFactor = deviceScaleFactor;
  }
}
