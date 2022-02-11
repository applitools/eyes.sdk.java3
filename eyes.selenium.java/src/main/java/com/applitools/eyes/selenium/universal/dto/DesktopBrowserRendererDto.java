package com.applitools.eyes.selenium.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * DesktopBrowserRendererDto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DesktopBrowserRendererDto implements IBrowsersInfo {
  private String name;
  private Integer width;
  private Integer height;

  public DesktopBrowserRendererDto() {
  }

  public DesktopBrowserRendererDto(String name, Integer width, Integer height) {
    this.name = name;
    this.width = width;
    this.height = height;
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
}
