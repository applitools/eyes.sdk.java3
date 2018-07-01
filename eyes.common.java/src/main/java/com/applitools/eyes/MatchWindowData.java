/*
 * Applitools SDK for Selenium integration.
 */
package com.applitools.eyes;

import com.applitools.utils.ArgumentGuard;

/**
 * Encapsulates the data to be sent to the agent on a "matchWindow" command.
 */
public class MatchWindowData {

    /**
     * Encapsulates the "Options" section of the MatchExpectedOutput body data.
     */
    public static class Options {
        private final Trigger[] userInputs;
        private final String name;
        private final boolean ignoreMismatch;
        private final boolean ignoreMatch;
        private final boolean forceMismatch;
        private final boolean forceMatch;
        private final ImageMatchSettings imageMatchSettings;


        /**
         * @param name           The tag of the window to be matched.
         * @param userInputs     A list of triggers between the previous matchWindow
         *                       call and the current matchWindow call. Can be array
         *                       of size 0, but MUST NOT be null.
         * @param ignoreMismatch Tells the server whether or not to store
         *                       a mismatch for the current window as window in
         *                       the session.
         * @param ignoreMatch    Tells the server whether or not to store
         *                       a match for the current window as window in
         *                       the session.
         * @param forceMismatch  Forces the server to skip the comparison
         *                       process and mark the current window
         *                       as a mismatch.
         * @param forceMatch     Forces the server to skip the comparison
         *                       process and mark the current window
         *                       as a match.
         */
        public Options(String name, Trigger[] userInputs,
                       boolean ignoreMismatch, boolean ignoreMatch,
                       boolean forceMismatch, boolean forceMatch,
                       ImageMatchSettings imageMatchSettings) {
            ArgumentGuard.notNull(userInputs, "userInputs");

            this.name = name;
            this.userInputs = userInputs;
            this.ignoreMismatch = ignoreMismatch;
            this.ignoreMatch = ignoreMatch;
            this.forceMismatch = forceMismatch;
            this.forceMatch = forceMatch;
            this.imageMatchSettings = imageMatchSettings;
        }

        @SuppressWarnings("unused")
        public String getName() {
            return name;
        }

        @SuppressWarnings("unused")
        public Trigger[] getUserInputs() {
            return userInputs;
        }

        @SuppressWarnings("unused")
        public boolean getIgnoreMismatch() {
            return ignoreMismatch;
        }

        @SuppressWarnings("unused")
        public boolean getIgnoreMatch() {
            return ignoreMatch;
        }

        @SuppressWarnings("unused")
        public boolean getForceMismatch() {
            return forceMismatch;
        }

        @SuppressWarnings("unused")
        public boolean getForceMatch() {
            return forceMatch;
        }

        @SuppressWarnings("unused")
        public ImageMatchSettings getImageMatchSettings() { return imageMatchSettings; }
    }


    // TODO Remove redundancy: userInputs and ignoreMismatch should only be inside Options. (requires server version update).
    private AppOutput appOutput;
    private String tag;
    private Options options;

    /**
     * @param appOutput      The appOutput for the current matchWindow call.
     * @param tag            The tag of the window to be matched.
     * @param options        The options to match by.
     */
    public MatchWindowData(AppOutput appOutput, String tag, Options options) {
        this.appOutput = appOutput;
        this.tag = tag;
        this.options = options;
    }

    public AppOutput getAppOutput() {
        return appOutput;
    }

    @SuppressWarnings("unused")
    public String getTag() {
        return tag;
    }

    @SuppressWarnings("unused")
    public Options getOptions() {
        return options;
    }
}
