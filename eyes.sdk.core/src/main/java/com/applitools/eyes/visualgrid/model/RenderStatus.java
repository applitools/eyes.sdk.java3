package com.applitools.eyes.visualgrid.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public enum RenderStatus {

    NEED_MORE_RESOURCE, RENDERING, RENDERED, ERROR, NEED_MORE_DOM;

    private static Map<String, RenderStatus> namesMap = new HashMap<>(3);

    /**
     * A mapping between the integer code and its corresponding text to facilitate lookup by code.
     */
    private static Map<Integer, RenderStatus> valueToTextMapping;

    static {
        namesMap.put("need-more-resources", NEED_MORE_RESOURCE);
        namesMap.put("rendering", RENDERING);
        namesMap.put("rendered", RENDERED);
        namesMap.put("error", ERROR);
    }

    @JsonCreator
    public static RenderStatus forValue(String value) {
        return namesMap.get(StringUtils.lowerCase(value));
    }

    @JsonValue
    public String toValue() {
        for (Map.Entry<String, RenderStatus> entry : namesMap.entrySet()) {
            if (entry.getValue() == this)
                return entry.getKey();
        }

        return null; // or fail
    }



    RenderStatus() {
    }
}


