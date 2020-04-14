package com.applitools.connectivity.api;

import com.applitools.utils.ArgumentGuard;

import javax.ws.rs.client.WebTarget;

public class TargetImpl implements Target {

    WebTarget target;

    TargetImpl(WebTarget target) {
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
        return new RequestImpl(target.request(acceptableResponseTypes));
    }
}
