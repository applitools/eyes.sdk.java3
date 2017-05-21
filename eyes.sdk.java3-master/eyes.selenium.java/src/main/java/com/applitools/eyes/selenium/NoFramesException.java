/*
 * Applitools SDK for Selenium integration.
 */
package com.applitools.eyes.selenium;

import com.applitools.eyes.EyesException;

public class NoFramesException extends EyesException {

    public NoFramesException(String message) {
        super(message);
    }
}