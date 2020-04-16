package com.applitools.connectivity.api;

public interface Response {
    int getStatusCode();

    String getStatusPhrase();

    String getHeader(String name);

    <T> T readEntity(Class<T> type);

    void close();
}
