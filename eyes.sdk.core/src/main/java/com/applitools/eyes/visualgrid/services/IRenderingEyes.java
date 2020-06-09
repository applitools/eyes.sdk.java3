package com.applitools.eyes.visualgrid.services;

import com.applitools.eyes.IBatchCloser;
import com.applitools.eyes.Logger;
import com.applitools.eyes.TestResultContainer;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

public interface IRenderingEyes {

    boolean isEyesClosed();

    RunningTest getNextTestToClose();

    void setListener(EyesListener listener);

    ScoreTask getBestScoreTaskForCheck();

    ScoreTask getBestScoreTaskForOpen();

    Collection<Future<TestResultContainer>> close();

    Logger getLogger();

    List<TestResultContainer> getAllTestResults();

    IBatchCloser getBatchCloser();

    String getBatchId();

    interface EyesListener{

        void onTaskComplete(VisualGridTask visualGridTask, IRenderingEyes eyes);

        void onRenderComplete();
    }

    List<RunningTest> getAllRunningTests();
}
