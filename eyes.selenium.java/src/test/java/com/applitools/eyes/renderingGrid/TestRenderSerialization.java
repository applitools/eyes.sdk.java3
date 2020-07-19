package com.applitools.eyes.renderingGrid;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.*;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.TestDomCapture;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.visualgrid.model.*;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.applitools.eyes.visualgrid.services.VisualGridTask;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;

public class TestRenderSerialization {

    @Test
    public void testRenderRequestSerialization() throws IOException {
        int width = 1600;
        int height = 1200;
        String target = "full-page";
        Region region = new Region(40, 50, 60, 70);
        EmulationBaseInfo emulationInfo = new ChromeEmulationInfo(DeviceName.Galaxy_S5, ScreenOrientation.PORTRAIT);
        RenderInfo renderInfo = new RenderInfo(width, height, target, region, null, emulationInfo, null);

        String xpath = "//html/body/some/path/to/some/element[@with:attribute]";
        Object category = "cat";
        List<VisualGridSelector> selectorsToFindRegionsFor = Collections.singletonList(new VisualGridSelector(xpath, category));

        URL webHook = new URL("https://some.uri.com");
        URL url = new URL("https://another.url.co.il");
        URL stitchingServiceUrl = new URL("https://some.stitchingserviceuri.com");
        RGridDom dom = new RGridDom();
        Map<String, RGridResource> resources = new HashMap<>();
        String platform = "android";
        BrowserType browserName = BrowserType.IE_10;
        VisualGridTask task = new VisualGridTask(VisualGridTask.TaskType.OPEN, null, null);
        RenderRequest request = new RenderRequest(webHook.toString(), url.toString(), dom, resources, renderInfo, platform,
                browserName, null, selectorsToFindRegionsFor, true, task, stitchingServiceUrl.toString());

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode actual = (ObjectNode) mapper.readTree(mapper.writeValueAsString(request));
        actual.remove("agentId");
        ObjectNode expected = (ObjectNode) mapper.readTree(GeneralUtils.readToEnd(TestDomCapture.class.getResourceAsStream("/renderRequest.json")));
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testRenderResultSerialization() {
        final List<MatchWindowData> matchWindowDataList = new ArrayList<>();
        final List<RenderStatusResults> renderStatusResults = new ArrayList<>();
        ServerConnector serverConnector = spy(ServerConnector.class);

        doAnswer(new Answer<MatchResult>() {
            @Override
            public MatchResult answer(InvocationOnMock invocationOnMock) throws Throwable {
                matchWindowDataList.add((MatchWindowData) invocationOnMock.getArgument(1));
                return (MatchResult) invocationOnMock.callRealMethod();
            }
        }).when(serverConnector).matchWindow(any(RunningSession.class), any(MatchWindowData.class));

        doAnswer(new Answer<List<RenderStatusResults>>() {
            @Override
            public List<RenderStatusResults> answer(InvocationOnMock invocation) throws Throwable {
                List<RenderStatusResults> results = (List<RenderStatusResults>) invocation.callRealMethod();
                for (RenderStatusResults result : results) {
                    if (result.getStatus().equals(RenderStatus.RENDERED)) {
                        renderStatusResults.add(result);
                    }
                }
                return results;
            }
        }).when(serverConnector).renderStatusById(anyString());

        EyesRunner runner = new VisualGridRunner(10);
        Eyes eyes = new Eyes(runner);
        eyes.setServerConnector(serverConnector);
        WebDriver driver = SeleniumUtils.createChromeDriver();
        driver.get("https://applitools.github.io/demo/TestPages/FramesTestPage/");
        try {
            eyes.open(driver, "Applitools Eyes SDK", "testRenderResultSerialization", new RectangleSize(800, 800));
            eyes.checkWindow();
            eyes.check(Target.window().fully());
            eyes.closeAsync();
        } finally {
            driver.quit();
            eyes.abortAsync();
            runner.getAllTestResults();
        }

        Assert.assertEquals(matchWindowDataList.size(), renderStatusResults.size());
        for (int i = 0; i < matchWindowDataList.size(); i++) {
            Assert.assertEquals(matchWindowDataList.get(0).getAppOutput().getViewport(), renderStatusResults.get(0).getVisualViewport());
        }
    }
}
