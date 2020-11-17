package com.applitools.eyes.visualgrid.services;

import com.applitools.ICheckSettings;
import com.applitools.eyes.MatchResult;
import com.applitools.eyes.visualgrid.model.RenderBrowserInfo;
import com.applitools.eyes.visualgrid.model.RenderStatusResults;
import com.applitools.eyes.visualgrid.model.VisualGridSelector;

import java.util.List;
import java.util.UUID;

public class CheckTask {
    private final String stepId = UUID.randomUUID().toString();
    private final VisualGridRunningTest runningTest;
    private final ICheckSettings checkSettings;
    private final List<VisualGridSelector[]> regionSelectors;
    private final String source;

    private RenderStatusResults renderStatusResults;

    public CheckTask(VisualGridRunningTest runningTest, ICheckSettings checkSettings, List<VisualGridSelector[]> regionSelectors, String source) {
        this.runningTest = runningTest;
        this.checkSettings = checkSettings;
        this.regionSelectors = regionSelectors;
        this.source = source;
    }

    public String getStepId() {
        return stepId;
    }

    public ICheckSettings getCheckSettings() {
        return checkSettings;
    }

    public List<VisualGridSelector[]> getRegionSelectors() {
        return regionSelectors;
    }

    public String getSource() {
        return source;
    }

    public String getRenderer() {
        return runningTest.getRenderer();
    }

    public RenderStatusResults getRenderStatusResults() {
        return renderStatusResults;
    }

    public void setRenderStatusResults(RenderStatusResults renderStatusResults) {
        this.renderStatusResults = renderStatusResults;
    }

    public boolean isRenderFinished() {
        return renderStatusResults != null;
    }

    public boolean isReadyForRender() {
        return runningTest.isOpen() && runningTest.isCheckTaskReadyForRender(this);
    }

    public String getTestId() {
        return runningTest.getTestId();
    }

    public RenderBrowserInfo getBrowserInfo() {
        return runningTest.getBrowserInfo();
    }

    public boolean isTestActive() {
        return !runningTest.isTestReadyToClose() && !runningTest.isTestAborted();
    }

    public void onComplete(MatchResult matchResult) {
        runningTest.checkCompleted(this, matchResult);
    }

    public void onFail(Throwable e) {
        runningTest.setTestInExceptionMode(e);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CheckTask that = (CheckTask) o;
        return stepId.equals(that.stepId) && runningTest.equals(that.runningTest);
    }
}
