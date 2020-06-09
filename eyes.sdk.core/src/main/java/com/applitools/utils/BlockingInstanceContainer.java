package com.applitools.utils;

/**
 * A generic container for objects to be shared between threads.
 */
public class BlockingInstanceContainer<T> {
    private T underlying;
    private boolean isClosed;

    public BlockingInstanceContainer() {
        underlying = null;
        isClosed = false;
    }

    /**
     *
     * @param underlying The object to set.
     */
    public synchronized void put(T underlying) {
        ArgumentGuard.isValidState(!isClosed, "Container is closed!");
        this.underlying = underlying;
        notifyAll();
    }

    /**
     *
     * @return The underlying, or null if the thread was interrupted.
     */
    public synchronized T take() {
        while (underlying == null && !isClosed) {
            try {
                wait();
            } catch (InterruptedException e) {
                return null;
            }
        }
        T result = underlying;
        underlying = null;
        return result;
    }

    /**
     * Marks the container as closed. All subsequent calls to {@link #put
     * (Object)} will throw an {@link IllegalStateException}.
     */
    public synchronized void close() {
        isClosed = true;
        notifyAll();
    }
}
