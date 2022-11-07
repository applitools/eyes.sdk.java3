package com.applitools.eyes.universal.dto;

import com.applitools.eyes.universal.Reference;

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

    /**
     * the driver.
     */
    private DebugEyesTargetDto target;

    public Reference getManager() {
        return manager;
    }

    public Reference getEyes() {
        return eyes;
    }

    public ConfigurationDto getConfig() {
        return config;
    }

    public DebugEyesTargetDto getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return "DebugEyesDto{" +
                "manager=" + manager +
                ", eyes=" + eyes +
                ", config=" + config +
                ", target=" + target +
                '}';
    }
}
