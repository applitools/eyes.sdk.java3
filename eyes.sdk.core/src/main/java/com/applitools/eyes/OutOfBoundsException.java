/*
 * Applitools software.
 */
package com.applitools.eyes;

/**
 * Applitools Eyes exception indicating the a geometrical element is out of
 * bounds (point outside a region, region outside another region etc.)
 */
@SuppressWarnings("UnusedDeclaration")
public class OutOfBoundsException extends EyesException {
    public OutOfBoundsException(String message) {
        super(message);
    }

    public OutOfBoundsException(String message, Throwable e) {
        super(message, e);
    }
}
