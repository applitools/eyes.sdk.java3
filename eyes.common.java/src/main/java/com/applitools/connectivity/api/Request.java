package com.applitools.connectivity.api;

import com.applitools.eyes.Logger;

public abstract class Request {

    public static String CONTENT_LENGTH_HEADER = "Content-Length";
    public static String CONTENT_TYPE_HEADER = "Content-Type";

    protected Logger logger;

    public Request(Logger logger) {
        this.logger = logger;
    }

    /**
     * Add a new http header to the request
     * @param name The header name
     * @param value The header value
     */
    public abstract Request header(String name, String value);

    /**
     *
     * @param method The http method for the request
     * @param data The data to send with the request. If null, no data will be sent.
     * @param contentType The data content type.  If null, no data will be sent.
     * @return Response from the server
     */
    public abstract Response method(String method, Object data, String contentType);
}
