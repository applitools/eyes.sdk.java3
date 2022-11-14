package com.applitools.eyes.universal.dto.request;

import com.applitools.eyes.universal.dto.DeleteTestSettingsDto;

/**
 * command delete test request dto
 */
public class CommandDeleteTestRequestDto {

    private DeleteTestSettingsDto settings;

    public CommandDeleteTestRequestDto(DeleteTestSettingsDto settings) {
        this.settings = settings;
    }

    public DeleteTestSettingsDto getSettings() {
        return settings;
    }

    public void setSettings(DeleteTestSettingsDto settings) {
        this.settings = settings;
    }
}
