package com.applitools.eyes.selenium.rendering;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.*;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.TestDomCapture;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.visualgrid.model.*;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static org.mockito.Mockito.*;

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
        List<VisualGridOption> options = Collections.singletonList(new VisualGridOption("key", "value"));

        URL webHook = new URL("https://some.uri.com");
        URL url = new URL("https://another.url.co.il");
        URL stitchingServiceUrl = new URL("https://some.stitchingserviceuri.com");
        RGridDom dom = new RGridDom();
        Map<String, RGridResource> resources = new HashMap<>();
        String platform = "android";
        BrowserType browserName = BrowserType.IE_10;
        RenderRequest request = new RenderRequest(webHook.toString(), url.toString(), dom, resources, renderInfo, platform,
                browserName, null, selectorsToFindRegionsFor, true, "rendererId", "", stitchingServiceUrl.toString(), options);

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
        ServerConnector serverConnector = new ServerConnector() {
            @Override
            public void matchWindow(TaskListener<MatchResult> listener, MatchWindowData matchData) throws EyesException {
                matchWindowDataList.add(matchData);
                super.matchWindow(listener, matchData);
            }

            @Override
            public void renderStatusById(final TaskListener<List<RenderStatusResults>> listener, String... renderIds) {
                super.renderStatusById(new TaskListener<List<RenderStatusResults>>() {
                    @Override
                    public void onComplete(List<RenderStatusResults> results) {
                        for (RenderStatusResults result : results) {
                            if (result.getStatus().equals(RenderStatus.RENDERED)) {
                                renderStatusResults.add(result);
                            }
                        }
                        listener.onComplete(results);
                    }

                    @Override
                    public void onFail() {
                        listener.onFail();
                    }
                }, renderIds);
            }
        };

        EyesRunner runner = new VisualGridRunner(10);
        Eyes eyes = new Eyes(runner);
        eyes.setLogHandler(new StdoutLogHandler());
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
            Assert.assertEquals(matchWindowDataList.get(i).getAppOutput().getViewport(), renderStatusResults.get(i).getVisualViewport());
        }
    }
}