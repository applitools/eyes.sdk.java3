package com.applitools.eyes.universal.dto.request;

import com.applitools.eyes.universal.dto.CloseBatchSettingsDto;

import java.util.List;

/**
 * command close batch request dto.
 */
public class CommandCloseBatchRequestDto {

    private List<CloseBatchSettingsDto> settings;

    public CommandCloseBatchRequestDto(List<CloseBatchSettingsDto> settings) {
        this.settings = settings;
    }

    public List<CloseBatchSettingsDto> getSettings() {
        return settings;
    }

    public void setSettings(List<CloseBatchSettingsDto> settings) {
        this.settings = settings;
    }
}
