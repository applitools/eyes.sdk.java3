package com.applitools.connectivity.api;

import com.applitools.eyes.Logger;
import com.applitools.utils.ArgumentGuard;
import com.sun.jersey.api.client.ClientResponse;

import javax.ws.rs.core.MultivaluedMap;

public class ResponseImpl extends Response {

    ClientResponse response;

    ResponseImpl(ClientResponse response, Logger logger) {
        super(logger);
        this.response = response;
        logIfError();
    }

    @Override
    public int getStatusCode() {
        return response.getStatus();
    }

    @Override
    public String getStatusPhrase() {
        return ClientResponse.Status.fromStatusCode(response.getStatus()).getReasonPhrase();
    }

    @Override
    public String getHeader(String name, boolean ignoreCase) {
        ArgumentGuard.notNullOrEmpty(name, "name");
        MultivaluedMap<String, String> headers = response.getHeaders();
        if (!ignoreCase) {
            return headers.getFirst(name);
        }

        for (String key : headers.keySet()) {
            if (name.equalsIgnoreCase(key)) {
                return headers.getFirst(key);
            }
        }

        return null;
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
