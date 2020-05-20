package com.applitools.eyes;

public interface IDownloadListener <T>{
        void onDownloadComplete(T downloadedResource);
        void onDownloadFailed();
}
