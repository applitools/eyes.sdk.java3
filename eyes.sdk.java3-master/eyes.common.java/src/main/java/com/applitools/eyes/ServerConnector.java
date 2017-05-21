package com.applitools.eyes;

import java.net.URI;

/**
 * Defines the interface which should be implemented by a ServerConnector.
 */
public interface ServerConnector {
    void setApiKey(String apiKey);
    String getApiKey();

    void setServerUrl(URI serverUrl);
    URI getServerUrl();

    void setProxy(ProxySettings proxySettings);
    ProxySettings getProxy();

    /**
     *
     * @return The server timeout. (Seconds).
     */
    int getTimeout();

    /**
     * Starts a new running session in the agent. Based on the given parameters,
     * this running session will either be linked to an existing session, or to
     * a completely new session.
     *
     * @param sessionStartInfo The start parameters for the session.
     * @return RunningSession object which represents the current running
     *         session
     * @throws EyesException
     */
    RunningSession startSession(SessionStartInfo sessionStartInfo);

    /**
     * Stops the running session.
     *
     * @param runningSession The running session to be stopped.
     * @return TestResults object for the stopped running session
     * @throws EyesException
     */
    TestResults stopSession(final RunningSession runningSession,
                                   final boolean isAborted, final boolean save);

    /**
     * Matches the current window (held by the WebDriver) to the expected
     * window.
     *
     * @param runningSession The current agent's running session.
     * @param matchData Encapsulation of a capture taken from the application.
     * @return The results of the window matching.
     * @throws EyesException
     */
    MatchResult matchWindow(RunningSession runningSession,
                            MatchWindowData matchData);
}
