package com.applitools.eyes.appium.general;

import com.applitools.eyes.Logger;
import com.applitools.eyes.appium.Eyes;
import com.applitools.eyes.appium.EyesAppiumDriver;
import com.applitools.eyes.config.Feature;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.utils.ReportingTestSuite;
import io.appium.java_client.android.AndroidDriver;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

public class TestPredefinedDeviceInfoFeature extends ReportingTestSuite {

    private static Map<String, Object> sessionDetails;
    private static AndroidDriver remoteWebDriver;

    @BeforeClass
    public static void beforeClass() {
        sessionDetails = new HashMap<>();

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("deviceName", "Samsung Galaxy S10");

        remoteWebDriver = Mockito.mock(AndroidDriver.class);
        when(remoteWebDriver.getSessionDetails()).thenReturn(sessionDetails);
        when(remoteWebDriver.getCapabilities()).thenReturn(capabilities);
        when(remoteWebDriver.getSystemBars()).thenThrow(NullPointerException.class);
    }

    @Before
    public void setUp() {
        sessionDetails.put("statBarHeight", 72L);
        sessionDetails.put("deviceScreenSize", "1080x1920");
        HashMap<String, Long> viewportRectMap = new HashMap<>();
        viewportRectMap.put("width", 1080L);
        viewportRectMap.put("height", 1708L);
        sessionDetails.put("viewportRect", viewportRectMap);
    }

    @Test
    public void testPredefinedDeviceInfo() {
        Eyes eyes = new Eyes();
        EyesAppiumDriver eyesDriver = new EyesAppiumDriver(new Logger(), eyes, remoteWebDriver);
        Assert.assertEquals(eyesDriver.getStatusBarHeight(), 72);
        Assert.assertEquals(eyesDriver.getViewportRect().get("height").intValue(), 1708);

        Configuration configuration = eyes.getConfiguration();
        configuration.setFeatures(Feature.USE_PREDEFINED_DEVICE_INFO);
        eyes.setConfiguration(configuration);

        Assert.assertEquals(eyesDriver.getStatusBarHeight(), 112);
        Assert.assertEquals(eyesDriver.getViewportRect().get("height").intValue(), 1930);
    }
}
