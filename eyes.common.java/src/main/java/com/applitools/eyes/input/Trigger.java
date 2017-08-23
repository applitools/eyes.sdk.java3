/*
 * Applitools SDK for Selenium integration.
 */
package com.applitools.eyes.input;

/**
 * A base class for triggers.
 */
public abstract class Trigger {

    @SuppressWarnings("UnusedDeclaration")
    enum TriggerType {Unknown, Mouse, Text, Keyboard}

    @SuppressWarnings("UnusedDeclaration")
    public abstract TriggerType getTriggerType();
}