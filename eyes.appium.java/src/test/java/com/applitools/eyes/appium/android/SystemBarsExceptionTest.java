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

        remoteWebDriver = Mockito.mock(AndroidDriver.class);
        when(remoteWebDriver.getSessionDetails()).thenReturn(sessionDetails);

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
