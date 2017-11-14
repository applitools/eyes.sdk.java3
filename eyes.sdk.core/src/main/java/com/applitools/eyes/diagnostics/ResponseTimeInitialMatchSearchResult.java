package com.applitools.eyes.diagnostics;

import com.applitools.eyes.MatchWindowDataWithScreenshot;

/**
 * A container for the results of the initial match search of a response time
 * test.
 */
public class ResponseTimeInitialMatchSearchResult {
    private final MatchWindowDataWithScreenshot theMatch;
    private final MatchWindowDataWithScreenshot lastNonMatch;

    /**
     *
     * @param theMatch The match data for which there was a match in the
     *                 initial match search phase, or {@code null} if there
     *                 was no match.
     * @param lastNonMatch The last match data for which there was no match,
     *                     or {@code null} if there was no failed match.
     */
    public ResponseTimeInitialMatchSearchResult(
            MatchWindowDataWithScreenshot theMatch,
            MatchWindowDataWithScreenshot lastNonMatch) {
        this.theMatch = theMatch;
        this.lastNonMatch = lastNonMatch;
    }

    /**
     *
     * @return The match data for which there was a match in the initial
     * match search phase, or {@code null} if there was no match.
     */
    public MatchWindowDataWithScreenshot getTheMatch() {
        return theMatch;
    }

    /**
     *
     * @return The last match data for which there was no match, or {@code
     * null} if there was no failed match.
     */
    public MatchWindowDataWithScreenshot getLastNonMatch() {
        return lastNonMatch;
    }
}
