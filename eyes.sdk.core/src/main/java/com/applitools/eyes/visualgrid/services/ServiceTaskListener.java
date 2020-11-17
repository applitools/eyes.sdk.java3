package com.applitools.eyes.visualgrid.services;

public interface ServiceTaskListener<T> {
    void onComplete(T taskResponse);

    void onFail(Throwable t);
}
