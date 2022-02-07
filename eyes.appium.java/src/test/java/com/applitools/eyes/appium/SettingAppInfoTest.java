package com.applitools.eyes.appium;

import com.applitools.eyes.AppEnvironment;
import com.applitools.eyes.appium.android.AndroidTestSetup;
import com.applitools.eyes.appium.ios.IOSTestSetup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.testng.AssertJUnit.assertEquals;

@RunWith(Parameterized.class)
public class SettingAppInfoTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {new AndroidTest(), "google pixel 3"},
                {new IosTest(), "iPhone 8"}
        });
    }

    @Parameterized.Parameter
    public TestSetup testSetup;

    @Parameterized.Parameter(1)
    public String expectedDeviceInfo;

    @Before
    public void setUp() {
        testSetup.beforeClass();
    }

    @Test
    public void testSettingAppInfo() {
        testSetup.eyes.open(testSetup.driver, testSetup.getApplicationName(), "Test setting appInfo");

        AppEnvironment appEnvironment = testSetup.eyes.getAppEnvironment();
        assertEquals(appEnvironment.getDeviceInfo(), expectedDeviceInfo);

        testSetup.eyes.close();
    }

    @After
    public void tearDown() throws Exception {
        testSetup.afterClass();
    }

    private static class AndroidTest extends AndroidTestSetup {
    }

    private static class IosTest extends IOSTestSetup {
    }
}
