package com.applitools.eyes;

public class SessionStopInfo {
    RunningSession runningSession;
    boolean isAborted;
    boolean shouldSave;

    public SessionStopInfo(RunningSession runningSession, boolean isAborted, boolean shouldSave) {
        this.runningSession = runningSession;
        this.isAborted = isAborted;
        this.shouldSave = shouldSave;
    }

    public RunningSession getRunningSession() {
        return runningSession;
    }

    public void setRunningSession(RunningSession runningSession) {
        this.runningSession = runningSession;
    }

    public boolean isAborted() {
        return isAborted;
    }

    public void setAborted(boolean aborted) {
        isAborted = aborted;
    }

    public boolean shouldSave() {
        return shouldSave;
    }

    public void setShouldSave(boolean shouldSave) {
        this.shouldSave = shouldSave;
    }

    @Override
    public String toString() {
        return "SessionStopInfo{" +
                "runningSession=" + runningSession +
                ", isAborted=" + isAborted +
                ", shouldSave=" + shouldSave +
                '}';
    }
}
