package com.applitools.eyes.universal.dto.request;

import com.applitools.eyes.settings.GetResultsSettings;
import com.applitools.eyes.universal.Reference;

/**
 * command eyes get results request dto
 */
public class CommandEyesGetResultsDto {

    /**
     * reference received from "Core.openEyes" command
     */
    private Reference eyes;

    /**
     * close settings
     */
    private GetResultsSettings settings;

    public CommandEyesGetResultsDto() {
    }

    public CommandEyesGetResultsDto(Reference eyes, GetResultsSettings settings) {
        this.eyes = eyes;
        this.settings = settings;
    }

    public Reference getEyes() {
        return eyes;
    }

    public void setEyes(Reference eyes) {
        this.eyes = eyes;
    }

    public GetResultsSettings getSettings() {
        return settings;
    }

    public void setSettings(GetResultsSettings settings) {
        this.settings = settings;
    }

}
