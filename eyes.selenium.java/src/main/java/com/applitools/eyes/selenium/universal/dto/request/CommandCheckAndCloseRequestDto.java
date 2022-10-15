package com.applitools.eyes.selenium.universal.dto.request;

import com.applitools.eyes.selenium.Reference;
import com.applitools.eyes.selenium.universal.dto.*;
import com.applitools.eyes.selenium.universal.mapper.SettingsMapper;

public class CommandCheckAndCloseRequestDto {

    /**
     * reference received from "Core.openEyes" command
     */
    private Reference eyes;

    /**
     * the target
     */
    private ITargetDto target;

    /**
     * check settings
     */
    private CombinedSettingsDto settings;

    /**
     * configuration object
     */
    private ConfigurationDto config;

    public CommandCheckAndCloseRequestDto(Reference eyes, ITargetDto target, CheckSettingsDto checkSettings,
                                          CloseSettingsDto closeSettings, ConfigurationDto config) {
        this.eyes = eyes;
        this.target = target;
        this.config = config;
        this.settings = SettingsMapper.toCheckAndCloseSettingsDto(checkSettings, closeSettings, config);
    }

    public Reference getEyes() {
        return eyes;
    }

    public void setEyes(Reference eyes) {
        this.eyes = eyes;
    }

    public CombinedSettingsDto getSettings() {
        return settings;
    }

    public void setSettings(CombinedSettingsDto settings) {
        this.settings = settings;
    }

    public ConfigurationDto getConfig() {
        return config;
    }

    public void setConfig(ConfigurationDto config) {
        this.config = config;
    }

    public ITargetDto getTarget() { return target; }

    public void setTarget(ITargetDto target) { this.target = target; }
}
