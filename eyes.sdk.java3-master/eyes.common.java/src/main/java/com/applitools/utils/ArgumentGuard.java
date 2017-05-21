package com.applitools.utils;

/**
 * Argument validation utilities.
 */
public class ArgumentGuard {

    private ArgumentGuard() {}

    /**
     * Fails if the input parameter is null.
     * @param param The input parameter.
     * @param paramName The input parameter name.
     */
    public static void notNull(Object param, String paramName)
            throws IllegalArgumentException {
        if (null == param) {
            throw new IllegalArgumentException(paramName + " is null");
        }
    }

    /**
     * Fails if the input parameter equals the input value.
     * @param param The input parameter.
     * @param value The input value.
     * @param paramName The input parameter name.
     */
    public static void notEqual(Object param, Object value, String paramName) {
        if (param == value || (param != null && param.equals(value))) {
            throw new IllegalArgumentException(paramName + " == " + value);
        }
    }

    /**
     * Fails if the input parameter string is null or empty.
     * @param param The input parameter.
     * @param paramName The input parameter name.
     */
    public static void notNullOrEmpty(String param, String paramName)
            throws IllegalArgumentException {
        notNull(param, paramName);
        if (param.length() == 0) {
            throw new IllegalArgumentException(paramName + " is empty");
        }
    }

    /**
     * Fails if the input parameter is not null.
     * @param param The input parameter.
     * @param paramName The input parameter name.
     */
    @SuppressWarnings("UnusedDeclaration")
    public static void isNull(Object param, String paramName)
            throws IllegalArgumentException{
        if (null != param) {
            throw new IllegalArgumentException(paramName + " is not null");
        }
    }

    /**
     * Fails if the input integer parameter is negative.
     * @param param The input parameter.
     * @param paramName The input parameter name.
     */
    public static void greaterThanOrEqualToZero(long param, String paramName)
            throws IllegalArgumentException {
        if (0 > param) {
            throw new IllegalArgumentException(paramName + " < 0");
        }
    }

    /**
     * Fails if the input integer parameter is smaller than 1.
     * @param param The input parameter.
     * @param paramName The input parameter name.
     */
    public static void greaterThanZero(long param, String paramName)
            throws IllegalArgumentException {
        if (0 >= param) {
            throw new IllegalArgumentException(paramName + " < 1");
        }
    }

    /**
     * Fails if the input integer parameter is below or equal to 0.
     * @param param The input parameter.
     * @param paramName The input parameter name.
     */
    public static void greaterThanZero(double param, String paramName)
            throws IllegalArgumentException {
        if (0 >= param) {
            throw new IllegalArgumentException(paramName + " < 1");
        }
    }

    /**
     * Fails if the input integer parameter is equal to 0.
     * @param param The input parameter.
     * @param paramName The input parameter name.
     */
    @SuppressWarnings("UnusedDeclaration")
    public static void notZero(long param, String paramName)
            throws IllegalArgumentException {
        if (0 == param) {
            throw new IllegalArgumentException(paramName + " == 0");
        }
    }

    /**
     * Fails if isValid is false.
     * @param isValid Whether the current state is valid.
     * @param errMsg A description of the error.
     */
    public static void isValidState(boolean isValid, String errMsg)
            throws IllegalStateException {
        if (!isValid) {
            throw new IllegalStateException(errMsg);
        }
    }
}
