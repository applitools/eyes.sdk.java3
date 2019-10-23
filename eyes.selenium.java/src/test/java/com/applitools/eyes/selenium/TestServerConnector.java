package com.applitools.eyes.selenium;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class TestServerConnector {

    @Test
    public void TestDelete() {
        WebDriver webDriver = SeleniumUtils.createChromeDriver();
        Eyes eyes = new Eyes();
        eyes.setBatch(TestDataProvider.batchInfo);
        try {
            WebDriver driver = eyes.open(webDriver,
                    "TestSessionConnector", "TestSessionConnector",
                    new RectangleSize(800, 600));

            driver.get("https://applitools.com/helloworld");

            eyes.check("Hello!", Target.window());

            TestResults results = eyes.close();

            results.delete();
        }
        finally {
            webDriver.quit();
            eyes.abort();
        }
    }

}
