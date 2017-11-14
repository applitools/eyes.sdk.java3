/*
 * Applitools SDK for Selenium integration.
 */
package com.applitools.eyes.triggers;

import com.applitools.eyes.Region;
import com.applitools.eyes.Trigger;
import com.applitools.utils.ArgumentGuard;

/**
 * Encapsulates a text input by the user.
 */
public class TextTrigger extends Trigger {
    private String text;

    // Can be null.
    private Region control;

    public TextTrigger(Region control, String text) {
        ArgumentGuard.notNull(control, "control");
        ArgumentGuard.notNullOrEmpty(text, "text");

        this.text = text;
        this.control = control;
    }

    public String getText() {
        return text;
    }

    public Region getControl() {
        return control;
    }

    public TriggerType getTriggerType() {
        return TriggerType.Text;
    }

    @Override
    public String toString() {
        return String.format("Text [%s] %s", control, text);
    }
}