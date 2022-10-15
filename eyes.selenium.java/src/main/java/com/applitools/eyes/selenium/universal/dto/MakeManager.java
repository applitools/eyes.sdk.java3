package com.applitools.eyes.selenium.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * create a manager object
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MakeManager {

  /**
   * manager type
   */
  private String type;

  /**
   * concurrency
   */
  private Integer concurrency;

  /**
   * legacy concurrency
   */
  private Integer legacyConcurrency;

  /**
   * agent ID
   */
  private String agentId;

  public MakeManager(String type, Integer concurrency, Integer legacyConcurrency, String agentId) {
    this.type = type;
    this.concurrency = concurrency;
    this.legacyConcurrency = legacyConcurrency;
    this.agentId = agentId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Integer getConcurrency() {
    return concurrency;
  }

  public void setConcurrency(Integer concurrency) {
    this.concurrency = concurrency;
  }

  public Integer getLegacyConcurrency() {
    return legacyConcurrency;
  }

  public void setLegacyConcurrency(Integer legacyConcurrency) {
    this.legacyConcurrency = legacyConcurrency;
  }

  public String getAgentId() {
    return agentId;
  }

  public void setAgentId(String agentId) {
    this.agentId = agentId;
  }

  @Override
  public String toString() {
    return "MakeManager{" +
        "type='" + type + '\'' +
        ", concurrency=" + concurrency +
        ", legacyConcurrency=" + legacyConcurrency +
        ", agentId=" + agentId +
        '}';
  }
}
