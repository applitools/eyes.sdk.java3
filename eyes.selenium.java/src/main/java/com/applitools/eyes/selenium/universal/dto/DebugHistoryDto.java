package com.applitools.eyes.selenium.universal.dto;

import java.util.List;

/**
 * debug history dto
 */
public class DebugHistoryDto {

    /**
     * list of managers.
     */
    private List<DebugManagersDto> managers;

    /**
     * started at.
     */
    private String startedAt;

    /**
     * requested at.
     */
    private String requestedAt;

    public DebugHistoryDto() {
    }

    public List<DebugManagersDto> getManagers() {
        return managers;
    }

    public void setManagers(List<DebugManagersDto> managers) {
        this.managers = managers;
    }

    public String getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(String startedAt) {
        this.startedAt = startedAt;
    }

    public String getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(String requestedAt) {
        this.requestedAt = requestedAt;
    }

    @Override
    public String toString() {
        return "DebugHistoryDto{" +
                "managers=" + managers +
                ", startedAt='" + startedAt + '\'' +
                ", requestedAt='" + requestedAt + '\'' +
                '}';
    }
}