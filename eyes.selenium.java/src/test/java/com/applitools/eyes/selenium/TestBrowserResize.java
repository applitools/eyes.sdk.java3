package com.applitools.eyes.selenium;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.metadata.ActualAppOutput;
import com.applitools.eyes.metadata.SessionResults;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

public class TestBrowserResize {

    @Test
    public void BrowserSizeTest() {
        WebDriver webDriver = SeleniumUtils.createChromeDriver();
        webDriver.get("https://applitools.github.io/demo/TestPages/DynamicResolution/desktop.html");
        Eyes eyes = new Eyes();
        eyes.setBatch(TestDataProvider.batchInfo);
        try {
            eyes.open(webDriver, "Browser Size Test", "Browser Size Test", new RectangleSize(640, 480));
            eyes.check("Test 1", Target.window());
            TestResults results1 = eyes.close(false);
            SessionResults sessionResults1 = TestUtils.getSessionResults(eyes.getApiKey(), results1);

            eyes.open(webDriver, "Browser Size Test", "Browser Size Test", new RectangleSize(800, 600));
            eyes.check("Test 2", Target.window());
            TestResults results2 = eyes.close(false);
            SessionResults sessionResults2 = TestUtils.getSessionResults(eyes.getApiKey(), results2);

            eyes.open(webDriver, "Browser Size Test", "Browser Size Test", new RectangleSize(1024, 768));
            eyes.check("Test 3", Target.window());
            TestResults results3 = eyes.close(false);
            SessionResults sessionResults3 = TestUtils.getSessionResults(eyes.getApiKey(), results3);

            Assert.assertNotNull(sessionResults1);
            ActualAppOutput[] actualAppOutput1 = sessionResults1.getActualAppOutput();
            Assert.assertEquals(actualAppOutput1.length, 1);
            Assert.assertEquals(actualAppOutput1[0].getImage().getSize(), new RectangleSize(640, 480));

            Assert.assertNotNull(sessionResults2);
            ActualAppOutput[] actualAppOutput2 = sessionResults2.getActualAppOutput();
            Assert.assertEquals(actualAppOutput2.length, 1);
            Assert.assertEquals(actualAppOutput2[0].getImage().getSize(), new RectangleSize(800, 600));

            Assert.assertNotNull(sessionResults3);
            ActualAppOutput[] actualAppOutput3 = sessionResults3.getActualAppOutput();
            Assert.assertEquals(actualAppOutput3.length, 1);
            Assert.assertEquals(actualAppOutput3[0].getImage().getSize(), new RectangleSize(1024, 768));
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            webDriver.quit();
            eyes.abort();
        }
    }
}
