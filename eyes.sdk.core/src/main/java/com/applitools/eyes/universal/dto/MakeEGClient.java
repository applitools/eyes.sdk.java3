package com.applitools.eyes.universal.dto;

public class MakeEGClient {

    private EGClientSettingsDto settings;

    public MakeEGClient(EGClientSettingsDto settings) {
        this.settings = settings;
    }

    public EGClientSettingsDto getSettings() {
        return settings;
    }

    public void setSettings(EGClientSettingsDto settings) {
        this.settings = settings;
    }
}
