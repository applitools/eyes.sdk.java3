package com.applitools.eyes.visualgrid.model;

import com.applitools.eyes.utils.ReportingTestSuite;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestDeviceInfo extends ReportingTestSuite {

    public TestDeviceInfo() {
        super.setGroupName("core");
    }

    @Test
    public void testIosDeviceInfo() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        IosDeviceInfo iosDeviceInfo = new IosDeviceInfo(IosDeviceName.iPhone_7);
        JsonNode node = objectMapper.readTree(objectMapper.writeValueAsString(iosDeviceInfo));
        Assert.assertEquals(node.size(), 3);
        Assert.assertEquals(node.get("name").asText(), "iPhone 7");
        Assert.assertEquals(node.get("screenOrientation").asText(), "portrait");

        iosDeviceInfo = new IosDeviceInfo(IosDeviceName.iPhone_11, ScreenOrientation.LANDSCAPE);
        node = objectMapper.readTree(objectMapper.writeValueAsString(iosDeviceInfo));
        Assert.assertEquals(node.size(), 3);
        Assert.assertEquals(node.get("name").asText(), "iPhone 11");
        Assert.assertEquals(node.get("screenOrientation").asText(), "landscape");

        iosDeviceInfo = new IosDeviceInfo(IosDeviceName.iPhone_8, IosVersion.ONE_VERSION_BACK);
        node = objectMapper.readTree(objectMapper.writeValueAsString(iosDeviceInfo));
        Assert.assertEquals(node.size(), 4);
        Assert.assertEquals(node.get("name").asText(), "iPhone 8");
        Assert.assertEquals(node.get("screenOrientation").asText(), "portrait");
        Assert.assertEquals(node.get("version").asText(), "latest-1");

        iosDeviceInfo = new IosDeviceInfo(IosDeviceName.iPhone_X, ScreenOrientation.LANDSCAPE, IosVersion.LATEST);
        node = objectMapper.readTree(objectMapper.writeValueAsString(iosDeviceInfo));
        Assert.assertEquals(node.size(), 4);
        Assert.assertEquals(node.get("name").asText(), "iPhone X");
        Assert.assertEquals(node.get("screenOrientation").asText(), "landscape");
        Assert.assertEquals(node.get("version").asText(), "latest");
    }
}
