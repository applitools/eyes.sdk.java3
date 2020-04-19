package com.applitools.connectivity.api;

import com.applitools.utils.ArgumentGuard;
import com.sun.jersey.api.client.WebResource;

public class ConnectivityTargetImpl implements ConnectivityTarget {

    WebResource target;

    ConnectivityTargetImpl(WebResource target) {
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
        return new RequestImpl(target.accept(acceptableResponseTypes));
    }
}
