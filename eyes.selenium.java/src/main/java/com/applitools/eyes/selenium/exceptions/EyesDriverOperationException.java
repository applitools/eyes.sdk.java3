package com.applitools.eyes.selenium.exceptions;

import com.applitools.eyes.EyesException;

/**
 * Encapsulates an error when trying to perform an action using WebDriver.
 */
public class EyesDriverOperationException extends EyesException {
    /**
     * Creates an EyesException instance.
     * @param message A description of the error.
     */
    public EyesDriverOperationException(String message) {
        super(message);
    }

    /**
     * Creates an EyesException instance.
     * @param message A description of the error.
     * @param e The throwable this exception should wrap.
     */
    public EyesDriverOperationException(String message, Throwable e) {
        super(message, e);
    }
}
