package com.applitools.connectivity.api;

import com.applitools.eyes.Logger;
import com.applitools.utils.ArgumentGuard;
import com.sun.jersey.api.client.AsyncWebResource;
import com.sun.jersey.api.client.WebResource;

public class ConnectivityTargetImpl extends ConnectivityTarget {

    WebResource target;
    AsyncWebResource asyncTarget;

    ConnectivityTargetImpl(WebResource target, AsyncWebResource asyncTarget, Logger logger) {
        super(logger);
        this.target = target;
        this.asyncTarget = asyncTarget;
    }

    @Override
    public ConnectivityTarget path(String path) {
        ArgumentGuard.notNull(path, "path");
        target = target.path(path);
        asyncTarget = asyncTarget.path(path);
        return this;
    }

    @Override
    public ConnectivityTarget queryParam(String name, String value) {
        ArgumentGuard.notNullOrEmpty(name, "name");
        ArgumentGuard.notNullOrEmpty(value, "value");
        target = target.queryParam(name, value);
        asyncTarget = asyncTarget.queryParam(name, value);
        return this;
    }

    @Override
    public Request request(String... acceptableResponseTypes) {
        return new RequestImpl(target.accept(acceptableResponseTypes), logger);
    }

    @Override
    public AsyncRequest asyncRequest(String... acceptableResponseTypes) {
        return new AsyncRequestImpl(asyncTarget.accept(acceptableResponseTypes), logger);
    }
}
