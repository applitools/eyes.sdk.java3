package com.applitools.eyes.selenium.universal.dto.request;

import com.applitools.eyes.selenium.Reference;

/**
 * command close manager request dto
 */
public class CommandCloseManagerRequestDto {
  private Reference manager;
  private Boolean throwErr;

  public CommandCloseManagerRequestDto() {
  }

  public CommandCloseManagerRequestDto(Reference manager, Boolean throwErr) {
    this.manager = manager;
    this.throwErr = throwErr;
  }

  public Reference getManager() {
    return manager;
  }

  public void setManager(Reference manager) {
    this.manager = manager;
  }

  public Boolean getThrowErr() {
    return throwErr;
  }

  public void setThrowErr(Boolean throwErr) {
    this.throwErr = throwErr;
  }
}
