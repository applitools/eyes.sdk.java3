/*
 * Applitools SDK for Selenium integration.
 */
package com.applitools.eyes;

import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;

public class MatchWindowTask {

    private static final int MATCH_INTERVAL = 500; // Milliseconds

    private final Logger logger;
    private final ServerConnector serverConnector;
    private final RunningSession runningSession;
    private final int defaultRetryTimeout;
    private final AppOutputProvider appOutputProvider;


    /**
     * @param logger            A logger instance.
     * @param serverConnector   Our gateway to the agent
     * @param runningSession    The running session in which we should match the
     *                          window
     * @param retryTimeout      The default total time to retry matching (ms).
     * @param appOutputProvider A callback for getting the application output
     *                          when performing match
     */
    public MatchWindowTask(Logger logger, ServerConnector serverConnector,
                           RunningSession runningSession, int retryTimeout,
                           AppOutputProvider appOutputProvider) {
        ArgumentGuard.notNull(serverConnector, "serverConnector");
        ArgumentGuard.notNull(runningSession, "runningSession");
        ArgumentGuard.greaterThanOrEqualToZero(retryTimeout, "retryTimeout");
        ArgumentGuard.notNull(appOutputProvider, "appOutputProvider");

        this.logger = logger;
        this.serverConnector = serverConnector;
        this.runningSession = runningSession;
        this.defaultRetryTimeout = retryTimeout;
        this.appOutputProvider = appOutputProvider;
    }

    /**
     * Creates the match data and calls the server connector matchWindow method.
     *
     * @param userInputs     The user inputs related to the current appOutput.
     * @param appOutput      The application output to be matched.
     * @param tag            Optional tag to be associated with the match (can
     *                       be {@code null}).
     * @param ignoreMismatch Whether to instruct the server to ignore the
     *                       match attempt in case of a mismatch.
     * @return The match result.
     */
    protected MatchResult performMatch(Trigger[] userInputs,
                                       AppOutputWithScreenshot appOutput,
                                       String tag, boolean ignoreMismatch) {

        // Prepare match data.
        MatchWindowData data = new MatchWindowData(userInputs, appOutput
                .getAppOutput(), tag, ignoreMismatch,
                new MatchWindowData.Options(tag, userInputs,ignoreMismatch,
                        false, false, false));

        // Perform match.
        return serverConnector.matchWindow(runningSession, data);
    }

    /**
     * Repeatedly obtains an application snapshot and matches it with the next
     * expected output, until a match is found or the timeout expires.
     *
     * @param userInputs                        User input preceding this match.
     * @param lastScreenshot                    The last screenshot matched or
     *                                          not ignored.
     * @param regionProvider                    Window region to capture.
     * @param tag                               Optional tag to be associated with
     *                                          the match (can be {@code null}).
     * @param shouldMatchWindowRunOnceOnTimeout Force a single match attempt at the
     *                                          end of the match timeout.
     * @param ignoreMismatch                    Whether to instruct the server to
     *                                          ignore the match attempt in case
     *                                          of a mismatch.
     * @param retryTimeout                      The amount of time to retry
     *                                          matching in milliseconds or a
     *                                          negative value to use the default
     *                                          retry timeout.
     * @return Returns the results of the match
     */
    public MatchResult matchWindow(Trigger[] userInputs,
                                   EyesScreenshot lastScreenshot,
                                   RegionProvider regionProvider, String tag,
                                   boolean shouldMatchWindowRunOnceOnTimeout,
                                   boolean ignoreMismatch, int retryTimeout) {

        AppOutputWithScreenshot appOutput;
        MatchResult matchResult;

        if (retryTimeout < 0) {
            retryTimeout = defaultRetryTimeout;
        }

        logger.verbose(String.format("retryTimeout = %d", retryTimeout));

        long elapsedTimeStart = System.currentTimeMillis();

        // If the wait to load time is 0, or "run once" is true,
        // we perform a single check window.
        if (0 == retryTimeout || shouldMatchWindowRunOnceOnTimeout) {

            if (shouldMatchWindowRunOnceOnTimeout) {
                GeneralUtils.sleep(retryTimeout);
            }

            // Getting the screenshot.
            appOutput = appOutputProvider.getAppOutput(regionProvider,
                    lastScreenshot);

            matchResult = performMatch(userInputs, appOutput, tag,
                    ignoreMismatch);

        } else {
            /*
            * We call a "tolerant" match window until we find a match
            * or we timeout, in which case we call a single "strict"
            * match.
            */

            // We intentionally start the timer after(!) taking the screenshot,
            // so less time is "wasted" on the transfer of the image.
            appOutput = appOutputProvider.getAppOutput(regionProvider,
                    lastScreenshot);

            // Start the retry timer.
            long start = System.currentTimeMillis();

            matchResult = performMatch(userInputs, appOutput, tag,
                    true);

            long retry = System.currentTimeMillis() - start;

            // The match retry loop.
            while ((retry < retryTimeout) && !matchResult.getAsExpected()) {

                // Wait before trying again.
                GeneralUtils.sleep(MATCH_INTERVAL);

                appOutput = appOutputProvider.getAppOutput(regionProvider,
                        lastScreenshot);

                // Notice the ignoreMismatch here is true
                matchResult = performMatch(userInputs, appOutput, tag, true);

                retry = System.currentTimeMillis() - start;
            }

            // if we're here because we haven't found a match yet, try once more
            if (!matchResult.getAsExpected()) {

                appOutput = appOutputProvider.getAppOutput(regionProvider,
                        lastScreenshot);

                matchResult = performMatch(userInputs, appOutput, tag,
                        ignoreMismatch);
            }
        }
        double elapsedTime = (System.currentTimeMillis() - elapsedTimeStart) /
                1000;
        logger.verbose(String.format("Completed in  %.2f seconds",
                elapsedTime));
        matchResult.setScreenshot(appOutput.getScreenshot());
        return matchResult;
    }
}
