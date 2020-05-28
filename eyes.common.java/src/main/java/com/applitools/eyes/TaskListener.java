package com.applitools.eyes;

public interface TaskListener<T> {
        void onComplete(T taskResponse);
        void onFail();
}
