package com.applitools.eyes.utils;

import com.applitools.eyes.FileLogger;
import com.applitools.eyes.LogHandler;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.selenium.Eyes;

import java.io.File;

import static com.applitools.eyes.utils.TestUtils.logsPath;

public class SeleniumTestUtils {
    public static void setupLogging(Eyes eyes) {
        setupLogging(eyes, Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    public static void setupLogging(Eyes eyes, String methodName) {
        LogHandler logHandler;
        if (!TestUtils.runOnCI && logsPath != null) {
            String path = TestUtils.initLogPath(methodName);
            logHandler = new FileLogger(path + File.separator + methodName + ".log", false, true);
            eyes.setDebugScreenshotsPath(path);
            eyes.setDebugScreenshotsPrefix(methodName + "_");
            eyes.setSaveDebugScreenshots(true);
        } else {
            logHandler = new StdoutLogHandler();
        }
        eyes.setLogHandler(logHandler);
    }
}
