package com.applitools.connectivity.api;

import com.applitools.utils.ArgumentGuard;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class RequestImpl implements Request {

    WebResource.Builder request;

    RequestImpl(WebResource.Builder request) {
        this.request = request;
    }

    @Override
    public Request header(String name, String value) {
        ArgumentGuard.notNullOrEmpty(name, "name");
        ArgumentGuard.notNullOrEmpty(value, "value");
        request = request.header(name, value);
        return this;
    }

    @Override
    public Response method(String method, Object data, String mediaType) {
        ArgumentGuard.notNullOrEmpty(method, "method");
        if (data == null) {
            return new ResponseImpl(request.method(method, ClientResponse.class));
        }

        if (mediaType == null) {
            request = request.entity(data);
        } else {
            request = request.entity(data, mediaType);
        }

        return new ResponseImpl(request.method(method, ClientResponse.class));
    }
}
