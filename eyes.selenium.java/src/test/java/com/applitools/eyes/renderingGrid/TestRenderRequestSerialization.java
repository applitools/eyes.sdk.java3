package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.Region;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.TestDomCapture;
import com.applitools.eyes.visualgrid.model.*;
import com.applitools.eyes.visualgrid.services.VisualGridTask;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestRenderRequestSerialization {

    @Test
    public void testSerialization() throws IOException {
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
}
