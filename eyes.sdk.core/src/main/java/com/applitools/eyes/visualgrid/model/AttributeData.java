package com.applitools.eyes.visualgrid.model;

public class AttributeData {

    public String name;

    public String value;

    public AttributeData() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "AttributeData{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
