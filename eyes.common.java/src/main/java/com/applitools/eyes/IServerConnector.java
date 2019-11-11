package com.applitools.eyes;

import com.applitools.IResourceUploadListener;
import com.applitools.eyes.visualgrid.services.IResourceFuture;
import com.applitools.eyes.visualgrid.model.*;

import java.net.URI;
import java.net.URL;
import java.util.List;

/**
 * Defines the interface which should be implemented by a ServerConnector.
 */
public interface IServerConnector {

    String API_SESSIONS = "api/sessions";
    String RUNNING_DATA_PATH = API_SESSIONS + "/running/data";
    String CLOSE_BATCH = "api/sessions/batches/%s/close/bypointerid";

    //Rendering Grid
    String RENDER_INFO_PATH = API_SESSIONS + "/renderinfo";
    String RESOURCES_SHA_256 = "/resources/sha256/";
    String RENDER_STATUS = "/render-status";
    String RENDER = "/render";

    int TIMEOUT = 1000 * 60 * 5; // 5 Minutes
    String API_PATH = "/" + API_SESSIONS + "/running";
    String DEFAULT_CHARSET_NAME = "UTF-8";
    int THREAD_SLEEP_MILLIS = 3;
    int NUM_OF_RETRIES = 20;

    void setApiKey(String apiKey);

    String getApiKey();

    void setServerUrl(URI serverUrl);

    URI getServerUrl();

    void setLogger(Logger logger);

    Logger getLogger();


    void setProxy(AbstractProxySettings proxySettings);
    AbstractProxySettings getProxy();

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
     * @throws EyesException the exception is being thrown when start session failed
     */
    RunningSession startSession(SessionStartInfo sessionStartInfo);

    /**
     * Stops the running session.
     *
     * @param runningSession The running session to be stopped.
     * @param isAborted Indicates that the session is being aborted
     * @param save Indicates whether the server should update the baseline.
     * @return TestResults object for the stopped running session
     * @throws EyesException the exception is being thrown when stopSession failed
     */
    TestResults stopSession(final RunningSession runningSession,
                                   final boolean isAborted, final boolean save);

    /**
     * Deletes the given test result
     *
     * @param testResults The session to delete by test results.
     * @throws EyesException the exception is being thrown when deleteSession failed
     */
    void deleteSession(TestResults testResults);

    /**
     * Matches the current window (held by the WebDriver) to the expected
     * window.
     *
     * @param runningSession The current agent's running session.
     * @param matchData Encapsulation of a capture taken from the application.
     * @return The results of the window matching.
     * @throws EyesException the exception is being thrown when deleteSession matchWindow
     */
    MatchResult matchWindow(RunningSession runningSession,
                            MatchWindowData matchData);

    /**
     * Downloads string from a given Url
     *
     * @param uri The URI from which the IServerConnector will download the string
     * @param isSecondRetry Indicates if a retry is mandatory onFailed - 2 retries per request
     * @param listener the listener will be called when the request will be resolved.
     */
     void downloadString(URL uri, boolean isSecondRetry, IDownloadListener<String> listener);

    /**
     * Downloads string from a given Url.
     *
     * @param uri The URI from which the IServerConnector will download the string
     * @param userAgent user agent to send to server
     * @return A future which will be resolved when the resources is downloaded.
     */
    IResourceFuture downloadResource(URL uri, String userAgent);


    /**
     * Posting the DOM snapshot to the server and returns
     * @param domJson JSON as String.
     * @return URL to the JSON that is stored by the server.
     */
    String postDomSnapshot(String domJson);


    /**
     * @return the render info from the server to be used later on.
     */
    RenderingInfo getRenderInfo();

    /**
     * Initiate a rendering using RenderingGrid API
     *
     * @param renderRequests renderRequest The current agent's running session.
     * @return {@code List<RunningRender>} The results of the render request
     */
    List<RunningRender> render(RenderRequest... renderRequests);

    /**
     * Check if resource exists on the server
     *
     * @param runningRender The running render (for second request only)
     * @param resource The resource to use
     * @return Whether resource exists on the server or not
     */
    boolean renderCheckResource(RunningRender runningRender, RGridResource resource);

    /**
     * Upload resource to the server
     *
     * @param runningRender The running render (for second request only)
     * @param resource The resource to upload
     * @param listener The callback wrapper for the upload result.
     * @param userAgent the userAgent to send to server
     * @return true if resource was uploaded
     */
    IPutFuture renderPutResource(RunningRender runningRender, RGridResource resource, String userAgent, IResourceUploadListener listener);

    /**
     * Get the rendering status for current render
     *
     * @param runningRender The running render
     * @return RenderStatusResults The render's status
     */
    RenderStatusResults renderStatus(RunningRender runningRender);

    /**
     * Get the rendering status for current render
     *
     * @param renderIds The running renderId
     * @return The render's status
     */
    List<RenderStatusResults> renderStatusById(String... renderIds);

    IResourceFuture createResourceFuture(RGridResource gridResource, String userAgent);

    void setRenderingInfo(RenderingInfo renderInfo);

    void closeBatch(String batchId);

    void closeConnector();
}
