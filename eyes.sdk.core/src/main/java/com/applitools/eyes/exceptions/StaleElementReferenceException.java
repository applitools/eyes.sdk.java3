package com.applitools.eyes.exceptions;

public abstract class StaleElementReferenceException extends RuntimeException {

    public abstract void throwException(String message);
}
