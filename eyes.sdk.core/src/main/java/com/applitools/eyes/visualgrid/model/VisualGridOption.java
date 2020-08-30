package com.applitools.eyes.visualgrid.model;

public class VisualGridOption {
    private final String key;
    private final Object value;

    public VisualGridOption(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "VisualGridOption{" +
                "key='" + key + "'" +
                ", value=" + value +
                "}";
    }
}
