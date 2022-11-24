package com.applitools.eyes.universal.dto.request;

import com.applitools.eyes.universal.dto.ITargetDto;

/**
 * command get viewport size request dto
 */
public class CommandGetViewportSizeRequestDto {
  private ITargetDto target;

  public CommandGetViewportSizeRequestDto() {
  }

  public CommandGetViewportSizeRequestDto(ITargetDto target) {
    this.target = target;
  }

  public ITargetDto getTarget() {
    return target;
  }

  public void setTarget(ITargetDto target) {
    this.target = target;
  }

  @Override
  public String toString() {
    return "CommandGetViewportSizeRequestDto{" +
        "driver=" + target +
        '}';
  }
}
