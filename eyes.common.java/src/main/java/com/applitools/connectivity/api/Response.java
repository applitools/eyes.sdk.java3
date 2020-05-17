package com.applitools.connectivity.api;

import com.applitools.eyes.Logger;

public abstract class Response {

    protected Logger logger;

    public Response(Logger logger) {
        this.logger = logger;
    }

    public abstract int getStatusCode();

    public abstract String getStatusPhrase();

    /**
     * Get a response header
     * @param name The name of the header
     * @param ignoreCase If true, ignores case
     * @return The value of the header
     */
    public abstract String getHeader(String name, boolean ignoreCase);

    public abstract <T> T readEntity(Class<T> type);

    public abstract void close();

    public void logIfError() {
        try {
            if (getStatusCode() > 300) {
                logger.log(String.format("Got invalid response from the server. Status code: %s. Status Phrase: %s. Response body: %s",
                        getStatusCode(), getStatusPhrase(), readEntity(String.class)));
            }
        } catch (Exception e) {
            logger.log(String.format("Failed logging the response body. Status code: %s", getStatusCode()));
        }
    }
}
