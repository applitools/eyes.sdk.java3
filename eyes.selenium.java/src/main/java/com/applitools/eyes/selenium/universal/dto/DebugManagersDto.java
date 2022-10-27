package com.applitools.eyes.selenium.universal.dto;

import com.applitools.eyes.selenium.Reference;

import java.util.List;

public class DebugManagersDto {

    /**
     * the type of manager.
     */
    private String type; // classic | ufg

    /**
     * the manager reference.
     */
    private Reference manager;

    /**
     * eyes.
     */
    private List<DebugEyesDto> eyes;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Reference getManager() {
        return manager;
    }

    public void setManager(Reference manager) {
        this.manager = manager;
    }

    public List<DebugEyesDto> getEyes() {
        return eyes;
    }

    public void setEyes(List<DebugEyesDto> eyes) {
        this.eyes = eyes;
    }

    @Override
    public String toString() {
        return "ManagersDto{" +
                "type='" + type + '\'' +
                ", manager=" + manager +
                ", eyes=" + eyes +
                '}';
    }
}
