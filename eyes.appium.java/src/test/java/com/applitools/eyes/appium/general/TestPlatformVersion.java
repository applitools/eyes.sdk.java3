package com.applitools.eyes.appium.general;

import com.applitools.eyes.selenium.EyesDriverUtils;
import com.applitools.eyes.utils.ReportingTestSuite;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.util.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

public class TestPlatformVersion extends ReportingTestSuite {

    @DataProvider(name = "capabilities")
    public static Object[][] capabilities() {
        return new Object[][] {
                { "platformVersion", "13.0" },
                { "os_version", "13.0" }
        };
    }

    @BeforeClass
    public void beforeClass() {
        super.setGroupName("appium");
    }

    @Test(dataProvider = "capabilities")
    public void testPlatformVersion(String capabilityName, String version) {
        super.addSuiteArg("capabilityName", capabilityName);
        super.addSuiteArg("version", version);
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(capabilityName, version);

        AppiumDriver remoteWebDriver = mock(AppiumDriver.class);
        when(remoteWebDriver.getCapabilities()).thenReturn(capabilities);

        Assert.notNull(EyesDriverUtils.getPlatformVersion(remoteWebDriver), "Could not parse platform version");
    }
}
