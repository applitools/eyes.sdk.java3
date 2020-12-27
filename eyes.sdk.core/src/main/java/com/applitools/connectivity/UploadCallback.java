package com.applitools.connectivity;

import com.applitools.connectivity.api.AsyncRequest;
import com.applitools.connectivity.api.AsyncRequestCallback;
import com.applitools.connectivity.api.Response;
import com.applitools.eyes.TaskListener;
import com.applitools.eyes.logging.Stage;
import com.applitools.utils.GeneralUtils;
import org.apache.http.HttpStatus;

import javax.ws.rs.HttpMethod;
import java.io.IOException;

public class UploadCallback implements AsyncRequestCallback {

    private static final int UPLOAD_TIMEOUT = 60 * 1000;
    private static final int TIME_THRESHOLD = 20 * 1000;

    private final TaskListener<String> listener;
    private final ServerConnector serverConnector;
    private final String targetUrl;
    private final byte[] bytes;
    private final String contentType;
    private final String mediaType;

    private int sleepDuration = 5000;
    private int timePassed = 0;

    public UploadCallback(TaskListener<String> listener, ServerConnector serverConnector,
                          String targetUrl, byte[] bytes, String contentType, String mediaType) {
        this.listener = listener;
        this.serverConnector = serverConnector;
        this.targetUrl = targetUrl;
        this.bytes = bytes;
        this.contentType = contentType;
        this.mediaType = mediaType;
    }

    @Override
    public void onComplete(Response response) {
        int statusCode = response.getStatusCode();
        String statusPhrase = response.getStatusPhrase();
        response.close();

        if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
            listener.onComplete(targetUrl);
            return;
        }

        String errorMessage = String.format("Status: %d %s.",
                statusCode, statusPhrase);

        if (statusCode < 500) {
            onFail(new IOException(String.format("Failed uploading image. %s", errorMessage)));
            return;
        }

        if (timePassed >= UPLOAD_TIMEOUT) {
            onFail(new IOException("Failed uploading image"));
            return;
        }

        if (timePassed >= TIME_THRESHOLD) {
            sleepDuration = 10 * 1000;
        }

        try {
            Thread.sleep(sleepDuration);
        } catch (InterruptedException e) {
            onFail(e);
            return;
        }

        timePassed += sleepDuration;
        uploadDataAsync();
    }

    @Override
    public void onFail(Throwable throwable) {
        GeneralUtils.logExceptionStackTrace(serverConnector.logger, Stage.GENERAL, throwable);
        listener.onFail();
    }

    public void uploadDataAsync() {
        AsyncRequest request = serverConnector.makeEyesRequest(new ServerConnector.HttpRequestBuilder() {
            @Override
            public AsyncRequest build() {
                return serverConnector.restClient.target(targetUrl).asyncRequest(mediaType);
            }
        });
        request = request.header("X-Auth-Token", serverConnector.getRenderInfo().getAccessToken())
                .header("x-ms-blob-type", "BlockBlob");
        serverConnector.sendAsyncRequest(request, HttpMethod.PUT, this, bytes, contentType);
    }
}
