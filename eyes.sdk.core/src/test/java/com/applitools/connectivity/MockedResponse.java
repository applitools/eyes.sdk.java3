package com.applitools.connectivity;

import com.applitools.connectivity.api.Response;
import com.applitools.eyes.Logger;

import java.util.HashMap;
import java.util.Map;

public class MockedResponse extends Response {

    private final int statusCode;
    private final String statusPhrase;
    private final Map<String, String> headers;

    public MockedResponse(Logger logger, int statusCode, String statusPhrase, byte[] body) {
        this(logger, statusCode, statusPhrase, body, new HashMap<String, String>());
    }

    public MockedResponse(Logger logger, int statusCode, String statusPhrase, byte[] body, Map<String, String> headers) {
        super(logger);
        this.statusCode = statusCode;
        this.statusPhrase = statusPhrase;
        this.body = body;
        this.headers = headers;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getStatusPhrase() {
        return statusPhrase;
    }

    @Override
    public String getHeader(String name, boolean ignoreCase) {
        return headers.get(name);
    }

    @Override
    protected Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    protected void readEntity() {

    }

    @Override
    public void close() {

    }
}
