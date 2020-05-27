package com.applitools.connectivity.api;

import com.applitools.eyes.Logger;
import com.applitools.utils.ArgumentGuard;

import javax.ws.rs.core.MultivaluedMap;

public class ResponseImpl extends Response {

    javax.ws.rs.core.Response response;

    ResponseImpl(javax.ws.rs.core.Response response, Logger logger) {
        super(logger);
        this.response = response;
        readEntity();
        logIfError();
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
    public String getHeader(String name, boolean ignoreCase) {
        ArgumentGuard.notNullOrEmpty(name, "name");
        MultivaluedMap<String, String> headers = response.getStringHeaders();
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
    public void readEntity() {
        body = response.readEntity(byte[].class);
    }

    @Override
    public void close() {
        response.close();
    }
}
