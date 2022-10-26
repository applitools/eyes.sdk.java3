package com.applitools.eyes.selenium.universal.dto;

import com.applitools.eyes.selenium.Reference;

public class DebugEyesDto {

    /**
     * the manager reference.
     */
    private Reference manager;

    /**
     * the eyes reference.
     */
    private Reference eyes;

    /**
     * the eyes configuration.
     */
    private ConfigurationDto config;

    public Reference getManager() {
        return manager;
    }

    public Reference getEyes() {
        return eyes;
    }

    public ConfigurationDto getConfig() {
        return config;
    }

    @Override
    public String toString() {
        return "DebugEyesDto{" +
                "manager=" + manager +
                ", eyes=" + eyes +
                ", config=" + config +
                '}';
    }
}
