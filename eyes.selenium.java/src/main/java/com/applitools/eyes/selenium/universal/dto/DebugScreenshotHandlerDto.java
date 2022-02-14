package com.applitools.eyes.selenium.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * DebugScreenshotHandlerDto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DebugScreenshotHandlerDto {
  private Boolean save;
  private String path;
  private String prefix;

  public DebugScreenshotHandlerDto() {
  }

  public DebugScreenshotHandlerDto(Boolean save, String path, String prefix) {
    this.save = save;
    this.path = path;
    this.prefix = prefix;
  }

  public Boolean getSave() {
    return save;
  }

  public void setSave(Boolean save) {
    this.save = save;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  @Override
  public String toString() {
    return "DebugScreenshotHandlerDto{" +
        "save=" + save +
        ", path='" + path + '\'' +
        ", prefix='" + prefix + '\'' +
        '}';
  }
}
