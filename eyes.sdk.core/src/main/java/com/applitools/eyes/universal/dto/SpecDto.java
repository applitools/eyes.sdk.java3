package com.applitools.eyes.universal.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpecDto {

    @JsonIgnore
    private String protocol;

    @JsonIgnore
    private String[] commands;

    public SpecDto(String protocol, String[] commands) {
        this.protocol = protocol;
        this.commands = commands;
    }

    @JsonProperty("spec")
    public Object getSpec() {
        return protocol != null? protocol : commands;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String[] getCommands() {
        return commands;
    }

    public void setCommands(String[] commands) {
        this.commands = commands;
    }

    @Override
    public String toString() {
        return commands == null? protocol : Arrays.toString(commands);
    }
}
