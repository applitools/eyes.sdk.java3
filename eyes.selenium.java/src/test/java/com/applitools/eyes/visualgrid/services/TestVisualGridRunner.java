package com.applitools.eyes.visualgrid.services;

import com.applitools.connectivity.MockServerConnector;
import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.*;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.config.ConfigurationProvider;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.selenium.rendering.VisualGridEyes;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.visualgrid.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.mockito.ArgumentMatchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.Mockito.*;

public class TestVisualGridRunner {

    Configuration configuration = new Configuration();
    ConfigurationProvider configurationProvider = new ConfigurationProvider() {
        @Override
        public Configuration get() {
            return configuration;
        }
    };

    @BeforeMethod
    public void beforeEach() {
        configuration = new Configuration();
    }

    @Test
    public void testOpenBeforeRender() {
        final VisualGridRunner runner = new VisualGridRunner(10);

        final AtomicReference<String> errorMessage = new AtomicReference<>();
        ServerConnector serverConnector = spy(new MockServerConnector());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                IEyes eyes = runner.allEyes.iterator().next();
                if (!eyes.getAllRunningTests().values().iterator().next().isOpen()) {
                    errorMessage.set("Render called before open");
                }

                invocation.callRealMethod();
                return null;
            }
        }).when(serverConnector).render(ArgumentMatchers.<TaskListener<List<RunningRender>>>any(), ArgumentMatchers.<RenderRequest[]>any());

        Eyes eyes = new Eyes(runner);
        eyes.setLogHandler(new StdoutLogHandler());
        Configuration configuration = eyes.getConfiguration();
        configuration.addBrowser(new IosDeviceInfo(IosDeviceName.iPhone_7));
        eyes.setConfiguration(configuration);
        eyes.setServerConnector(serverConnector);
        WebDriver driver = SeleniumUtils.createChromeDriver();
        try {
            eyes.open(driver, "Eyes SDK", "UFG Flow");
            driver.get("http://applitools.github.io/demo");
            eyes.checkWindow();
            eyes.closeAsync();
        } finally {
            eyes.abortAsync();
            driver.quit();
            runner.getAllTestResults();
        }

        Assert.assertNull(errorMessage.get(), errorMessage.get());
    }

    @Test
    public void testRunnerConcurrency() {
        final VisualGridRunner runner = spy(new VisualGridRunner(new RunnerOptions().testConcurrency(3)));

        final AtomicInteger currentlyOpenTests = new AtomicInteger(0);
        final AtomicBoolean isFail = new AtomicBoolean(false);
        ServerConnector serverConnector = new MockServerConnector() {
            @Override
            public void startSession(final TaskListener<RunningSession> listener, SessionStartInfo sessionStartInfo) {
                currentlyOpenTests.incrementAndGet();
                if (currentlyOpenTests.get() > 3) {
                    isFail.set(true);
                }

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ignored) {}

                if (currentlyOpenTests.get() > 3) {
                    isFail.set(true);
                }

                super.startSession(listener, sessionStartInfo);
            }

            @Override
            public void stopSession(final TaskListener<TestResults> listener, SessionStopInfo sessionStopInfo) {
                super.stopSession(listener, sessionStopInfo);
                currentlyOpenTests.decrementAndGet();
            }
        };

        VisualGridEyes eyes = new VisualGridEyes(runner, configurationProvider);
        eyes.setLogHandler(new StdoutLogHandler());
        eyes.setServerConnector(serverConnector);
        configuration.addBrowser(new IosDeviceInfo(IosDeviceName.iPhone_7));
        configuration.addDeviceEmulation(DeviceName.Galaxy_S5, ScreenOrientation.LANDSCAPE);
        configuration.addBrowser(new DesktopBrowserInfo(800, 800, BrowserType.CHROME));
        configuration.addBrowser(new DesktopBrowserInfo(800, 800, BrowserType.FIREFOX));
        configuration.addBrowser(new DesktopBrowserInfo(800, 800, BrowserType.SAFARI));
        WebDriver driver = SeleniumUtils.createChromeDriver();
        try {
            eyes.open(driver, "Eyes SDK", "UFG Runner Concurrency", new RectangleSize(800, 800));
            driver.get("http://applitools.github.io/demo");
            eyes.check(Target.window().fully());
            eyes.closeAsync();
        } finally {
            eyes.abortAsync();
            driver.quit();
            runner.getAllTestResults();
        }

        Assert.assertFalse(isFail.get(), "Number of open tests was higher than the concurrency limit");
    }

    @Test
    public void testRetryWhenServerConcurrencyLimitReached() {
        final VisualGridRunner runner = new VisualGridRunner(5);

        final AtomicInteger counter = new AtomicInteger(0);
        ServerConnector serverConnector = new MockServerConnector() {
            @Override
            public void startSession(final TaskListener<RunningSession> listener, SessionStartInfo sessionStartInfo) {
                if (counter.getAndIncrement() < 3) {
                    final RunningSession newSession = new RunningSession();
                    newSession.setConcurrencyFull(true);
                    listener.onComplete(newSession);
                    return;
                }

                // Return a valid response from the server
                super.startSession(listener, sessionStartInfo);
            }
        };

        VisualGridEyes eyes = spy(new VisualGridEyes(runner, configurationProvider));

        eyes.setLogHandler(new StdoutLogHandler());
        eyes.setServerConnector(serverConnector);
        configuration.addBrowser(new IosDeviceInfo(IosDeviceName.iPhone_7));
        WebDriver driver = SeleniumUtils.createChromeDriver();
        try {
            eyes.open(driver, "Eyes SDK", "UFG Server Concurrency", new RectangleSize(800, 800));
            driver.get("http://applitools.github.io/demo");
            eyes.check(Target.window().fully());
            eyes.closeAsync();
        } finally {
            eyes.abortAsync();
            driver.quit();
            runner.getAllTestResults();
        }

        Assert.assertEquals(counter.get(), 4);
    }

    @Test
    public void testConcurrencyAmount() {
        VisualGridRunner runner = new VisualGridRunner();
        Assert.assertEquals(runner.testConcurrency.actualConcurrency, VisualGridRunner.DEFAULT_CONCURRENCY);
        Assert.assertEquals(runner.testConcurrency.userConcurrency, VisualGridRunner.DEFAULT_CONCURRENCY);
        Assert.assertTrue(runner.testConcurrency.isDefault);
        Assert.assertFalse(runner.testConcurrency.isLegacy);

        runner = new VisualGridRunner("");
        Assert.assertEquals(runner.testConcurrency.actualConcurrency, VisualGridRunner.DEFAULT_CONCURRENCY);
        Assert.assertEquals(runner.testConcurrency.userConcurrency, VisualGridRunner.DEFAULT_CONCURRENCY);
        Assert.assertTrue(runner.testConcurrency.isDefault);
        Assert.assertFalse(runner.testConcurrency.isLegacy);

        runner = new VisualGridRunner(10);
        Assert.assertEquals(runner.testConcurrency.actualConcurrency, 50);
        Assert.assertEquals(runner.testConcurrency.userConcurrency, 10);
        Assert.assertFalse(runner.testConcurrency.isDefault);
        Assert.assertTrue(runner.testConcurrency.isLegacy);

        runner = new VisualGridRunner(5, "");
        Assert.assertEquals(runner.testConcurrency.actualConcurrency, 25);
        Assert.assertEquals(runner.testConcurrency.userConcurrency, 5);
        Assert.assertFalse(runner.testConcurrency.isDefault);
        Assert.assertTrue(runner.testConcurrency.isLegacy);

        runner = new VisualGridRunner(new RunnerOptions().testConcurrency(5));
        Assert.assertEquals(runner.testConcurrency.actualConcurrency, 5);
        Assert.assertEquals(runner.testConcurrency.userConcurrency, 5);
        Assert.assertFalse(runner.testConcurrency.isDefault);
        Assert.assertFalse(runner.testConcurrency.isLegacy);

        runner = new VisualGridRunner(new RunnerOptions().testConcurrency(5).testConcurrency(7));
        Assert.assertEquals(runner.testConcurrency.actualConcurrency, 7);
        Assert.assertEquals(runner.testConcurrency.userConcurrency, 7);
        Assert.assertFalse(runner.testConcurrency.isDefault);
        Assert.assertFalse(runner.testConcurrency.isLegacy);

        runner = new VisualGridRunner(new RunnerOptions().testConcurrency(10), "");
        Assert.assertEquals(runner.testConcurrency.actualConcurrency, 10);
        Assert.assertEquals(runner.testConcurrency.userConcurrency, 10);
        Assert.assertFalse(runner.testConcurrency.isDefault);
        Assert.assertFalse(runner.testConcurrency.isLegacy);
    }

    @Test
    public void testConcurrencyLogMessage() throws JsonProcessingException {
        VisualGridRunner runner = new VisualGridRunner();
        Assert.assertEquals(runner.getConcurrencyLog(),
                String.format("{\"type\":\"runnerStarted\",\"defaultConcurrency\":%d}", VisualGridRunner.DEFAULT_CONCURRENCY));

        runner = new VisualGridRunner(10);
        Assert.assertEquals(runner.getConcurrencyLog(),
                String.format("{\"type\":\"runnerStarted\",\"concurrency\":%d}", 10));

        runner = new VisualGridRunner(new RunnerOptions().testConcurrency(10));
        Assert.assertEquals(runner.getConcurrencyLog(),
                String.format("{\"type\":\"runnerStarted\",\"testConcurrency\":%d}", 10));

        Assert.assertNull(runner.getConcurrencyLog());
    }

    @Test
    public void testParallelStepsLimitOfTest() {
        final AtomicBoolean isOnlyOneRender = new AtomicBoolean(true);
        final AtomicInteger runningRendersCount = new AtomicInteger(0);

        VisualGridRunner runner = new VisualGridRunner();
        MockServerConnector serverConnector = new MockServerConnector() {
            public void render(final TaskListener<List<RunningRender>> listener, RenderRequest... renderRequests) {
                if (runningRendersCount.getAndIncrement() >= VisualGridRunningTest.PARALLEL_STEPS_LIMIT) {
                    isOnlyOneRender.set(false);
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
                if (renderRequests.length != 1) {
                    isOnlyOneRender.set(false);
                }
                super.render(new TaskListener<List<RunningRender>>() {
                    @Override
                    public void onComplete(List<RunningRender> taskResponse) {
                        runningRendersCount.decrementAndGet();
                        listener.onComplete(taskResponse);
                    }

                    @Override
                    public void onFail() {
                        runningRendersCount.decrementAndGet();
                        listener.onFail();
                    }
                }, renderRequests);
            }
        };

        Eyes eyes = new Eyes(runner);
        eyes.setLogHandler(new StdoutLogHandler());
        eyes.setServerConnector(serverConnector);
        configuration.addBrowser(new IosDeviceInfo(IosDeviceName.iPhone_7));
        WebDriver driver = SeleniumUtils.createChromeDriver();
        try {
            eyes.open(driver, "Eyes SDK", "UFG Server Concurrency", new RectangleSize(800, 800));
            driver.get("http://applitools.github.io/demo");
            for (int i = 0; i < 10; i++) {
                eyes.check(Target.window().fully());
            }
            eyes.closeAsync();
        } finally {
            eyes.abortAsync();
            driver.quit();
            runner.getAllTestResults();
        }

        Assert.assertTrue(isOnlyOneRender.get());
    }
}
