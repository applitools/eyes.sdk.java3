package com.applitools.eyes.utils;

import java.util.HashMap;
import java.util.Map;

public class ChromeMobileEmulationDeviceSettings {
    private Map<String, Object> map;

    public ChromeMobileEmulationDeviceSettings(String userAgent, int width, int height, int pixelRatio) {
        Map<String,Object> deviceMetrics = new HashMap<>();
        deviceMetrics.put("width", width);
        deviceMetrics.put("height", height);
        deviceMetrics.put("pixelRatio", pixelRatio);

        map = new HashMap<>();
        map.put("deviceMetrics", deviceMetrics);
        map.put("userAgent", userAgent);
    }

    public Map<String, Object> toMap() {
        return map;
    }
}
