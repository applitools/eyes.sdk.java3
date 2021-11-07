package com.applitools.eyes.appium;

import org.openqa.selenium.interactions.Encodable;
import org.openqa.selenium.interactions.InputSource;
import org.openqa.selenium.interactions.Interaction;

import java.util.HashMap;
import java.util.Map;

public class PointerCancelInteraction extends Interaction implements Encodable {

    protected PointerCancelInteraction(InputSource source) {
        super(source);
    }

    @Override
    public Map<String, Object> encode() {
        Map<String, Object> toReturn = new HashMap<>();
        toReturn.put("type", "pointerCancel");
        return toReturn;
    }
}
