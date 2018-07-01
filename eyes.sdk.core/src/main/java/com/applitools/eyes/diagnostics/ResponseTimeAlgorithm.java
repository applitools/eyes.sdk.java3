package com.applitools.eyes.diagnostics;

import com.applitools.eyes.*;
import com.applitools.eyes.capture.AppOutputProvider;
import com.applitools.eyes.capture.AppOutputWithScreenshot;
import com.applitools.eyes.positioning.RegionProvider;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.BlockingInstanceContainer;
import com.applitools.utils.GeneralUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * A container for the algorithm for window timing tests.
 */
public class ResponseTimeAlgorithm {
    private static final int FAST_INTERVAL_SCREENSHOTS_COUNT = 10;
    private static final int STANDARD_INTERVAL_SCREENSHOTS_COUNT = 20;


    /**
     * Creates a string describing the elapsed time relative to a deadline.
     *
     * @param deadline The deadline to be used as reference. (Seconds)
     * @param elapsedTime The elapsed time to describe. (Second)
     * @return A tag describing the elapsed time, with reference to the
     * deadline.
     */
    private static String createTagForDeadline(int deadline, long elapsedTime) {
        String tag;
        if (elapsedTime < deadline) {
            tag = String.format("After %d seconds (%d seconds to deadline)",
                    elapsedTime, deadline - elapsedTime);
        } else if (elapsedTime > deadline){
            tag = String.format(
                    "After %d seconds (%d seconds after deadline)",
                    elapsedTime, elapsedTime - deadline );
        } else {
            tag = String.format("After %d seconds (deadline)",
                    elapsedTime);
        }

        return tag;
    }

    /**
     * Clones the given {@link MatchWindowDataWithScreenshot} instance, while
     * setting the primary to the required value.
     * @param currentMwdws The instance to clone.
     * @param updatePrimary The primary value to set.
     * @return A new instance with the updated primary value.
     */
    private static MatchWindowDataWithScreenshot cloneTimedMWDWSWithPrimary(
            MatchWindowDataWithScreenshot currentMwdws, boolean updatePrimary) {

        MatchWindowData currentMwd =
                currentMwdws.getMatchWindowData();
        TimedAppOutput currentAppOutput = (TimedAppOutput)
                currentMwdws.getMatchWindowData().getAppOutput();

        TimedAppOutput updatedAppOutput = new TimedAppOutput(
                currentAppOutput.getTitle(),
                currentAppOutput.getScreenshot64(),
                currentAppOutput.getElapsed(),
                updatePrimary);
        MatchWindowData updatedMwd = new MatchWindowData(
                updatedAppOutput,
                currentMwd.getTag(),
                currentMwd.getOptions());

        return new MatchWindowDataWithScreenshot(updatedMwd,
                currentMwdws.getScreenshot());
    }

    /**
     * Runs a new progression session (i.e., no existing baseline).
     *
     * @param logger The logger to use.
     * @param serverConnector The server connector to use.
     * @param runningSession The current session.
     * @param appOutputProvider A provider which enables us to capture a screenshot.
     * @param regionProvider Which part of the screenshot we wish to compare.
     * @param startTime The start time for the test. should be the result of
     *                  a call to {@link System#currentTimeMillis()}.
     * @param deadline The expected time by which the application should have been loaded. (Seconds)
     */
    public static void runNewProgressionSession(Logger logger,
                                                ServerConnector serverConnector, RunningSession runningSession,
                                                AppOutputProvider appOutputProvider, RegionProvider regionProvider,
                                                long startTime, int deadline) {
        logger.verbose("New progression session detected.");
        logger.verbose("Waiting for deadline to create the baseline...");
        try {
            Thread.sleep(deadline * 1000);
            logger.verbose("Finished waiting for deadline.");
        } catch (InterruptedException e) {
            logger.verbose(
                    "Got interrupted while waiting for deadline to pass!");
        }

        logger.verbose("Taking screenshot...");
        AppOutputWithScreenshot appOutputWithScreenshot =
                appOutputProvider.getAppOutput(regionProvider.getRegion(), null, null);
        logger.verbose("Screenshot taken.");
        long elapsedTime =
                GeneralUtils.getFullSecondsElapsedTimeMillis(startTime,
                        System.currentTimeMillis());

        logger.verbose("Saving screenshot...");
        AppOutput appOutput = appOutputWithScreenshot.getAppOutput();
        TimedAppOutput timedAppOutput = new TimedAppOutput(
                appOutput.getTitle(),
                appOutput.getScreenshot64(),
                elapsedTime,
                true
        );
        String tag = appOutput.getTitle();
        Trigger[] noUserInputs = new Trigger[0];
        MatchWindowData mwd = new MatchWindowData(
                timedAppOutput,
                tag,
                new MatchWindowData.Options(tag, noUserInputs, false,
                        false, false, false, null)
        );
        serverConnector.matchWindow(runningSession, mwd);
        logger.verbose("Finished saving.");
        logger.verbose("testResponseTimeBase Done!");
    }

