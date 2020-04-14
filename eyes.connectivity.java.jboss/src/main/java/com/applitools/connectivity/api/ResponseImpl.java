package com.applitools.connectivity.api;

import com.applitools.utils.ArgumentGuard;

public class ResponseImpl implements Response {

    javax.ws.rs.core.Response response;

    ResponseImpl(javax.ws.rs.core.Response response) {
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
        return response.getHeaderString(name);
    }

    @Override
    public <T> T readEntity(Class<T> type) {
        return response.readEntity(type);
    }

    @Override
    public void close() {
        response.close();
    }
}
