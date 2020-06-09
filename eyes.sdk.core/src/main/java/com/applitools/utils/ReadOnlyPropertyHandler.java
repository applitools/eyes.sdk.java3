package com.applitools.utils;

import com.applitools.eyes.Logger;

/**
 * A property handler for read-only properties (i.e., set always fails).
 */
public class ReadOnlyPropertyHandler<T> implements PropertyHandler<T> {
    private final Logger logger;
    private final T obj;

    public ReadOnlyPropertyHandler(Logger logger, T obj) {
        this.logger = logger;
        this.obj = obj;
    }

    /**
     * This method does nothing. It simply returns false.
     * @param obj The object to set.
     * @return Always returns false.
     */
    public boolean set(T obj) {
        logger.verbose(String.format("Ignored. (%s)",
                getClass().getSimpleName()));
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public T get() {
        return obj;
    }
}
