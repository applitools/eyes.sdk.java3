package com.applitools.connectivity.api;

import com.applitools.utils.ArgumentGuard;
import com.sun.jersey.api.client.WebResource;

public class TargetImpl implements Target {

    WebResource target;

    TargetImpl(WebResource target) {
        this.target = target;
    }

    @Override
    public Target path(String path) {
        ArgumentGuard.notNull(path, "path");
        target = target.path(path);
        return this;
    }

    @Override
    public Target queryParam(String name, String value) {
        ArgumentGuard.notNullOrEmpty(name, "name");
        ArgumentGuard.notNullOrEmpty(value, "value");
        target = target.queryParam(name, value);
        return this;
    }

    @Override
    public Request request(String... acceptableResponseTypes) {
        return new RequestImpl(target.accept(acceptableResponseTypes));
    }
}
