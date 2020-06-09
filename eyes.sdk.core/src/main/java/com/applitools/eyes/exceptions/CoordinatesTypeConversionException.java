package com.applitools.eyes.exceptions;

import com.applitools.eyes.CoordinatesType;
import com.applitools.eyes.EyesException;

/**
 * Encapsulates an error converting between two coordinate types.
 */
@SuppressWarnings("UnusedDeclaration")
public class CoordinatesTypeConversionException extends EyesException {
    public CoordinatesTypeConversionException(String message) {
        super(message);
    }

    public CoordinatesTypeConversionException(String message, Throwable e) {
        super(message, e);
    }

    /**
     * Represents an error trying to convert between two coordinate types.
     *
     * @param from The source coordinates type.
     * @param to The target coordinates type.
     */
    public CoordinatesTypeConversionException(CoordinatesType from,
              CoordinatesType to) {
        this(String.format("Cannot convert from '%s' to '%s'",from, to));
    }
}