    /**
     * Performs an initial search for a match. Notice that this means this
     * function does NOT try to find the EARLIEST match, but to find ANY
     * match. It does so by taking screenshots at some interval, and
     * performing the match on a different thread. It is done that way
     * because the matching process is much slower than screenshot taking.
     *
     * @param logger The logger to use.
     * @param serverConnector The server connector to use for the matching.
     * @param runningSession The current session in which we perform the matching.
     * @param appOutputProvider A provider which enables us to capture a screenshot.
     * @param regionProvider Which part of the screenshot we wish to compare.
     * @param startTime The start time for the test. should be the result of a call to {@code System.currentTimeMillis()}.
     * @param deadline The expected time by which the application should have been loaded. (Seconds)
     * @param timeout The maximum time waiting for the application to load. (Seconds)
     * @param matchInterval The interval between performing matches.
     * @param collectedData A container in which we will store all the captured screenshots.
     * @return The result of the initial search.
     */
    private static ResponseTimeInitialMatchSearchResult
    responseTimeInitialMatchSearch (Logger logger, ServerConnector
            serverConnector, RunningSession runningSession, AppOutputProvider
            appOutputProvider, RegionProvider regionProvider, long startTime,
            int deadline, int timeout, long matchInterval,
            List<MatchWindowDataWithScreenshot> collectedData) {

        logger.verbose("responseTimeInitialMatchSearch()");

        final long MIN_SCREENSHOT_INTERVAL = 1000; // Milliseconds
        final long MATCH_THREAD_CLOSE_TIMEOUT = 10000; // Milliseconds

        // The point from which screenshots should be taken at minimum interval.
        int fastIntervalTimeout = timeout - FAST_INTERVAL_SCREENSHOTS_COUNT;
        if (fastIntervalTimeout < 0) {
            fastIntervalTimeout = 0;
        }

        logger.verbose("fast interval timeout: " + fastIntervalTimeout);

        // Up to the fastIntervalTimeout, we can use slower intervals, based on
        // the number of screenshots we want.
        long screenshotInterval = MIN_SCREENSHOT_INTERVAL;
        if (fastIntervalTimeout > 0) {
            screenshotInterval = (long) Math.ceil(
                    ((float) fastIntervalTimeout) /
                            STANDARD_INTERVAL_SCREENSHOTS_COUNT) * 1000;
        }

        logger.verbose("Screenshot interval (Milliseconds): " +
                screenshotInterval);

        int maxScreenshotsCount = STANDARD_INTERVAL_SCREENSHOTS_COUNT +
                FAST_INTERVAL_SCREENSHOTS_COUNT;

        // The shared object to pass the capture data between threads.
        BlockingInstanceContainer<MatchWindowDataWithScreenshot>
                matchDataContainer =
                new BlockingInstanceContainer<MatchWindowDataWithScreenshot>();

        logger.verbose("Starting matcher thread.");
        ResponseTimeMatchFinderTask matcherTask =
                new ResponseTimeMatchFinderTask(matchDataContainer,
                        matchInterval, serverConnector, runningSession);
        Thread matcherTaskThread = new Thread(matcherTask);
        matcherTaskThread.start();

        // The actual screenshot taking logic.
        long elapsedTime, currentTime, lastScreenshotRequestTime, timeToSleep;
        currentTime = System.currentTimeMillis();
        elapsedTime = GeneralUtils.getFullSecondsElapsedTimeMillis(startTime,
                currentTime);
        boolean switchedToFastInterval, markedPrimary, isPrimary;
        isPrimary = markedPrimary = switchedToFastInterval = false;

        MatchWindowData currentWindowData;
        MatchWindowDataWithScreenshot currentWindowDataWithScreenshot,
                lastNonMatch, theMatch, originalPrimary, updatedPrimary;

        updatedPrimary = originalPrimary = theMatch = null;
        int screenshotsCount = 0;
        long deadlineMs = deadline * 1000;
        long timeoutMs = timeout * 1000;
        long fastIntervalTimeoutMs = fastIntervalTimeout * 1000;
        while (theMatch == null && screenshotsCount < maxScreenshotsCount &&
                elapsedTime < timeoutMs) {

            // If We reached the part where screenshots needed to be taken at
            // minimum interval.
            if (!switchedToFastInterval &&
                    (elapsedTime >= fastIntervalTimeoutMs)) {
                screenshotInterval = MIN_SCREENSHOT_INTERVAL;
                switchedToFastInterval = true;
                logger.verbose("Switched to fast interval.");
            }

            logger.verbose("Taking screenshot...");
            lastScreenshotRequestTime = System.currentTimeMillis();
            // Get the screenshot and build the match data.
            AppOutputWithScreenshot appOutputWithScreenshot =
                    appOutputProvider.getAppOutput(regionProvider.getRegion(),
                            matcherTask.getLastScreenshot(), null);
            elapsedTime =
                    GeneralUtils.getFullSecondsElapsedTimeMillis(startTime,
                            System.currentTimeMillis());
            logger.verbose("Screenshot taken!");
            if (!markedPrimary && elapsedTime > deadlineMs) {
                if (collectedData.size() != 0) {
                    logger.verbose("Previous screenshot is primary.");
                    originalPrimary = collectedData.get(
                            collectedData.size() - 1);
                    updatedPrimary = cloneTimedMWDWSWithPrimary(originalPrimary,
                            true);
                    collectedData.set(collectedData.size() - 1, updatedPrimary);

                } else {
                    // No collected data yet, so we'll mark the screenshot we
                    // just took as primary.
                    isPrimary = true;
                    logger.verbose("current screenshot is primary.");
                }
                markedPrimary = true;
            }
            AppOutput appOutput = appOutputWithScreenshot.getAppOutput();
            TimedAppOutput timedAppOutput = new TimedAppOutput(
                    appOutput.getTitle(),
                    appOutput.getScreenshot64(),
                    elapsedTime,
                    isPrimary
            );
            // So not all windows from now on will be primary.
            isPrimary = false;
            // elapsed time is always full seconds, so we can use "floor".
            int elapsedSeconds = (int) Math.floor(elapsedTime / 1000.0);
            String tag = createTagForDeadline(deadline, elapsedSeconds);
            Trigger[] noUserInputs = new Trigger[0];
            currentWindowData = new MatchWindowData(
                    timedAppOutput,
                    tag,
                    new MatchWindowData.Options(tag, noUserInputs, true,
                            true, false, false, null)
            );
            currentWindowDataWithScreenshot =
                    new MatchWindowDataWithScreenshot(currentWindowData,
                            appOutputWithScreenshot.getScreenshot());

            // Add the screenshot to the collection and pass it to the
            // matcher thread.
            ++screenshotsCount;
            collectedData.add(collectedData.size(),
                    currentWindowDataWithScreenshot);
            matchDataContainer.put(currentWindowDataWithScreenshot);

            // Check if there was a match
            theMatch = matcherTask.getTheMatch();

            // We only need to wait for the next interval if there was no
            // match yet.
            if (theMatch == null) {
                timeToSleep = screenshotInterval -
                        (System.currentTimeMillis() -
                                lastScreenshotRequestTime);
                logger.verbose("No match yet.");
                if (timeToSleep > 0) {
                    logger.verbose("Time to sleep: " + timeToSleep);
                    try {
                        Thread.sleep(timeToSleep);
                    } catch (InterruptedException e) {
                        // We basically shouldn't be here, but if we got here,
                        // stop the search process.
                        break;
                    }
                }
            }

            currentTime = System.currentTimeMillis();
            elapsedTime =
                    GeneralUtils.getFullSecondsElapsedTimeMillis(startTime,
                            currentTime);
        }

        logger.verbose("Finished collecting data.");
        matchDataContainer.close();

        // If we left the loop because we reached maxScreenshot/timeout, give
        // the matcher thread time finish.
        if (theMatch == null) {
            logger.verbose("No match found yet. Waiting for matcher thread..");
            try {
                matcherTaskThread.join(MATCH_THREAD_CLOSE_TIMEOUT);
                logger.verbose("Finished waiting.");
                if (matcherTaskThread.isAlive()) {
                    logger.verbose(
                            "Matcher thread still running. Interrupting it..");
                    matcherTaskThread.interrupt();
                    logger.verbose("Done!");
                }
            } catch (InterruptedException e) {
                logger.verbose(
                        "Got interrupt while Waiting for matcher thread.");
            }
            theMatch = matcherTask.getTheMatch();
        }

        lastNonMatch = matcherTask.getLastNonMatch();

        // If the match/lastNonMatch was update to be primary, we should return
        // the updated object.
        if (theMatch == originalPrimary) {
            theMatch = updatedPrimary;
        } else if (lastNonMatch == originalPrimary) {
            lastNonMatch = updatedPrimary;
        }

        logger.verbose("Is match found? " + (theMatch != null));

        return new ResponseTimeInitialMatchSearchResult(theMatch, lastNonMatch);
    }

