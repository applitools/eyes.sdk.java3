package com.applitools.connectivity.api;

public interface Target {
    /**
     * @param path Appends path to the URI
     */
    Target path(String path);

    /**
     * Configures a query parameter on the URI
     * @param name Parameter name
     * @param value Parameter value
     */
    Target queryParam(String name, String value);

    /**
     * Creates a request for sending to the server
     * @param acceptableResponseTypes Accepted response media types
     * @return The request
     */
    Request request(String... acceptableResponseTypes);
}
