/*
 * Applitools SDK for Selenium integration.
 */
package com.applitools.eyes;

import com.applitools.utils.ArgumentGuard;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Encapsulates the model to be sent to the agent on a "matchWindow" command.
 */
public class MatchWindowData {

    /**
     * Encapsulates the "Options" section of the MatchExpectedOutput body model.
     */
    public static class Options {

        private final Trigger[] userInputs;
        private final String name;
        private final boolean replaceLast;
        private final boolean ignoreMismatch;
        private final boolean ignoreMatch;
        private final boolean forceMismatch;
        private final boolean forceMatch;
        private final ImageMatchSettings imageMatchSettings;
        private final String source;
        private final String variantId;
        private final String renderId;

        /**
         * @param name               The tag of the window to be matched.
         * @param userInputs         A list of triggers between the previous matchWindow
         *                           call and the current matchWindow call. Can be array
         *                           of size 0, but MUST NOT be null.
         * @param ignoreMismatch     Tells the server whether or not to store
 *                           a mismatch for the current window as window in
 *                           the session.
         * @param ignoreMatch        Tells the server whether or not to store
*                           a match for the current window as window in
*                           the session.
         * @param forceMismatch      Forces the server to skip the comparison
*                           process and mark the current window
*                           as a mismatch.
         * @param forceMatch         Forces the server to skip the comparison
*                           process and mark the current window
*                           as a match.
         * @param imageMatchSettings Settings specifying how the server should compare the image.
         */
        public Options(String name, Trigger[] userInputs, boolean replaceLast,
                       boolean ignoreMismatch, boolean ignoreMatch,
                       boolean forceMismatch, boolean forceMatch,
                       ImageMatchSettings imageMatchSettings,
                       String source, String variantId, String renderId) {
            ArgumentGuard.notNull(userInputs, "userInputs");

            this.name = name;
            this.userInputs = userInputs;
            this.replaceLast = replaceLast;
            this.ignoreMismatch = ignoreMismatch;
            this.ignoreMatch = ignoreMatch;
            this.forceMismatch = forceMismatch;
            this.forceMatch = forceMatch;
            this.imageMatchSettings = imageMatchSettings;
            this.source = source;
            this.variantId = variantId;
            this.renderId = renderId;
        }

        public String getName() {
            return name;
        }

        public Trigger[] getUserInputs() {
            return userInputs;
        }

        public boolean getReplaceLast() {
            return replaceLast;
        }

        public boolean getIgnoreMismatch() {
            return ignoreMismatch;
        }

        public boolean getIgnoreMatch() {
            return ignoreMatch;
        }

        public boolean getForceMismatch() {
            return forceMismatch;
        }

        public boolean getForceMatch() {
            return forceMatch;
        }

        public ImageMatchSettings getImageMatchSettings() {
            return imageMatchSettings;
        }

        public String getSource() {
            return source;
        }

        public String getRenderId() {
            return renderId;
        }

        public String getVariantId() {
            return variantId;
        }
    }

    // TODO Remove redundancy: userInputs and ignoreMismatch should only be inside Options. (requires server version update).

    private final Trigger[] userInputs;
    private final AppOutput appOutput;
    private final String tag;
    private final boolean ignoreMismatch;
    private final Options options;
    private final Object agentSetup;
    private String renderId;
    @JsonIgnore
    private final RunningSession runningSession;

    /**
     * @param userInputs     A list of triggers between the previous matchWindow
     *                       call and the current matchWindow call. Can be array
     *                       of size 0, but MUST NOT be null.
     * @param appOutput      The appOutput for the current matchWindow call.
     * @param tag            The tag of the window to be matched.
     * @param ignoreMismatch A flag indicating whether the server should ignore the image in case of a mismatch.
     * @param options        A set of match options for the server.
     * @param agentSetup     An object representing the configuration used to create the image.
     */
    public MatchWindowData(RunningSession runningSession, Trigger[] userInputs, AppOutput appOutput,
                           String tag, boolean ignoreMismatch,
                           Options options, Object agentSetup, String renderId) {

        ArgumentGuard.notNull(userInputs, "userInputs");

        this.userInputs = userInputs;
        this.appOutput = appOutput;
        this.tag = tag;
        this.ignoreMismatch = ignoreMismatch;
        this.options = options;
        this.agentSetup = agentSetup;
        this.renderId = renderId;
        this.runningSession = runningSession;
    }

    public String getRenderId() {
        return renderId;
    }

    public void setRenderId(String renderId) {
        this.renderId = renderId;
    }

    public AppOutput getAppOutput() {
        return appOutput;
    }

    public Trigger[] getUserInputs() {
        return userInputs;
    }

    public String getTag() {
        return tag;
    }

    public Options getOptions() {
        return options;
    }

    public boolean getIgnoreMismatch() {
        return ignoreMismatch;
    }

    public Object getAgentSetup() {
        return agentSetup;
    }

    public RunningSession getRunningSession() {
        return runningSession;
    }
}