    /**
     * Recursively binary search for the earliest match.
     *
     * @param logger The logger to use.
     * @param serverConnector The server connector to use for matching.
     * @param runningSession The session for which we perform the match.
     * @param dataToSearch The data inside which we search for a match.
     * @param fromIndex The index in the search area from which to start
     *                  searching. (inclusive)
     * @param toIndex The index in the search area up to which to perform the
     *                search. (inclusive).
     * @param earliestMatchIndex The currently known earliest match index.
     * @return The index of earliest match found.
     */
    private static int binarySearchEarliestMatch(Logger logger,
            ServerConnector serverConnector, RunningSession runningSession,
            List<MatchWindowDataWithScreenshot> dataToSearch, int fromIndex,
            int toIndex, int earliestMatchIndex) {

        logger.verbose(String.format("Indices: From %d, to %s ", fromIndex,
                toIndex));

        // Just in case.
        if (fromIndex > toIndex) {
            throw new EyesException(
                    String.format("Invalid indices: From %d, to %s ",
                            fromIndex, toIndex));
        }

        // We need to attempt a match anyway (stop condition comes later).
        int currentMatchIndex = (int) Math.ceil((fromIndex + toIndex) / 2.0);
        logger.verbose("Trying to match index: " + currentMatchIndex);
        MatchResult matchResult = serverConnector.matchWindow(runningSession,
                dataToSearch.get(currentMatchIndex).getMatchWindowData());

        if (matchResult.getAsExpected()) {
            logger.verbose("Match!");
            earliestMatchIndex = currentMatchIndex;
        } else {
            logger.verbose("No match!");
        }

        // Stop condition. Notice:
        // 1. "toIndex == currentMatchIndex" only happens when we have 2
        // elements left, and in this case if the match failed, then no point
        // in going further. If the match was successful however there is a
        // chance that "fromIndex" will also be a match.
        // 2. Because when we  calculate currentMatchIndex we round UP,
        // "toIndex" will NEVER be equal to "matchCurrentIndex" unless
        // toIndex==fromIndex, so no need to check for that explicitly.
        if (fromIndex == toIndex ||
            (toIndex == currentMatchIndex && !matchResult.getAsExpected())) {

            logger.verbose(String.format(
                    "Finished matching! Current index: %d, earliest match: %d",
                    currentMatchIndex, earliestMatchIndex));
            return earliestMatchIndex;
        }

        if (matchResult.getAsExpected()) {
            toIndex = currentMatchIndex - 1;
        } else {
            fromIndex = currentMatchIndex + 1;
        }

        return binarySearchEarliestMatch(logger, serverConnector,
                runningSession, dataToSearch, fromIndex, toIndex,
                earliestMatchIndex);
    }

