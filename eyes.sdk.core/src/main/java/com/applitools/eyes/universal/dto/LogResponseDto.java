package com.applitools.eyes.universal.dto;

/**
 * log response
 */
public class LogResponseDto<T> {

    /**
     * name of the request this response sent for
     */
    private String name;

    /**
     * any output data
     */
    private LogResponsePayload<T> payload;

    public LogResponseDto() {
    }

    public LogResponseDto(String name, String key, LogResponsePayload<T> payload) {
        this.name = name;
        this.payload = payload;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LogResponsePayload<T> getPayload() {
        return payload;
    }

    public void setPayload(LogResponsePayload<T> payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "LogResponseDto{" +
                "name='" + name + '\'' +
                ", payload=" + payload +
                '}';
    }
}
