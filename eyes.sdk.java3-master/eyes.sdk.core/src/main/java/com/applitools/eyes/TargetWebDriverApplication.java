package com.applitools.eyes;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.net.URL;

/*
 * The remote web-driver controlling the browser that hosts the application
 * under test.
 */
@JsonPropertyOrder({"$type"})
public class TargetWebDriverApplication extends TargetApplication {
    private String url;
    private String sessionId;
    private String userAgent;

    /**
     * Creates a new TargetWebDriverApplication instance.
     *
     * @param webDriverUrl The URL of the remote web-driver or {@code null}
     *                     if unknown.
     * @param sessionId    The id of the web-driver session or {@code null} if
     *                     unknown.
     * @param userAgent    The user agent string of the browser hosting the
     *                     application under test or {@code null} if unknown.
     */
    public TargetWebDriverApplication(URL webDriverUrl, String sessionId,
                                      String userAgent) {

        if (webDriverUrl != null) {
            this.url = webDriverUrl.toString();
            if (!this.url.endsWith("/")) {
                this.url += "/";
            }
        }

        this.sessionId = sessionId;
        this.userAgent = userAgent;
    }

    /**
     * Gets the URL of the remote web-driver controlling the browser that hosts
     * the application under test or {@code null} if unknown.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Gets the ID of the web-driver session or {@code null} if unknown.
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Gets the user-agent string of the browser that hosts the application
     * under test or {@code null} if unknown.
     */
    public String getUserAgent() {
        return userAgent;
    }
}