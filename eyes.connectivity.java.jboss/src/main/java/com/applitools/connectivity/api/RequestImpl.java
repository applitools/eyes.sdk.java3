package com.applitools.connectivity.api;

import com.applitools.utils.ArgumentGuard;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;

public class RequestImpl implements Request {

    Invocation.Builder request;

    RequestImpl(Invocation.Builder request) {
        this.request = request;
    }

    @Override
    public Request header(String name, String value) {
        ArgumentGuard.notNullOrEmpty(name, "name");
        ArgumentGuard.notNullOrEmpty(value, String.format("value of %s", name));
        request = request.header(name, value);
        return this;
    }

    @Override
    public Response method(String method, byte[] data, String contentType) {
        ArgumentGuard.notNullOrEmpty(method, "method");
        if (data == null) {
            return new ResponseImpl(request.method(method));
        }

        if (contentType == null) {
            throw new IllegalArgumentException("Content type can't be null");
        }

        return new ResponseImpl(request.method(method, Entity.entity(data, contentType)));
    }
}
