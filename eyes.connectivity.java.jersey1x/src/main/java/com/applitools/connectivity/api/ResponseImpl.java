package com.applitools.connectivity.api;

import com.applitools.utils.ArgumentGuard;
import com.sun.jersey.api.client.ClientResponse;

public class ResponseImpl implements Response {

    ClientResponse response;

    ResponseImpl(ClientResponse response) {
        this.response = response;
    }

    @Override
    public int getStatusCode() {
        return response.getStatus();
    }

    @Override
    public String getStatusPhrase() {
        return response.getStatusInfo().getReasonPhrase();
    }

    @Override
    public String getHeader(String name) {
        ArgumentGuard.notNullOrEmpty(name, "name");
        return response.getHeaders().getFirst(name);
    }

    @Override
    public <T> T readEntity(Class<T> type) {
        return response.getEntity(type);
    }

    @Override
    public void close() {
        response.close();
    }
}