    /**
     * Find the EARLIEST match within the collected data.
     *
     * @param logger The logger to use.
     * @param serverConnector The server connector to use for the matching.
     * @param runningSession The current session in which we perform the
     *                       matching.
     * @param collectedData The list of captured screenshots in which to
     *                      search for a match.
     * @param theMatch The match data of the currently known successful match,
     *                 or  {@code null} if no match is known.
     * @param lastNonMatch The match data of the last known failed match, or
     *                  {@code null} if no such failed match is known.
     * @return The index of earliest match found.
     */
    private static int findEarliestMatchIndex(Logger logger,
            ServerConnector serverConnector, RunningSession runningSession,
            List<MatchWindowDataWithScreenshot> collectedData,
            MatchWindowDataWithScreenshot theMatch,
            MatchWindowDataWithScreenshot lastNonMatch) {

        if (theMatch == null) {
            return -1;
        }

        logger.verbose("findEarliestMatchIndex()");

        // The match we know about
        int theMatchIndex = collectedData.indexOf(theMatch);

        if (theMatchIndex == 0) {
            return 0;
        }

        // Notice that the first screenshot we test could have been a match,
        // so we might not have a "lastNonMatch" object. (due to threading
        // issues it might not be the first screenshot collected, mind you).
        int lastNonMatchIndex = lastNonMatch != null ?
                collectedData.indexOf(lastNonMatch) : 0;

        logger.verbose(String.format(
                "Performing binary search for earliest match: From %d to %d",
                lastNonMatchIndex, theMatchIndex - 1));

        theMatchIndex = binarySearchEarliestMatch(logger,
                serverConnector, runningSession, collectedData,
                lastNonMatchIndex, theMatchIndex - 1, theMatchIndex);

        logger.verbose("The earliest match index: " + theMatchIndex);
        return theMatchIndex;
    }

