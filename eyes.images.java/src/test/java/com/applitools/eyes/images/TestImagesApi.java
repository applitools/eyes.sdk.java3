package com.applitools.eyes.images;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.LogHandler;
import com.applitools.eyes.StdoutLogHandler;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.net.URI;

@RunWith(JUnit4.class)
public class TestImagesApi {
    private static Eyes eyes;
    private static LogHandler logHandler;

    @BeforeClass
    public static void OneTimeSetUp() {

        // Initialize the eyes SDK and set your private API key.
        eyes = new Eyes();
        eyes.setServerUrl(URI.create("https://localhost.applitools.com"));
        eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));

        //logHandler = new FileLogger("c:\\temp\\logs\\TestElement.log", true, true);
        logHandler = new StdoutLogHandler(true);
        eyes.setLogHandler(logHandler);

        eyes.open("TestImagesApi", "Test Images API");
    }

    @AfterClass
    public static void Finished() {
        try {
            eyes.close();
        } finally {
            eyes.abortIfNotClosed();
        }
    }

    @Test
    public void TestCheckImage() {
        eyes.getLogHandler().onMessage(true, System.getProperty("user.dir"));
        eyes.checkImage("resources/minions-800x500.jpg");
    }

}
