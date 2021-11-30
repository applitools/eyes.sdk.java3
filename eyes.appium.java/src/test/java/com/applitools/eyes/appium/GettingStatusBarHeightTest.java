package com.applitools.eyes.appium;

import com.applitools.eyes.Logger;
import com.applitools.eyes.utils.ReportingTestSuite;
import io.appium.java_client.AppiumDriver;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class GettingStatusBarHeightTest extends ReportingTestSuite {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {2d},
                {2L}
        });
    }

    private static Map<String, Object> sessionDetails;
    private static AppiumDriver driver;


    @Parameterized.Parameter
    public Object statusBarHeight;

    @BeforeClass
    public static void beforeClass() throws Exception {
        sessionDetails = new HashMap<>();

        driver = Mockito.mock(AppiumDriver.class);
        when(driver.getCapabilities().asMap()).thenReturn(sessionDetails);
    }

    @Before
    public void setUp() {
        sessionDetails.put("statBarHeight", statusBarHeight);
    }

    @Test
    public void testGettingStatusBarHeight() {
        EyesAppiumDriver eyesDriver = new EyesAppiumDriver(new Logger(), new Eyes(), driver);
        eyesDriver.getStatusBarHeight();
    }
}