    /**
     * Updates the primary flag for the earliest match, if it happened before
     * the deadline.
     * @param collectedData The list of captured screenshots and their
     *                      meta data.
     * @param theMatchIndex The index of the earliest match found.
     * @param deadline The deadline given for the progression session.
     * @return The match object which is now marked as primary, or {@code
     * null} if no match exists.
     */
    private static MatchWindowDataWithScreenshot updatePrimary(Logger logger,
            List<MatchWindowDataWithScreenshot> collectedData,
            int theMatchIndex, int deadline) {

        logger.verbose("updatedPrimary()");

        if (theMatchIndex < 0) {
            logger.verbose("No match exists. No update is necessary.");
            return null;
        }

        MatchWindowDataWithScreenshot theMatch = collectedData.get(
                theMatchIndex);
        TimedAppOutput tao =
                (TimedAppOutput) theMatch.getMatchWindowData().getAppOutput();
        long matchElapsed = tao.getElapsed();
        long deadlineMs = deadline * 1000;

        // If the match is within the deadline, we mark it as primary
        // instead of the current primary.
        if (matchElapsed <= deadlineMs) {
            logger.verbose("Match is within the deadline.");
            logger.verbose("Searching for primary...");
            MatchWindowDataWithScreenshot currentMwdws;
            // Notice that if a match is within the deadline, the
            // current primary must be after the match (or the match itself
            // would've been the primary).
            for (int i = theMatchIndex + 1; i < collectedData.size(); ++i) {

                currentMwdws = collectedData.get(i);
                TimedAppOutput currentAppOutput = (TimedAppOutput)
                        currentMwdws.getMatchWindowData().getAppOutput();

                // If this is the current primary, un-mark it as such.
                if (currentAppOutput.getIsPrimary()) {
                    logger.verbose("Found primary at index " + i);
                    MatchWindowDataWithScreenshot updatedMwdws =
                            ResponseTimeAlgorithm.cloneTimedMWDWSWithPrimary(
                                    currentMwdws, false);

                    logger.verbose("Un-marking original primary.");
                    collectedData.set(i, updatedMwdws);

                    // No point in continuing the loop, since there will
                    // be no more primary.
                    break;
                }
            }

            logger.verbose(String.format(
                    "Marking the earliest match as primary (at index %d).",
                    theMatchIndex));
            theMatch = ResponseTimeAlgorithm.cloneTimedMWDWSWithPrimary(
                    theMatch, true);
            collectedData.set(theMatchIndex, theMatch);
        }

        return theMatch;
    }

