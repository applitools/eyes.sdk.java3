package com.applitools.utils;

/**
 * Encapsulates getter/setter behavior. (e.g., set only once etc.).
 */
public interface PropertyHandler<T> {
    /**
     *
     * @param obj The object to set.
     * @return {@code true} if the object was set, {@code false} otherwise.
     */
    boolean set(T obj);

    /**
     *
     * @return The object that was set. (Note that object might also be set
     * in the constructor of an implementation class).
     */
    T get();
}
