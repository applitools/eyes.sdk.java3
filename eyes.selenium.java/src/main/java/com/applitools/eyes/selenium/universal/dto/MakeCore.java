package com.applitools.eyes.selenium.universal.dto;

import java.util.Arrays;

/**
 * This event has to be sent in the first place just after a connection between Client and Server will be established.
 */
public class MakeCore {
  /**
   * name of the client sdk
   */
  private String name;

  /**
   * version of the client sdk
   */
  private String version;

  /**
   * current working directory
   */
  private String cwd;

  /**
   * the name of the prebuilt server-side driver
   */
  private String protocol;

  /**
   * ?
   */
  private String[] commands;

  public MakeCore(String name, String version, String cwd, String protocol, String[] commands) {
    this.name = name;
    this.version = version;
    this.cwd = cwd;
    this.protocol = protocol;
    this.commands = commands;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getCwd() {
    return cwd;
  }

  public void setCwd(String cwd) {
    this.cwd = cwd;
  }

  public String getProtocol() {
    return protocol;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  public String[] getCommands() {
    return commands;
  }

  public void setCommands(String[] commands) {
    this.commands = commands;
  }

  @Override
  public String toString() {
    return "MakeSdk{" +
        "name='" + name + '\'' +
        ", version='" + version + '\'' +
        ", cwd='" + cwd + '\'' +
        ", protocol='" + protocol + '\'' +
        ", commands='" + Arrays.toString(commands) + '\'' +
        '}';
  }
}
