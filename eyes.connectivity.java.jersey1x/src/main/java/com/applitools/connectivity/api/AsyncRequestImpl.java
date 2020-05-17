package com.applitools.connectivity.api;

import com.applitools.eyes.Logger;
import com.applitools.utils.ArgumentGuard;
import com.sun.jersey.api.client.AsyncWebResource;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.async.TypeListener;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AsyncRequestImpl extends AsyncRequest {

    AsyncWebResource.Builder request;

    AsyncRequestImpl(AsyncWebResource.Builder request, Logger logger) {
        super(logger);
        this.request = request;
    }

    @Override
    public AsyncRequest header(String name, String value) {
        ArgumentGuard.notNullOrEmpty(name, "name");
        ArgumentGuard.notNullOrEmpty(value, String.format("value of %s", name));
        request = request.header(name, value);
        return this;
    }

    @Override
    public Future<?> method(String method, final AsyncRequestCallback callback, Object data, String contentType) {
        ArgumentGuard.notNullOrEmpty(method, "method");
        if (data != null) {
            if (contentType == null) {
                request = request.entity(data);
            } else {
                request = request.entity(data, contentType);
            }
        }

        return request.method(method, new TypeListener<ClientResponse>(ClientResponse.class) {
            @Override
            public void onComplete(Future<ClientResponse> f) throws InterruptedException {
                try {
                    callback.onComplete(new ResponseImpl(f.get(), logger));
                } catch (ExecutionException e) {
                    callback.onFail(e);
                }
            }
        });
    }
}
