package com.applitools.eyes.appium.general;

import com.applitools.eyes.selenium.EyesDriverUtils;
import com.applitools.eyes.utils.ReportingTestSuite;
import io.appium.java_client.AppiumDriver;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;

@RunWith(Parameterized.class)
public class TestDriverInstance extends ReportingTestSuite {

    @DataProvider(name = "data")
    public static Object[][] data() {
        return new Object[][] {
                {AppiumDriver.class, true},
                {WebDriver.class, false}
        };
    }

    @Test(dataProvider = "data")
    public void testDriver(Class clazz, boolean condition) {
        super.addSuiteArg("driverInstance", clazz);

        WebDriver webDriver = (WebDriver) mock(clazz);
        Assert.assertEquals(EyesDriverUtils.isMobileDevice(webDriver), condition);
    }
}
