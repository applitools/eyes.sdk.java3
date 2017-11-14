package com.applitools.eyes.diagnostics;

import com.applitools.eyes.*;
import com.applitools.utils.BlockingInstanceContainer;

/**
 * A class which performs a checkWindow call (so it can be run on a separate
 * thread).
 */
public class ResponseTimeMatchFinderTask implements Runnable {

    private final long matchInterval;
    private final ServerConnector serverConnector;
    private final RunningSession runningSession;
    private final BlockingInstanceContainer<MatchWindowDataWithScreenshot>
            matchDataContainer;
    private MatchWindowDataWithScreenshot lastNonMatch;
    private MatchWindowDataWithScreenshot theMatch;
    private EyesScreenshot lastScreenshot;

    /**
     *
     * @param matchDataContainer The container to use for passing
     *                           the match data to this instance.
     * @param matchInterval The time to wait between match attempts.
     *                      (Milliseconds)
     * @param serverConnector The server connector instance.
     * @param runningSession The current running session in which we should
     *                       perform the match.
     */
    public ResponseTimeMatchFinderTask(
            BlockingInstanceContainer<MatchWindowDataWithScreenshot>
                    matchDataContainer,
            long matchInterval, ServerConnector serverConnector,
            RunningSession runningSession) {

        this.matchDataContainer = matchDataContainer;
        this.matchInterval = matchInterval;
        this.serverConnector = serverConnector;
        this.runningSession = runningSession;
        theMatch = null;
        lastNonMatch = null;
        lastScreenshot = null;
    }

    public void run() {
        long lastMatchAttemptTime, currentTime, timeToSleep;
        MatchResult matchResult;
        // First attempt
        MatchWindowDataWithScreenshot currentMatchData =
                matchDataContainer.take();
        if (currentMatchData == null) {
            // No more data to handle
            return;
        }
        lastScreenshot = currentMatchData.getScreenshot();
        lastMatchAttemptTime = System.currentTimeMillis();
        matchResult = serverConnector.matchWindow(runningSession,
                currentMatchData.getMatchWindowData());

        // We explicitly test for "interrupted" because "matchWindow"
        // might take a long time toc complete.
        while (!matchResult.getAsExpected() && !Thread.interrupted()) {

            lastNonMatch = currentMatchData;

            currentMatchData = matchDataContainer.take();

            if (currentMatchData == null) {
                // No more data to handle
                return;
            }

            currentTime = System.currentTimeMillis();

            timeToSleep = matchInterval - (currentTime - lastMatchAttemptTime);
            if (timeToSleep > 0) {
                try {
                    Thread.sleep(timeToSleep);
                } catch (InterruptedException e) {
                    // If the thread was interrupted, we need to finish.
                    return;
                }
            }

            lastScreenshot = currentMatchData.getScreenshot();
            lastMatchAttemptTime = System.currentTimeMillis();
            matchResult = serverConnector.matchWindow(runningSession,
                    currentMatchData.getMatchWindowData());
        }

        // If we left the loop because we found a match.
        if (matchResult.getAsExpected()) {
            theMatch = currentMatchData;
        } else {
            lastNonMatch = currentMatchData;
        }
    }

    /**
     *
     * @return The match data for which there was a successful match, or
     * {@code null} if no match was found.
     */
    public MatchWindowDataWithScreenshot getTheMatch() {
            return theMatch;
    }

    /**
     *
     * @return The last match data for which the match failed,
     * or {@code null} if there was no failed match.
     */
    public MatchWindowDataWithScreenshot getLastNonMatch() {
            return lastNonMatch;
    }

    public EyesScreenshot getLastScreenshot() {
        return lastScreenshot;
    }
}
