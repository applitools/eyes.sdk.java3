package com.applitools.eyes.universal.dto.request;

import com.applitools.eyes.universal.Reference;

/**
 * command abort request dto
 */
public class CommandAbortRequestDto {

    private Reference eyes;

    public CommandAbortRequestDto() {
    }

    public CommandAbortRequestDto(Reference eyes) {
        this.eyes = eyes;
    }

    public Reference getEyes() {
        return eyes;
    }

    public void setEyes(Reference eyes) {
        this.eyes = eyes;
    }
}
