package com.applitools.eyes.unit;

import com.applitools.eyes.StitchOverlap;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.universal.dto.ConfigurationDto;
import com.applitools.eyes.universal.mapper.ConfigurationMapper;
import com.applitools.eyes.utils.ReportingTestSuite;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestConfigurationMapper extends ReportingTestSuite {

    @BeforeClass
    public void setup() {
        super.setGroupName("core");
    }

    @Test
    public void testConfigurationMapping() {
        Configuration config = new Configuration();

        config.setAppName("appName");
        config.setTestName("testName");

        config.setDeviceInfo("deviceInfo");
        config.setHostApp("hostApp");
        config.setOsInfo("osInfo");
        config.setHostingAppInfo("hostingAppInfo");
        config.setHostOS("hostOs");

        config.addProperty("customPropertyName", "customPropertyValue");

        config = new Configuration(config);
        ConfigurationDto dto = ConfigurationMapper.toConfigurationDto(config, null);

        Assert.assertEquals(dto.getAppName(), "appName");
        Assert.assertEquals(dto.getTestName(), "testName");

        Assert.assertEquals(dto.getDeviceInfo(), "deviceInfo");
        Assert.assertEquals(dto.getHostApp(), "hostApp");
        Assert.assertEquals(dto.getHostOSInfo(), "osInfo");
        Assert.assertEquals(dto.getHostAppInfo(), "hostingAppInfo");
        Assert.assertEquals(dto.getHostOS(), "hostOs");

        Assert.assertEquals(dto.getProperties().get(0).getName(), "customPropertyName");
        Assert.assertEquals(dto.getProperties().get(0).getValue(), "customPropertyValue");

        Assert.assertNull(dto.getDontCloseBatches());
    }

    @Test
    public void testStitchOverlap() {
        Configuration config = new Configuration();

        config.setStitchOverlap(10);

        config = new Configuration(config);
        ConfigurationDto dto = ConfigurationMapper.toConfigurationDto(config, null);

        Assert.assertNull(dto.getStitchOverlap().getTop());
        Assert.assertEquals((int) dto.getStitchOverlap().getBottom(), 10);

        // new api
        config.setStitchOverlap(new StitchOverlap());

        config = new Configuration(config);
        dto = ConfigurationMapper.toConfigurationDto(config, null);

        Assert.assertNull(dto.getStitchOverlap().getTop());
        Assert.assertNull(dto.getStitchOverlap().getBottom());

        config.setStitchOverlap(new StitchOverlap().top(10));

        config = new Configuration(config);
        dto = ConfigurationMapper.toConfigurationDto(config, null);

        Assert.assertEquals((int) dto.getStitchOverlap().getTop(), 10);
        Assert.assertNull(dto.getStitchOverlap().getBottom());

        config.setStitchOverlap(new StitchOverlap(10, 10));

        config = new Configuration(config);
        dto = ConfigurationMapper.toConfigurationDto(config, null);

        Assert.assertEquals((int) dto.getStitchOverlap().getTop(), 10);
        Assert.assertEquals((int) dto.getStitchOverlap().getBottom(), 10);
    }
}
