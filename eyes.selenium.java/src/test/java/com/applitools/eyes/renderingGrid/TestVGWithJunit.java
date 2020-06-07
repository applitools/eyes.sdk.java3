package com.applitools.eyes.renderingGrid;// Generated by Selenium IDE

import com.applitools.eyes.FileLogger;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.TestDataProvider;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;
import java.util.Map;

public class TestVGWithJunit {
    private WebDriver driver;
    private Map<String, Object> vars;
    JavascriptExecutor js;
    private Eyes eyes;
    private VisualGridRunner runner;
    final int concurrency = 5;
    private String preRenderHook;
    @Before
    public void setUp() {
        driver = SeleniumUtils.createChromeDriver();
        js = (JavascriptExecutor) driver;
        vars = new HashMap<String, Object>();
        preRenderHook = "";
        runner = new VisualGridRunner(concurrency);
        eyes = new Eyes(runner);
        Configuration config = eyes.getConfiguration();
        eyes = new Eyes(runner);
        config.addBrowser(1920, 1080, BrowserType.CHROME);
        config.setBatch(TestDataProvider.batchInfo);
        eyes.setConfiguration(config);
        eyes.setLogHandler(new FileLogger("daves.log", true ,true));
        eyes.open(driver, "d", "Untitled");
    }
    @After
    public void tearDown() {
        driver.quit();
        eyes.close();
        runner.getAllTestResults();
    }
    @Test
    public void untitled() {
        driver.get("http://the-internet.herokuapp.com/");
        preRenderHook = "document.body.style.background = 'red'";
        eyes.check(Target.window().fully().beforeRenderScreenshotHook(preRenderHook));
        eyes.check(Target.window().region(By.cssSelector(".heading")).beforeRenderScreenshotHook(preRenderHook));
        preRenderHook = "";
        eyes.check(Target.window().fully().beforeRenderScreenshotHook(preRenderHook));
        eyes.check(Target.window().region(By.cssSelector(".heading")).beforeRenderScreenshotHook(preRenderHook));
    }
}