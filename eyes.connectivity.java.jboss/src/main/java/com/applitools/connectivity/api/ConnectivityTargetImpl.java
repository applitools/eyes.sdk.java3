package com.applitools.connectivity.api;

import com.applitools.eyes.Logger;
import com.applitools.utils.ArgumentGuard;

import javax.ws.rs.client.WebTarget;

public class ConnectivityTargetImpl extends ConnectivityTarget {

    WebTarget target;

    ConnectivityTargetImpl(WebTarget target, Logger logger) {
        super(logger);
        this.target = target;
    }

    @Override
    public ConnectivityTarget path(String path) {
        ArgumentGuard.notNull(path, "path");
        target = target.path(path);
        return this;
    }

    @Override
    public ConnectivityTarget queryParam(String name, String value) {
        ArgumentGuard.notNullOrEmpty(name, "name");
        ArgumentGuard.notNullOrEmpty(value, "value");
        target = target.queryParam(name, value);
        return this;
    }

    @Override
    public Request request(String... acceptableResponseTypes) {
        return new RequestImpl(target.request(acceptableResponseTypes), logger);
    }

    @Override
    public AsyncRequest asyncRequest(String... acceptableResponseTypes) {
        return new AsyncRequestImpl(target.request(acceptableResponseTypes), logger);
    }
}