    /**
     * Sets the images in a progression session.
     *
     * @param logger The logger to use.
     * @param serverConnector The server connector to use.
     * @param runningSession The current session.
     * @param collectedData The list of progression images.
     * @param theMatchIndex The index of the match within {@code collectedData},
     *                      or {@code -1} if no match was found.
     */
    private static void setProgressionImages(Logger logger, ServerConnector
        serverConnector, RunningSession runningSession,
        List<MatchWindowDataWithScreenshot> collectedData, int theMatchIndex) {

        logger.verbose("setProgressionImages()");
        logger.verbose("The match index: " + theMatchIndex);
        int lastImageIndex = theMatchIndex > -1 ?
                theMatchIndex : (collectedData.size() - 1);
        logger.verbose("Last image index: " + lastImageIndex);

        logger.verbose("Setting images...");
        MatchWindowDataWithScreenshot currentMwdws;
        MatchWindowData mwdToSend, currentMwd;
        MatchWindowData.Options currentOptions;
        for (int i = 0; i < lastImageIndex; ++i) {
            currentMwdws = collectedData.get(i);
            currentMwd = currentMwdws.getMatchWindowData();
            currentOptions = currentMwd.getOptions();
            long currentElapsed = ((TimedAppOutput) currentMwd.getAppOutput())
                    .getElapsed();
            long nextElapsed = ((TimedAppOutput)
                    collectedData.get(i+1).getMatchWindowData().getAppOutput())
                    .getElapsed();
            if (currentElapsed == nextElapsed) {
                logger.verbose(String.format(
                    "Skipping image at index %d (same elapsed as next image)..."
                        , i));
                if (((TimedAppOutput)
                        currentMwd.getAppOutput()).getIsPrimary()){
                    logger.verbose("Skipped image is primary..");
                    logger.verbose("Moving primary to the next image..");
                    MatchWindowDataWithScreenshot nextMatchData =
                            collectedData.get(i+1);
                    nextMatchData = cloneTimedMWDWSWithPrimary(nextMatchData,
                            true);
                    collectedData.set(i+1, nextMatchData);
                    logger.verbose("Done moving primary.");
                }

                continue;
            }
            mwdToSend = new MatchWindowData(
                    currentMwd.getAppOutput(),
                    currentMwd.getTag(),
                    new MatchWindowData.Options(
                            currentOptions.getName(),
                            currentOptions.getUserInputs(),
                            false,
                            false,
                            true,
                            false,
                            null
                    )
            );
            serverConnector.matchWindow(runningSession, mwdToSend);
        }


        // Last screenshot should force match if there was a match.
        boolean forceMatch = (theMatchIndex > -1);
        logger.verbose("Setting last image as a match? " + forceMatch);
        currentMwdws = collectedData.get(lastImageIndex);
        currentMwd = currentMwdws.getMatchWindowData();
        currentOptions = currentMwd.getOptions();
        mwdToSend = new MatchWindowData(
                currentMwd.getAppOutput(),
                currentMwd.getTag(),
                new MatchWindowData.Options(
                        currentOptions.getName(),
                        currentOptions.getUserInputs(),
                        false,
                        false,
                        !forceMatch,
                        forceMatch,
                        null
                )
        );
        serverConnector.matchWindow(runningSession, mwdToSend);
        logger.verbose("Done setting images!");
    }

