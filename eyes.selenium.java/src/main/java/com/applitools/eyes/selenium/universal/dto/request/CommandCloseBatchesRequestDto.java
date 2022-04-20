package com.applitools.eyes.selenium.universal.dto.request;

import com.applitools.eyes.selenium.universal.dto.CloseBatchesDto;

/**
 * command close batches request dto
 */
public class CommandCloseBatchesRequestDto {
    private CloseBatchesDto settings;

    public CommandCloseBatchesRequestDto() {
    }

    public CommandCloseBatchesRequestDto(CloseBatchesDto settings) {
        this.settings = settings;
    }

    public CloseBatchesDto getSettings() {
        return settings;
    }

    public void setSettings(CloseBatchesDto settings) {
        this.settings = settings;
    }
}
