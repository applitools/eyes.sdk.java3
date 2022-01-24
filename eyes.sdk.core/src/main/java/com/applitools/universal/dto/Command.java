package com.applitools.universal.dto;

/**
 * command
 */
public class Command {

  /**
   * command name, recommended format is "<Domain>.<eventName>"
   */
  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
