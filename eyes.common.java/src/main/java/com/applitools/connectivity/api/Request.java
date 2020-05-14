package com.applitools.connectivity.api;

public interface Request {

    String CONTENT_LENGTH_HEADER = "Content-Length";
    String CONTENT_TYPE_HEADER = "Content-Type";

    /**
     * Add a new http header to the request
     * @param name The header name
     * @param value The header value
     */
    Request header(String name, String value);

    /**
     *
     * @param method The http method for the request
     * @param data The data to send with the request. If null, no data will be sent.
     * @param contentType The data content type.  If null, no data will be sent.
     * @return Response from the server
     */
    Response method(String method, byte[] data, String contentType);
}
