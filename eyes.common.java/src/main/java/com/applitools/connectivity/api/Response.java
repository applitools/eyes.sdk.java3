package com.applitools.connectivity.api;

public interface Response extends AutoCloseable {
    int getStatusCode();

    String getStatusPhrase();

    String getHeader(String name);

    <T> T readEntity(Class<T> type);
}
