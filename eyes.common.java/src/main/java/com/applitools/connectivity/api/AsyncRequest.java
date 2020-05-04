package com.applitools.connectivity.api;

import java.util.concurrent.Future;

/**
 * Wrapper for the asynchronous request api of the connectivity packages
 */
public interface AsyncRequest {
    /**
     * Add a new http header to the request
     * @param name The header name
     * @param value The header value
     */
    AsyncRequest header(String name, String value);

    /**
     *
     * @param method The http method for the request
     * @param callback To be called when the response is received
     * @param data The data to send with the request. If null, no data will be sent.
     * @param contentType The data content type.  If null, no data will be sent.
     * @return Response from the server
     */
    Future<?> method(String method, AsyncRequestCallback callback, Object data, String contentType);
}
