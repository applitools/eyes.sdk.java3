package com.applitools.connectivity;

import com.applitools.connectivity.api.AsyncRequest;
import com.applitools.connectivity.api.AsyncRequestCallback;
import com.applitools.connectivity.api.Request;
import com.applitools.connectivity.api.Response;
import com.applitools.eyes.Logger;
import com.applitools.utils.EyesSyncObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.ws.rs.HttpMethod;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.Mockito.*;

public class TestApi {
    @Test
    public void testRequestFail() {
        final Response mockedResponse = mock(Response.class);
        final AtomicBoolean isFirstTime = new AtomicBoolean(true);

        Request request = spy(new Request(new Logger()) {
            @Override
            public Request header(String name, String value) {
                return this;
            }

            @Override
            protected Response methodInner(String method, Object data, String contentType) {
                if (isFirstTime.get()) {
                    isFirstTime.set(false);
                    throw new IllegalStateException();
                }

                return mockedResponse;
            }
        });

        Response response = request.method(HttpMethod.GET, null, null);
        Assert.assertEquals(response, mockedResponse);
        verify(request, times(2)).method(HttpMethod.GET, null, null);
    }

    @Test
    public void testAsyncRequestFail() throws InterruptedException {
        final Response mockedResponse = mock(Response.class);
        final AtomicBoolean isFirstTime = new AtomicBoolean(true);

        AsyncRequest request = spy(new AsyncRequest(new Logger()) {
            @Override
            public AsyncRequest header(String name, String value) {
                return this;
            }

            @Override
            public Future<?> method(String method, AsyncRequestCallback callback, Object data, String contentType, boolean logIfError) {
                if (isFirstTime.get()) {
                    isFirstTime.set(false);
                    callback.onFail(new IllegalStateException());
                }

                callback.onComplete(mockedResponse);
                return null;
            }
        });

        final AtomicReference<Response> responseRef = new AtomicReference<>();
        final EyesSyncObject eyesSyncObject = new EyesSyncObject(new Logger(), "");
        AsyncRequestCallback callback = new AsyncRequestCallback() {
            @Override
            public void onComplete(Response response) {
                responseRef.set(response);
                synchronized (eyesSyncObject) {
                    eyesSyncObject.notifyObject();
                }
            }

            @Override
            public void onFail(Throwable throwable) {

            }
        };

        request.method(HttpMethod.GET, callback, null, null);
        synchronized (eyesSyncObject) {
            eyesSyncObject.waitForNotify();
        }
        Assert.assertEquals(responseRef.get(), mockedResponse);
        verify(request, times(2)).method(HttpMethod.GET, callback, null, null);
    }
}
