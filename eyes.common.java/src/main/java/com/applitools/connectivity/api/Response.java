package com.applitools.connectivity.api;

public interface Response {
    int getStatusCode();

    String getStatusPhrase();

    /**
     * Get a response header
     * @param name The name of the header
     * @param ignoreCase If true, ignores case
     * @return The value of the header
     */
    String getHeader(String name, boolean ignoreCase);

    <T> T readEntity(Class<T> type);

    void close();
}
