package com.applitools.eyes.selenium;

import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public final class TestHelloWorld {

    @Test(dataProvider = "booleanDP", dataProviderClass = TestDataProvider.class)
    public void HelloWorldTest(boolean useVisualGrid)
    {
        WebDriver webDriver = SeleniumUtils.createChromeDriver();
        webDriver.get("https://applitools.com/helloworld");

        EyesRunner runner = useVisualGrid ? new VisualGridRunner(10) : new ClassicRunner();

        runner.setLogHandler(TestUtils.initLogger());
        Eyes eyes = new Eyes(runner);

        Configuration sconf = eyes.getConfiguration();

        String suffix = useVisualGrid ? "_VG" : "";
        // Set test name
        sconf.setTestName("Hello World Demo" + suffix);

        // Set app name
        sconf.setAppName("Hello World Demo");

        // Add browsers
        sconf.addBrowser(800, 600, BrowserType.CHROME);
        sconf.addBrowser(700, 500, BrowserType.FIREFOX);
        sconf.addBrowser(1200, 800, BrowserType.IE_10);
        sconf.addBrowser(1200, 800, BrowserType.IE_11);
        sconf.addBrowser(1600, 1200, BrowserType.EDGE);

        // Set the configuration object to eyes
        eyes.setConfiguration(sconf);

        try
        {
            // Call Open on eyes to initialize a test session
            eyes.open(webDriver);

            // Add 2 checks
            eyes.check(Target.window().withName("Step 1 - Viewport").ignore(By.cssSelector(".primary")));
            eyes.check(Target.window().fully().withName("Step 1 - Full Page")
                    .floating(By.cssSelector(".primary"), 10, 20, 30, 40)
                    .floating(By.tagName("button"), 1, 2, 3, 4));

            webDriver.findElement(By.tagName("button")).click();

            // Add 2 checks
            eyes.check(Target.window().withName("Step 2 - Viewport"));
            eyes.check(Target.window().fully().withName("Step 2 - Full Page"));

            // Close eyes and collect results
            eyes.close();
        }
        finally
        {
            eyes.abort();
            webDriver.quit();
        }
    }
}
