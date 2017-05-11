package com.applitools.utils;

/**
 * A simple implementation of {@link PropertyHandler}. Allows get/set.
 */
public class SimplePropertyHandler<T> implements PropertyHandler<T> {
    private T obj;

    /**
     * {@inheritDoc}
     */
    public boolean set(T obj) {
        this.obj = obj;
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public T get() {
        return obj;
    }
}
