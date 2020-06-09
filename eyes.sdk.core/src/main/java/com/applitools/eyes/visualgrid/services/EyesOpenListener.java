package com.applitools.eyes.visualgrid.services;

import com.applitools.eyes.RunningSession;

public interface EyesOpenListener {

    void onEyesOpenComplete(IEyesConnector openedEyes, RunningSession runningSession);

    void onEyesOpenFailed();
}
