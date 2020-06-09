package com.applitools.eyes.exceptions;

import com.applitools.eyes.EyesException;

/**
 * Encapsulates an error when a type or action is not supported.
 */
public class NotSupportedException extends EyesException {
    public NotSupportedException(String message) {
        super(message);
    }

    public NotSupportedException(String message, Throwable e) {
        super(message, e);
    }
}
