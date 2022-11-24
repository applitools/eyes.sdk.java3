package com.applitools.eyes.playwright.universal;

import com.applitools.eyes.exceptions.StaleElementReferenceException;

public class PlaywrightStaleElementReferenceException extends StaleElementReferenceException {

    @Override
    public void throwException(String message) {
        throw new RuntimeException(message);
    }
}
