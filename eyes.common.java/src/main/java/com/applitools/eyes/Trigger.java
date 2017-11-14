/*
 * Applitools SDK for Selenium integration.
 */
package com.applitools.eyes;

/**
 * A base class for triggers.
 */
public abstract class Trigger {

    @SuppressWarnings("UnusedDeclaration")
    public enum TriggerType {Unknown, Mouse, Text, Keyboard}

    @SuppressWarnings("UnusedDeclaration")
    public abstract TriggerType getTriggerType();
}