package com.applitools.utils;

/**
 * A property handler for read-only properties (i.e., set always fails).
 */
public class ReadOnlyPropertyHandler<T> implements PropertyHandler<T> {
    private final T obj;

    public ReadOnlyPropertyHandler(T obj) {
        this.obj = obj;
    }

    /**
     * This method does nothing. It simply returns false.
     * @param obj The object to set.
     * @return Always returns false.
     */
    public boolean set(T obj) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public T get() {
        return obj;
    }
}
