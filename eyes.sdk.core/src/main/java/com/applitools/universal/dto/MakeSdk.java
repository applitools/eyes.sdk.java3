package com.applitools.universal.dto;

/**
 * This event has to be sent in the first place just after a connection between Client and Server will be established.
 */
public class MakeSdk {
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

  public MakeSdk(String name, String version, String cwd, String protocol) {
    this.name = name;
    this.version = version;
    this.cwd = cwd;
    this.protocol = protocol;
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

  @Override
  public String toString() {
    return "MakeSdk{" +
        "name='" + name + '\'' +
        ", version='" + version + '\'' +
        ", cwd='" + cwd + '\'' +
        ", protocol='" + protocol + '\'' +
        '}';
  }
}
