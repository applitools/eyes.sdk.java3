package com.applitools.eyes;

import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.universal.server.UniversalSdkNativeLoader;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LogHandlerTest {

    private final PrintStream stdout = System.out;
    private final String filepath = "src/test/java/com/applitools/eyes/test.log";

    @Test
    public void shouldWriteUniversalCoreLogsToStdoutWithVisualGridRunnerOptions() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Redirecting console output to a stream
        System.setOut(new PrintStream(baos));

        RunnerOptions runnerOptions = new RunnerOptions().logHandler(new StdoutLogHandler(true));

        new VisualGridRunner(runnerOptions);
        restoreIO();

        String[] logs = baos.toString().split("\n", 2);
        Assert.assertFalse(logs[0].isEmpty());
        Assert.assertFalse(Files.exists(Paths.get(filepath)));
    }

    @Test
    public void shouldWriteUniversalCoreLogsToFile() throws IOException {
        RunnerOptions runnerOptions = new RunnerOptions().logHandler(new FileLogger(filepath, false, true));
        new VisualGridRunner(runnerOptions);
        Assert.assertTrue(Files.exists(Paths.get(filepath)));
        Files.delete(Paths.get(filepath));
    }

    @Test
    public void shouldNotWriteLogsWhenUsingClassicRunner() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Redirecting console output to a stream
        System.setOut(new PrintStream(baos));

        new ClassicRunner();

        restoreIO();

        String[] logs = baos.toString().split("\n", 2);
        Assert.assertTrue(logs[0].isEmpty());
        Assert.assertFalse(Files.exists(Paths.get(filepath)));
    }

    @Test
    public void shouldNotWriteLogsIfNotSpecifiedLogHandler() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Redirecting console output to a stream
        System.setOut(new PrintStream(baos));

        new VisualGridRunner(new RunnerOptions());

        restoreIO();

        String[] logs = baos.toString().split("\n", 2);
        Assert.assertTrue(logs[0].isEmpty());
        Assert.assertFalse(Files.exists(Paths.get(filepath)));
    }

    @Test
    public void shouldNotWriteLogsStartingServerManually() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Redirecting console output to a stream
        System.setOut(new PrintStream(baos));

        UniversalSdkNativeLoader.start();

        restoreIO();

        String[] logs = baos.toString().split("\n", 2);
        Assert.assertTrue(logs[0].isEmpty());
        Assert.assertFalse(Files.exists(Paths.get(filepath)));
    }

    private void restoreIO() {
        // Restore console output to stdout
        System.setOut(stdout);
    }
}
