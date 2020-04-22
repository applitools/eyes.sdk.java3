package com.applitools.connectivity.api;

public interface ConnectivityTarget {
    /**
     * @param path Appends path to the URI
     */
    ConnectivityTarget path(String path);

    /**
     * Configures a query parameter on the URI
     * @param name Parameter name
     * @param value Parameter value
     */
    ConnectivityTarget queryParam(String name, String value);

    /**
     * Creates a request for sending to the server
     * @param acceptableResponseTypes Accepted response media types
     * @return The request
     */
    Request request(String... acceptableResponseTypes);

    /**
     * Creates an async request for sending to the server
     * @param acceptableResponseTypes Accepted response media types
     * @return The request
     */
    AsyncRequest asyncRequest(String... acceptableResponseTypes);
}
