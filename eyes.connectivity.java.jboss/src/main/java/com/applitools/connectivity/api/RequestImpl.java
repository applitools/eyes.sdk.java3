package com.applitools.connectivity.api;

import com.applitools.eyes.Logger;
import com.applitools.utils.ArgumentGuard;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;

public class RequestImpl extends Request {

    Invocation.Builder request;

    RequestImpl(Invocation.Builder request, Logger logger) {
        super(logger);
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
            return new ResponseImpl(request.method(method), logger);
        }

        if (contentType == null) {
            throw new IllegalArgumentException("Content type can't be null");
        }

        return new ResponseImpl(request.method(method, Entity.entity(data, contentType)), logger);
    }
}
