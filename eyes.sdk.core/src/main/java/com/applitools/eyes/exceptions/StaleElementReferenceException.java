package com.applitools.eyes.exceptions;

public class StaleElementReferenceException extends RuntimeException {
    private static final String SUPPORT_URL = "https://selenium.dev/exceptions/#stale_element_reference";

    public StaleElementReferenceException(String message) {
        super(message);
    }
}
