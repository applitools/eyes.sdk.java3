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

    /**
     * the driver.
     */
    private DebugDriverDto driver;

    public Reference getManager() {
        return manager;
    }

    public Reference getEyes() {
        return eyes;
    }

    public ConfigurationDto getConfig() {
        return config;
    }

    public DebugDriverDto getDriver() {
        return driver;
    }

    @Override
    public String toString() {
        return "DebugEyesDto{" +
                "manager=" + manager +
                ", eyes=" + eyes +
                ", config=" + config +
                ", driver=" + driver +
                '}';
    }
}
