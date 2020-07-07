package com.applitools.eyes.demo;

import com.applitools.eyes.*;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class BasicDemoJunit extends ReportingTestSuite {

    private final boolean useVisualGrid;
    private WebDriver driver;
    private final LogHandler logger = new StdoutLogHandler(false);

    public BasicDemoJunit(boolean useVisualGrid) {
        this.useVisualGrid = useVisualGrid;
        super.setGroupName("selenium");
    }

    @Parameterized.Parameters
    public static Collection useVisualGrid() {
        return Arrays.asList(true, false);
    }

    @Before
    public void beforeEach() {
        driver = SeleniumUtils.createChromeDriver();
    }

    @Test
    public void basicDemo() {
        EyesRunner runner = useVisualGrid ? new VisualGridRunner(10) : new ClassicRunner();
        String suffix = useVisualGrid ? "_VG" : "";
        Eyes eyes = new Eyes(runner);
        eyes.setLogHandler(logger);
        try {
            eyes.open(driver, "Demo App", "BasicDemoJunit" + suffix, new RectangleSize(800, 800));

            // Navigate the browser to the "ACME" demo app.
            driver.get("https://demo.applitools.com");

            // To see visual bugs after the first run, use the commented line below instead.
            //driver.get("https://demo.applitools.com/index_v2.html");

            eyes.checkWindow("Login Window");
            driver.findElement(By.id("log-in")).click();
            eyes.checkWindow("App Window");
            eyes.closeAsync();
        } finally {
            eyes.abortAsync();
            driver.quit();
            TestResultsSummary allTestResults = runner.getAllTestResults();
            System.out.println(allTestResults);
        }
    }
}