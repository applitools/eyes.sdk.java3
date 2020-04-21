package com.applitools.connectivity.api;

public interface AsyncRequestCallback {
    void onComplete(Response response);

    void onFail(Throwable throwable);
}
