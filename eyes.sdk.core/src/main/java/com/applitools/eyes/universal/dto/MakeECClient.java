package com.applitools.eyes.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MakeECClient {

    private ECClientSettingsDto settings;

    public MakeECClient() {
    }

    public MakeECClient(ECClientSettingsDto settings) {
        this.settings = settings;
    }

    public ECClientSettingsDto getSettings() {
        return settings;
    }

    public void setSettings(ECClientSettingsDto settings) {
        this.settings = settings;
    }

    @Override
    public String toString() {
        return "MakeECClient{" +
                "settings=" + settings +
                '}';
    }
}