    /**
     * Runs a progression session, for an existing baseline.
     *
     * @param logger The logger to use.
     * @param serverConnector The server connector to use for the matching.
     * @param runningSession The current session in which we perform the
     *                       matching.
     * @param appOutputProvider A provider which enables us to capture a
     *                          screenshot.
     * @param regionProvider Which part of the screenshot we wish to compare.
     * @param startTime The start time for the test. should be the result of
     *                  a call to {@code System.currentTimeMillis()}.
     * @param deadline The expected time by which the application
     *                        should have been loaded. (Seconds)
     * @param timeout The maximum time waiting for the application to load.
     *                   (Seconds)
     * @param matchInterval The interval between performing matches.
     * @return The earliest match found, or {@code null} if no match is found.
     */
    public static MatchWindowDataWithScreenshot
    runProgressionSessionForExistingBaseline(Logger logger, ServerConnector
            serverConnector, RunningSession runningSession, AppOutputProvider
             appOutputProvider, RegionProvider regionProvider, long
            startTime, int deadline, int timeout, long matchInterval) {

        ArgumentGuard.notNull(serverConnector, "serverConnector");
        ArgumentGuard.notNull(runningSession, "runningSession");
        ArgumentGuard.notNull(appOutputProvider, "appOutputProvider");
        ArgumentGuard.notNull(regionProvider, "regionProvider");
        ArgumentGuard.greaterThanOrEqualToZero(startTime, "startTime");
        ArgumentGuard.greaterThanOrEqualToZero(deadline, "deadline");
        ArgumentGuard.greaterThanOrEqualToZero(timeout, "timeout");
        ArgumentGuard.greaterThanOrEqualToZero(matchInterval, "matchInterval");

        logger.verbose("runProgressionSessionForExistingBaseline()");

        List<MatchWindowDataWithScreenshot> collectedData =
                new LinkedList<MatchWindowDataWithScreenshot>();

        // Run initial progression search for a match.
        ResponseTimeInitialMatchSearchResult initialSearchResult =
                ResponseTimeAlgorithm.responseTimeInitialMatchSearch(logger,
                    serverConnector, runningSession, appOutputProvider,
                    regionProvider, startTime, deadline, timeout, matchInterval,
                    collectedData);
        MatchWindowDataWithScreenshot theMatch, lastNonMatch;
        theMatch = initialSearchResult.getTheMatch();
        lastNonMatch = initialSearchResult.getLastNonMatch();

        logger.verbose("Finished initial search!");
        logger.verbose("No. of screenshots: " + collectedData.size());
        logger.verbose("Is match found? "  + (theMatch != null));

        if (theMatch != null) {
            logger.verbose("Initial known match index: "
                    + collectedData.indexOf(theMatch));
        }

        logger.verbose("Searching for the earliest match..");
        int theMatchIndex = ResponseTimeAlgorithm.findEarliestMatchIndex(logger,
                serverConnector, runningSession, collectedData, theMatch,
                lastNonMatch);
        logger.verbose("Done! Earliest match index: " + theMatchIndex);

        // If the match happened before the deadline, it should be marked as
        // primary.
        theMatch = updatePrimary(logger, collectedData, theMatchIndex,
                deadline);

        // Actually save the images into the test.
        setProgressionImages(logger, serverConnector, runningSession,
                collectedData, theMatchIndex);

        logger.verbose("Done!");

        return theMatch;
    }
}
