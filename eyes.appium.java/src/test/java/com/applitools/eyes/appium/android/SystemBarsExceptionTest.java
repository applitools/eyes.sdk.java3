package com.applitools.eyes.appium.android;

import com.applitools.eyes.Logger;
import com.applitools.eyes.appium.Eyes;
import com.applitools.eyes.appium.EyesAppiumDriver;
import com.applitools.eyes.appium.EyesAppiumUtils;
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

public class SystemBarsExceptionTest extends ReportingTestSuite {

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
    }

    @Test
    public void testSystemBars() {
        EyesAppiumDriver eyesDriver = new EyesAppiumDriver(new Logger(), new Eyes(), remoteWebDriver);

        Map<String, Integer> systemBars = EyesAppiumUtils.getSystemBarsHeights(eyesDriver);

        Assert.assertEquals(systemBars.get("statusBar").intValue(), 72);
        Assert.assertEquals(systemBars.get("navigationBar").intValue(), 140);
    }
}
