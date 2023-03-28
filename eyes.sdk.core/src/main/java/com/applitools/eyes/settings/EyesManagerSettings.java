package com.applitools.eyes.settings;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EyesManagerSettings {

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

    public EyesManagerSettings(Integer concurrency, Integer legacyConcurrency, String agentId) {
        this.concurrency = concurrency;
        this.legacyConcurrency = legacyConcurrency;
        this.agentId = agentId;
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
        return "EyesManagerSettings{" +
                "concurrency=" + concurrency +
                ", legacyConcurrency=" + legacyConcurrency +
                ", agentId='" + agentId + '\'' +
                '}';
    }
}
