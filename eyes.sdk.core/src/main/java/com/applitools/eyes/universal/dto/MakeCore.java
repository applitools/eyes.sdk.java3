package com.applitools.eyes.universal.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * This event has to be sent in the first place just after a connection between Client and Server will be established.
 */
public class MakeCore {
  /**
   * client sdk agent id
   */
  private String agentId;

  /**
   * current working directory
   */
  private String cwd;

  /**
   * the spec-driver (webdriver or custom)
   */
  @JsonUnwrapped
  private SpecDto spec;

  public MakeCore(String agentId, String cwd, SpecDto spec) {
    this.agentId = agentId;
    this.cwd = cwd;
    this.spec = spec;
  }

  public String getAgentId() {
    return agentId;
  }

  public void setAgentId(String agentId) {
    this.agentId = agentId;
  }

  public String getCwd() {
    return cwd;
  }

  public void setCwd(String cwd) {
    this.cwd = cwd;
  }

  public SpecDto getSpec() {
    return spec;
  }

  public void setSpec(SpecDto spec) {
    this.spec = spec;
  }

  @Override
  public String toString() {
    return "MakeCore{" +
        "agentId='" + agentId + '\'' +
        ", cwd='" + cwd + '\'' +
        ", spec='" + spec + '\'' +
        '}';
  }
}
