package com.applitools.connectivity.api;

import com.applitools.eyes.Logger;
import com.applitools.utils.ArgumentGuard;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class RequestImpl extends Request {

    WebResource.Builder request;

    RequestImpl(WebResource.Builder request, Logger logger) {
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
        if (data != null) {
            if (contentType == null) {
                throw new IllegalArgumentException("Content type can't be null");
            } else {
                request = request.entity(data, contentType);
            }
        }

        return new ResponseImpl(request.method(method, ClientResponse.class), logger);
    }
}
