package com.applitools.eyes.selenium.rendering;

import com.applitools.connectivity.MockServerConnector;
import com.applitools.connectivity.MockedResponse;
import com.applitools.connectivity.ServerConnector;
import com.applitools.connectivity.TestServerConnector;
import com.applitools.connectivity.api.*;
import com.applitools.eyes.*;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.config.ConfigurationProvider;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.TestDataProvider;
import com.applitools.eyes.selenium.fluent.SeleniumCheckSettings;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.selenium.wrappers.EyesSeleniumDriver;
import com.applitools.eyes.selenium.wrappers.EyesTargetLocator;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumTestUtils;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.model.*;
import com.applitools.eyes.visualgrid.services.CheckTask;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.mockito.ArgumentMatchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.ws.rs.HttpMethod;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.Future;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TestRenderings extends ReportingTestSuite {

    public TestRenderings() {
        super.setGroupName("selenium");
    }

    @Test
    public void TestMobileOnly() {
        VisualGridRunner runner = new VisualGridRunner(30);
        Eyes eyes = new Eyes(runner);

        eyes.setLogHandler(TestUtils.initLogger());

        Configuration sconf = new Configuration();
        sconf.setTestName("Mobile Render Test");
        sconf.setAppName("Visual Grid Render Test");
        sconf.setBatch(TestDataProvider.batchInfo);

        sconf.addDeviceEmulation(DeviceName.Galaxy_S5);

        eyes.setConfiguration(sconf);
        ChromeDriver driver = SeleniumUtils.createChromeDriver();
        eyes.open(driver);
        driver.get("https://applitools.github.io/demo/TestPages/DynamicResolution/mobile.html");
        eyes.check("Test Mobile Only", Target.window().fully());
        driver.quit();
        eyes.close();
        runner.getAllTestResults();
    }

    @DataProvider
    public static Object[][] pages() {
        return new Object[][]{
                {"https://applitools.github.io/demo/TestPages/DomTest/shadow_dom.html", "Shadow DOM Test"},
                {"https://applitools.github.io/demo/TestPages/VisualGridTestPage/canvastest.html", "Canvas Test"}
        };
    }

    @Test(dataProvider = "pages")
    public void TestSpecialRendering(String url, String testName) {
        VisualGridRunner runner = new VisualGridRunner(30);

        String logsPath = TestUtils.initLogPath();
        LogHandler logHandler = TestUtils.initLogger("TestSpecialRendering", logsPath);
        Eyes eyes = new Eyes(runner);
        eyes.setLogHandler(logHandler);

        Configuration sconf = new Configuration();
        sconf.setTestName(testName);
        sconf.setAppName("Visual Grid Render Test");
        sconf.setBatch(TestDataProvider.batchInfo);

        sconf.addDeviceEmulation(DeviceName.Galaxy_S5);
        sconf.addBrowser(1200, 800, BrowserType.CHROME);
        sconf.addBrowser(1200, 800, BrowserType.FIREFOX);

        eyes.setConfiguration(sconf);
        ChromeDriver driver = SeleniumUtils.createChromeDriver();
        eyes.open(driver);
        driver.get(url);
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {}
        eyes.check(testName, Target.window().fully());
        driver.quit();
        eyes.close(false);
        runner.getAllTestResults(false);
    }

    @Test
    public void testRenderingIosSimulator() {
        VisualGridRunner runner = new VisualGridRunner(10);
        Eyes eyes = new Eyes(runner);
        Configuration conf = eyes.getConfiguration();
        conf.addBrowser(new IosDeviceInfo(IosDeviceName.iPhone_XR, ScreenOrientation.LANDSCAPE));
        conf.addBrowser(new IosDeviceInfo(IosDeviceName.iPhone_XR, IosVersion.LATEST));
        conf.addBrowser(new IosDeviceInfo(IosDeviceName.iPhone_XR, IosVersion.ONE_VERSION_BACK));
        conf.setSaveDiffs(false);
        conf.setSaveNewTests(false);
        eyes.setConfiguration(conf);
        eyes.setLogHandler(new StdoutLogHandler());
        ChromeDriver driver = SeleniumUtils.createChromeDriver();
        driver.get("http://applitools.github.io/demo");
        try {
            eyes.open(driver, "Eyes SDK", "UFG Mobile Web Happy Flow", new RectangleSize(800, 600));
            eyes.checkWindow();
            eyes.closeAsync();
        } finally {
            driver.quit();
            eyes.abortAsync();
            runner.getAllTestResults();
        }
    }

    @Test
    public void testRenderingMultipleBrowsers() {
        VisualGridRunner runner = new VisualGridRunner(10);
        Eyes eyes = new Eyes(runner);
        Configuration conf = eyes.getConfiguration();
        conf.addBrowser(new IosDeviceInfo(IosDeviceName.iPhone_7));
        conf.addBrowser(new ChromeEmulationInfo(DeviceName.iPhone_4, ScreenOrientation.LANDSCAPE));
        conf.addBrowser(new DesktopBrowserInfo(new RectangleSize(800, 800), BrowserType.SAFARI));
        eyes.setConfiguration(conf);
        eyes.setLogHandler(new StdoutLogHandler());
        eyes.setSaveNewTests(false);
        ChromeDriver driver = SeleniumUtils.createChromeDriver();
        driver.get("http://applitools.github.io/demo");
        try {
            eyes.open(driver, "Eyes SDK", "UFG Mobile Web Multiple Browsers", new RectangleSize(800, 800));
            eyes.checkWindow();
            eyes.closeAsync();
        } finally {
            driver.quit();
            eyes.abortAsync();
            runner.getAllTestResults();
        }
    }

    @Test
    public void testRenderFail() {
        ServerConnector serverConnector = spy(ServerConnector.class);
        doThrow(new IllegalStateException()).when(serverConnector).render(ArgumentMatchers.<TaskListener<List<RunningRender>>>any(), ArgumentMatchers.<List<RenderRequest>>any());

        EyesRunner runner = new VisualGridRunner(10);
        Eyes eyes = new Eyes(runner);
        SeleniumTestUtils.setupLogging(eyes);
        eyes.setServerConnector(serverConnector);
        eyes.setLogHandler(new StdoutLogHandler());

        com.applitools.eyes.selenium.Configuration config = eyes.getConfiguration();
        config.addBrowser(new DesktopBrowserInfo(new RectangleSize(700, 460), BrowserType.CHROME));
        config.addDeviceEmulation(DeviceName.Galaxy_S3);
        config.addBrowser(new IosDeviceInfo(IosDeviceName.iPhone_11_Pro, ScreenOrientation.LANDSCAPE));
        config.addBrowser(new IosDeviceInfo(IosDeviceName.iPhone_XR));
        config.setBatch(TestDataProvider.batchInfo);
        config.setAppName("Applitools Eyes Sdk");
        config.setTestName("Test Render Fail");
        eyes.setConfiguration(config);

        WebDriver driver = SeleniumUtils.createChromeDriver();
        driver.get("https://demo.applitools.com");
        try {
            eyes.open(driver);
            eyes.check(Target.window());
            eyes.closeAsync();
        } finally {
            driver.quit();
            eyes.abortAsync();
        }

        TestResultsSummary allTestResults = runner.getAllTestResults(false);
        Assert.assertEquals(4, allTestResults.getAllResults().length);

        // Set results in array
        TestResults[] testResults = new TestResults[4];
        for (TestResultContainer results : allTestResults.getAllResults()) {
            RenderBrowserInfo browserInfo = results.getBrowserInfo();
            if (browserInfo.getIosDeviceInfo() == null && browserInfo.getEmulationInfo() == null) {
                testResults[0] = results.getTestResults();
                continue;
            }

            if (browserInfo.getEmulationInfo() != null) {
                testResults[1] = results.getTestResults();
                continue;
            }

            String deviceName = browserInfo.getIosDeviceInfo().getDeviceName();
            if (deviceName.equals(IosDeviceName.iPhone_11_Pro.getName())) {
                testResults[2] = results.getTestResults();
                continue;
            }
            if (deviceName.equals(IosDeviceName.iPhone_XR.getName())) {
                testResults[3] = results.getTestResults();
                continue;
            }

            Assert.fail();
        }

        Assert.assertEquals(testResults[0].getHostDisplaySize(), new RectangleSize(700, 460));
        Assert.assertEquals(testResults[1].getHostDisplaySize(), new RectangleSize(360, 640));
        Assert.assertEquals(testResults[2].getHostDisplaySize(), new RectangleSize(812, 375));
        Assert.assertEquals(testResults[3].getHostDisplaySize(), new RectangleSize(414, 896));
    }

    @Test
    public void testRenderResourceNotFound() {
        final Map<String,RGridResource> missingResources = new HashMap<>();
        final String missingUrl = "http://httpstat.us/503";
        final String unknownHostUrl = "http://hostwhichdoesntexist/503";

        HttpClient client = spy(new HttpClientImpl(new Logger(), ServerConnector.DEFAULT_CLIENT_TIMEOUT, null));

        // Mocking async request to get status code 503 when downloading resource
        ConnectivityTarget target1 = mock(ConnectivityTarget.class);
        when(client.target(missingUrl)).thenReturn(target1);
        AsyncRequest request1 = spy(new TestServerConnector.MockedAsyncRequest());
        when(target1.asyncRequest(anyString())).thenReturn(request1);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                AsyncRequestCallback callback = invocation.getArgument(1);
                callback.onComplete(new MockedResponse(new Logger(), 400, "", "".getBytes()));
                return null;
            }
        }).when(request1).method(eq(HttpMethod.GET), any(AsyncRequestCallback.class), nullable(String.class), nullable(String.class), eq(false));

        // Mocking async request to get an exception when downloading resource
        ConnectivityTarget target2 = mock(ConnectivityTarget.class);
        when(client.target(unknownHostUrl)).thenReturn(target2);
        AsyncRequest request2 = spy(new TestServerConnector.MockedAsyncRequest());
        when(target2.asyncRequest(anyString())).thenReturn(request2);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                AsyncRequestCallback callback = invocation.getArgument(1);
                callback.onFail(new UnknownHostException());
                return null;
            }
        }).when(request2).method(eq(HttpMethod.GET), any(AsyncRequestCallback.class), nullable(String.class), nullable(String.class), eq(false));


        // Mocking server connector to add fake missing resources to the render request
        final ServerConnector serverConnector = new ServerConnector() {
            @Override
            public void checkResourceStatus(final TaskListener<Boolean[]> listener, Set<String> testIds, String renderId, HashObject... hashes) {
                Boolean[] result = new Boolean[hashes.length];
                for (int i = 0; i < hashes.length; i++) {
                    result[i] = !hashes[i].getHash().equals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
                }
                listener.onComplete(result);
            }

            @Override
            public Future<?> renderPutResource(Set<String> testIds, String renderId, RGridResource resource, TaskListener<Void> listener) {
                missingResources.put(resource.getUrl(), resource);
                return super.renderPutResource(testIds, renderId, resource, listener);
            }
        };
        VisualGridRunner runner = spy(new VisualGridRunner(10));
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                FrameData frameData = invocation.getArgument(0);
                frameData.getResourceUrls().add(missingUrl);
                frameData.getResourceUrls().add(unknownHostUrl);
                invocation.callRealMethod();
                return null;
            }
        }).when(runner).check(any(FrameData.class), ArgumentMatchers.<List<CheckTask>>any());

        Eyes eyes = new Eyes(runner);
        eyes.setLogHandler(new StdoutLogHandler());
        eyes.setServerConnector(serverConnector);
        ChromeDriver driver = SeleniumUtils.createChromeDriver();
        try {
            eyes.open(driver, "Applitools Eyes Sdk", "Test Render Resource Not Found", new RectangleSize(800, 800));
            driver.get("http://applitools.github.io/demo");
            serverConnector.updateClient(client);
            eyes.checkWindow();
            eyes.closeAsync();
        } finally {
            driver.quit();
            eyes.abortAsync();
            TestResultsSummary summary = runner.getAllTestResults(false);
            TestResults results = summary.getAllResults()[0].getTestResults();
            results.delete();
        }

        // Only one empty resource will be sent to the server
        Assert.assertEquals(missingResources.size(), 1);
    }

    @Test
    public void testVisualGridOptions() {
        VisualGridRunner runner = new VisualGridRunner(10);
        Eyes eyes = new Eyes(runner);
        Configuration configuration = eyes.getConfiguration();
        configuration.addBrowser(800, 600, BrowserType.CHROME);
        configuration.setVisualGridOptions(new VisualGridOption("option1", "value1"), new VisualGridOption("option2", false));
        eyes.setConfiguration(configuration);
        MockServerConnector serverConnector = new MockServerConnector();
        eyes.setServerConnector(serverConnector);
        eyes.setLogHandler(new StdoutLogHandler());

        WebDriver driver = SeleniumUtils.createChromeDriver();
        driver.get("https://applitools.github.io/demo/TestPages/DynamicResolution/desktop.html");
        try {
            eyes.open(driver, "Mock app", "Mock test");

            eyes.check(Target.window().visualGridOptions(new VisualGridOption("option3", "value3"), new VisualGridOption("option4", 5)));

            eyes.checkWindow();

            configuration = eyes.getConfiguration();
            configuration.setVisualGridOptions(null);
            eyes.setConfiguration(configuration);
            eyes.checkWindow();

            eyes.closeAsync();
        } finally {
            driver.quit();
            runner.getAllTestResults(false);
        }

        List<RenderRequest> renderRequests = serverConnector.renderRequests;
        Map<String, Object> expected = new HashMap<>();
        Assert.assertEquals(renderRequests.get(2).getOptions(), expected);

        expected.put("option1", "value1");
        expected.put("option2", false);
        Assert.assertEquals(renderRequests.get(1).getOptions(), expected);

        expected.put("option3", "value3");
        expected.put("option4", 5);
        Assert.assertEquals(renderRequests.get(0).getOptions(), expected);
    }

    @Test
    public void testRenderStatusNull() {
        MockServerConnector mockServerConnector = new MockServerConnector() {
            @Override
            public void renderStatusById(final TaskListener<List<RenderStatusResults>> listener, List<String> testIds, List<String> renderIds) {
                listener.onComplete(new ArrayList<RenderStatusResults>() {{add(null);}});
            }
        };

        VisualGridRunner runner = new VisualGridRunner(10);
        Eyes eyes = new Eyes(runner);
        eyes.setLogHandler(new StdoutLogHandler());
        eyes.setServerConnector(mockServerConnector);
        ChromeDriver driver = SeleniumUtils.createChromeDriver();
        try {
            eyes.open(driver, "Applitools Eyes Sdk", "Test Render Status Null", new RectangleSize(800, 800));
            driver.get("http://applitools.github.io/demo");
            eyes.checkWindow();
            eyes.closeAsync();
        } finally {
            driver.quit();
            eyes.abortAsync();
        }

        try {
            runner.getAllTestResults();
            Assert.fail("Test should fail");
        } catch (Throwable t) {
            Assert.assertTrue(t.getMessage().contains("Render status result was null"));
        }
    }

    @Test
    public void testCaptureDomSnapshot() throws Exception {
        final Configuration configuration = new Configuration();
        ConfigurationProvider configurationProvider = new ConfigurationProvider() {
            @Override
            public Configuration get() {
                return configuration;
            }
        };
        WebDriver driver = SeleniumUtils.createChromeDriver();
        driver.get("https://applitools.github.io/demo/TestPages/CorsTestPage/");
        VisualGridEyes eyes = new VisualGridEyes(new VisualGridRunner(10), configurationProvider);
        driver = eyes.open(driver, "test", "test", new RectangleSize(800, 800));
        EyesTargetLocator switchTo = ((EyesTargetLocator) driver.switchTo());
        FrameData frameData = eyes.captureDomSnapshot(new HashSet<String>(), switchTo);
        driver.quit();
        Assert.assertEquals(frameData.getFrames().size(), 1);

        FrameData innerFrame = frameData.getFrames().get(0);
        Assert.assertEquals(innerFrame.getCrossFrames().size(), 1);
        Assert.assertEquals(innerFrame.getFrames().size(), 1);

        int cdtIndex = innerFrame.getCrossFrames().get(0).getIndex();
        List<AttributeData> attributeData = innerFrame.getCdt().get(cdtIndex).attributes;
        AttributeData applitoolsSrc = attributeData.get(attributeData.size() - 1);
        Assert.assertEquals(applitoolsSrc.name, "data-applitools-src");
        Assert.assertEquals(applitoolsSrc.value, innerFrame.getFrames().get(0).getUrl());
    }

    @Test
    public void testMapRunningTestsToRequiredBrowserWidth() {
        final Configuration configuration = new Configuration();
        ConfigurationProvider configurationProvider = new ConfigurationProvider() {
            @Override
            public Configuration get() {
                return configuration;
            }
        };
        configuration.addBrowser(new DesktopBrowserInfo(1000, 500, BrowserType.CHROME));
        configuration.addBrowser(new DesktopBrowserInfo(1000, 700, BrowserType.CHROME));
        configuration.addBrowser(new DesktopBrowserInfo(700, 500, BrowserType.CHROME));
        VisualGridEyes eyes = spy(new VisualGridEyes(new VisualGridRunner(10), configurationProvider));
        doNothing().when(eyes).setViewportSize(ArgumentMatchers.<EyesSeleniumDriver>any());
        eyes.setServerConnector(new MockServerConnector());

        RemoteWebDriver driver = mock(RemoteWebDriver.class);
        when(driver.getSessionId()).thenReturn(mock(SessionId.class));
        eyes.open(driver, "app", "test", new RectangleSize(800, 800));

        SeleniumCheckSettings seleniumCheckSettings = Target.window();
        Map<Integer, List<RunningTest>> map = eyes.mapRunningTestsToRequiredBrowserWidth(seleniumCheckSettings);
        Assert.assertTrue(map.isEmpty());

        seleniumCheckSettings = Target.window().layoutBreakpoints(false);
        map = eyes.mapRunningTestsToRequiredBrowserWidth(seleniumCheckSettings);
        Assert.assertTrue(map.isEmpty());

        seleniumCheckSettings = Target.window().layoutBreakpoints(true);
        map = eyes.mapRunningTestsToRequiredBrowserWidth(seleniumCheckSettings);
        Assert.assertEquals(map.keySet(), new HashSet<>(Arrays.asList(700, 1000)));
        Assert.assertEquals(map.get(700).size(), 1);
        Assert.assertEquals(map.get(1000).size(), 2);

        seleniumCheckSettings = Target.window().layoutBreakpoints(750, 1200);
        map = eyes.mapRunningTestsToRequiredBrowserWidth(seleniumCheckSettings);
        Assert.assertEquals(map.keySet(), new HashSet<>(Arrays.asList(749, 750)));
        Assert.assertEquals(map.get(749).size(), 1);
        Assert.assertEquals(map.get(750).size(), 2);

        seleniumCheckSettings = Target.window().layoutBreakpoints(700);
        map = eyes.mapRunningTestsToRequiredBrowserWidth(seleniumCheckSettings);
        Assert.assertEquals(map.keySet(), new HashSet<>(Collections.singletonList(700)));
        Assert.assertEquals(map.get(700).size(), 3);

        configuration.setLayoutBreakpoints(false);
        seleniumCheckSettings = Target.window();
        map = eyes.mapRunningTestsToRequiredBrowserWidth(seleniumCheckSettings);
        Assert.assertTrue(map.isEmpty());

        configuration.setLayoutBreakpoints(true);
        seleniumCheckSettings = Target.window();
        map = eyes.mapRunningTestsToRequiredBrowserWidth(seleniumCheckSettings);
        Assert.assertEquals(map.keySet(), new HashSet<>(Arrays.asList(700, 1000)));
        Assert.assertEquals(map.get(700).size(), 1);
        Assert.assertEquals(map.get(1000).size(), 2);

        configuration.setLayoutBreakpoints(750, 1200);
        seleniumCheckSettings = Target.window();
        map = eyes.mapRunningTestsToRequiredBrowserWidth(seleniumCheckSettings);
        Assert.assertEquals(map.keySet(), new HashSet<>(Arrays.asList(749, 750)));
        Assert.assertEquals(map.get(749).size(), 1);
        Assert.assertEquals(map.get(750).size(), 2);

        configuration.setLayoutBreakpoints(750, 1200);
        seleniumCheckSettings = Target.window().layoutBreakpoints(true);
        map = eyes.mapRunningTestsToRequiredBrowserWidth(seleniumCheckSettings);
        Assert.assertEquals(map.keySet(), new HashSet<>(Arrays.asList(700, 1000)));
        Assert.assertEquals(map.get(700).size(), 1);
        Assert.assertEquals(map.get(1000).size(), 2);

        configuration.setLayoutBreakpoints(750, 1200);
        seleniumCheckSettings = Target.window().layoutBreakpoints(700);
        map = eyes.mapRunningTestsToRequiredBrowserWidth(seleniumCheckSettings);
        Assert.assertEquals(map.keySet(), new HashSet<>(Collections.singletonList(700)));
        Assert.assertEquals(map.get(700).size(), 3);
    }
}
