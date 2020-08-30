package com.applitools.eyes.renderingGrid;

import com.applitools.ICheckSettings;
import com.applitools.connectivity.MockServerConnector;
import com.applitools.connectivity.MockedResponse;
import com.applitools.connectivity.ServerConnector;
import com.applitools.connectivity.TestServerConnector;
import com.applitools.connectivity.api.*;
import com.applitools.eyes.*;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.metadata.SessionResults;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.TestDataProvider;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.*;
import com.applitools.eyes.visualgrid.model.*;
import com.applitools.eyes.visualgrid.services.IEyesConnector;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.applitools.eyes.visualgrid.services.VisualGridTask;
import com.applitools.utils.GeneralUtils;
import org.mockito.ArgumentMatchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.ws.rs.HttpMethod;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

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
        runner.setDebugResourceWriter(new FileDebugResourceWriter(eyes.getLogger(), logsPath, null, null));

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
        } catch (InterruptedException e) {
            GeneralUtils.logExceptionStackTrace(eyes.getLogger(), e);
        }
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
        conf.setSaveDiffs(false);
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
    public void testRenderFail() throws IOException {
        ServerConnector serverConnector = spy(ServerConnector.class);
        doThrow(new IllegalStateException()).when(serverConnector).render(any(RenderRequest.class));

        EyesRunner runner = new VisualGridRunner(10);
        Eyes eyes = new Eyes(runner);
        SeleniumTestUtils.setupLogging(eyes);
        eyes.setServerConnector(serverConnector);

        com.applitools.eyes.selenium.Configuration config = eyes.getConfiguration();
        config.addBrowser(new DesktopBrowserInfo(new RectangleSize(700, 460), BrowserType.CHROME));
        config.addDeviceEmulation(DeviceName.Galaxy_S3);
        config.addBrowser(new IosDeviceInfo(IosDeviceName.iPhone_11_Pro));
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

        SessionResults chromeSessionResults = TestUtils.getSessionResults(eyes.getApiKey(), testResults[0]);
        String actualUserAgent = chromeSessionResults.getStartInfo().getEnvironment().getInferred();
        Map<String, String> userAgents = eyes.getServerConnector().getUserAgents();
        Assert.assertEquals(actualUserAgent, "useragent:" + userAgents.get("chrome-0"));

        Assert.assertEquals(testResults[0].getHostDisplaySize(), new RectangleSize(700, 460));
        Assert.assertEquals(testResults[1].getHostDisplaySize(), new RectangleSize(360, 640));
        Assert.assertEquals(testResults[2].getHostDisplaySize(), new RectangleSize(375, 812));
        Assert.assertEquals(testResults[3].getHostDisplaySize(), new RectangleSize(414, 896));
    }

    @Test
    public void testRenderResourceNotFound() {
        final AtomicBoolean alreadyRendered = new AtomicBoolean(false);
        final Map<String,RGridResource> missingResources = new HashMap<>();
        final String missingUrl = "http://httpstat.us/503";
        final String unknownHostUrl = "http://hostwhichdoesntexist/503";

        HttpClient client = spy(new HttpClientImpl(new Logger(), ServerConnector.DEFAULT_CLIENT_TIMEOUT, null));

        // Mocking async request to get status code 503 when downloading resource
        ConnectivityTarget target1 = mock(ConnectivityTarget.class);
        when(client.target(missingUrl)).thenReturn(target1);
        AsyncRequest request1 = spy(new TestServerConnector.MockedAsyncRequest(new Logger()));
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
        AsyncRequest request2 = spy(new TestServerConnector.MockedAsyncRequest(new Logger()));
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
        ServerConnector serverConnector = spy(new ServerConnector());
        serverConnector.updateClient(client);
        doAnswer(new Answer<List<RunningRender>>() {
            @Override
            public List<RunningRender> answer(InvocationOnMock invocation) throws Throwable {
                List<RunningRender> runningRenders = (List<RunningRender>) invocation.callRealMethod();
                if (alreadyRendered.get()) {
                    return runningRenders;
                }

                alreadyRendered.set(true);
                runningRenders.get(0).setNeedMoreResources(Arrays.asList(missingUrl, unknownHostUrl));
                runningRenders.get(0).setRenderStatus(RenderStatus.NEED_MORE_RESOURCE);
                return runningRenders;
            }
        }).when(serverConnector).render(any(RenderRequest.class));
        doAnswer(new Answer<Future<?>>() {
            @Override
            public Future<?> answer(InvocationOnMock invocation) throws Throwable {
                RGridResource rGridResource = invocation.getArgument(1);
                missingResources.put(rGridResource.getUrl(), rGridResource);
                return (Future<?>) invocation.callRealMethod();
            }
        }).when(serverConnector).renderPutResource(any(RunningRender.class), any(RGridResource.class), anyString(), ArgumentMatchers.<TaskListener<Boolean>>any());

        VisualGridRunner runner = spy(new VisualGridRunner(10));
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                FrameData frameData = invocation.getArgument(2);
                frameData.getResourceUrls().add(missingUrl);
                frameData.getResourceUrls().add(unknownHostUrl);
                invocation.callRealMethod();
                return null;
            }
        }).when(runner).check(any(ICheckSettings.class), nullable(IDebugResourceWriter.class), any(FrameData.class),
                any(IEyesConnector.class), ArgumentMatchers.<List<VisualGridTask>>any(), ArgumentMatchers.<List<VisualGridTask>>any(),
                any(VisualGridRunner.RenderListener.class), ArgumentMatchers.<List<VisualGridSelector[]>>any(), any(UserAgent.class));

        Eyes eyes = new Eyes(runner);
        eyes.setLogHandler(new StdoutLogHandler());
        eyes.setServerConnector(serverConnector);
        ChromeDriver driver = SeleniumUtils.createChromeDriver();
        try {
            eyes.open(driver, "Applitools Eyes Sdk", "Test Render Resource Not Found", new RectangleSize(800, 800));
            driver.get("http://applitools.github.io/demo");
            eyes.checkWindow();
            eyes.closeAsync();
        } finally {
            driver.quit();
            eyes.abortAsync();
            TestResultsSummary summary = runner.getAllTestResults(false);
            TestResults results = summary.getAllResults()[0].getTestResults();
            ServerConnector connector = new ServerConnector();
            try {
                results.setServerConnector(connector);
                results.delete();
            } finally {
                connector.closeConnector();
            }
        }

        RGridResource missingResource = missingResources.get(missingUrl);
        Assert.assertEquals(missingResource.getUrl(), missingUrl);
        Assert.assertEquals(missingResource.getContent().length, 0);
        Assert.assertEquals(missingResource.getContentType(), "application/empty-response");

        RGridResource unknownHostResource = missingResources.get(unknownHostUrl);
        Assert.assertEquals(unknownHostResource.getUrl(), unknownHostUrl);
        Assert.assertEquals(unknownHostResource.getContent().length, 0);
        Assert.assertEquals(unknownHostResource.getContentType(), "application/empty-response");
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
}
