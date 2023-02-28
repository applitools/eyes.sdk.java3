package com.applitools.eyes.unit;

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

        config = new Configuration(config);

        ConfigurationDto dto = ConfigurationMapper.toConfigurationDto(config, null);

        Assert.assertEquals(dto.getAppName(), "appName");
        Assert.assertEquals(dto.getTestName(), "testName");

        Assert.assertEquals(dto.getDeviceInfo(), "deviceInfo");
        Assert.assertEquals(dto.getHostApp(), "hostApp");
        Assert.assertEquals(dto.getHostOSInfo(), "osInfo");
        Assert.assertEquals(dto.getHostAppInfo(), "hostingAppInfo");
        Assert.assertEquals(dto.getHostOS(), "hostOs");

        Assert.assertNull(dto.getDontCloseBatches());
    }
}
