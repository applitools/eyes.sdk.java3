package com.applitools.eyes.selenium.exceptions;

public class StaleElementReferenceException extends com.applitools.eyes.exceptions.StaleElementReferenceException {

    @Override
    public void throwException(String message) {
        throw new org.openqa.selenium.StaleElementReferenceException(message);
    }
}