package com.applitools.connectivity.api;

import com.applitools.utils.ArgumentGuard;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.Response;
import java.util.concurrent.Future;

public class AsyncRequestImpl implements AsyncRequest {

    Invocation.Builder request;

    AsyncRequestImpl(Invocation.Builder request) {
        this.request = request;
    }


    @Override
    public AsyncRequest header(String name, String value) {
        ArgumentGuard.notNullOrEmpty(name, "name");
        ArgumentGuard.notNullOrEmpty(value, "value");
        request = request.header(name, value);
        return this;
    }

    @Override
    public Future<?> method(String method, final AsyncRequestCallback callback, Object data, String contentType) {
        ArgumentGuard.notNullOrEmpty(method, "method");

        InvocationCallback<Response> invocationCallback = new InvocationCallback<Response>() {
            @Override
            public void completed(Response response) {
                callback.onComplete(new ResponseImpl(response));
            }

            @Override
            public void failed(Throwable throwable) {
                callback.onFail(throwable);
            }
        };

        if (data == null || contentType == null) {
            return request.async().method(method, invocationCallback);
        }

        return request.async().method(method, Entity.entity(data, contentType), invocationCallback);
    }
}
