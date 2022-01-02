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
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.Response;
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
        Capabilities capabilities = new Capabilities() {
            @Override
            public Map<String, Object> asMap() {
                return sessionDetails;
            }

            @Override
            public Object getCapability(String capabilityName) {
                return sessionDetails.get(capabilityName);
            }
        };

        remoteWebDriver = Mockito.mock(AndroidDriver.class);
        Response response = new Response();
        response.setValue(sessionDetails);
        when(remoteWebDriver.getCapabilities()).thenReturn(capabilities);
        when(remoteWebDriver.execute("getSession")).thenReturn(response);

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
        sessionDetails.put("deviceName", "Samsung Galaxy S10");
    }

    @Test
    public void testPredefinedDeviceInfo() {
        Eyes eyes = new Eyes();
        EyesAppiumDriver eyesDriver = new EyesAppiumDriver(new Logger(), eyes, remoteWebDriver);
        Assert.assertEquals(eyesDriver.getStatusBarHeight(), 72);
        Assert.assertEquals(eyesDriver.getViewportRect().get("height").intValue(), 1708);
        Assert.assertEquals(eyesDriver.getViewportRect().get("width").intValue(), 1080);

        Configuration configuration = eyes.getConfiguration();
        configuration.setFeatures(Feature.USE_PREDEFINED_DEVICE_INFO);
        eyes.setConfiguration(configuration);

        Assert.assertEquals(eyesDriver.getStatusBarHeight(), 112);
        Assert.assertEquals(eyesDriver.getViewportRect().get("height").intValue(), 1930);
        Assert.assertEquals(eyesDriver.getViewportRect().get("width").intValue(), 1080);
    }
}
