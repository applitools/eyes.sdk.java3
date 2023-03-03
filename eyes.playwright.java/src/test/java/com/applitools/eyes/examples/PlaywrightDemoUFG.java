package com.applitools.eyes.examples;

import com.applitools.eyes.TestResultContainer;
import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.playwright.visualgrid.VisualGridRunner;
import com.applitools.eyes.visualgrid.BrowserType;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.playwright.Eyes;
import com.applitools.eyes.playwright.fluent.Target;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.testng.annotations.*;

import java.util.Arrays;

public class PlaywrightDemoUFG {

    // Shared between all tests in this class.
    private static Playwright playwright;
    private static Browser browser;

    // New instance for each test method.
    private BrowserContext context;
    private Page page;
    private Eyes eyes;
    private VisualGridRunner runner;


    @BeforeTest
    public void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();

        runner = new VisualGridRunner(new RunnerOptions().testConcurrency(5));
        eyes = new Eyes(runner);
        setUFGConfiguration(eyes);
        eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));
    }

    private void setUFGConfiguration(Eyes eyes) {
        eyes.setConfiguration(eyes.getConfiguration()
                .addBrowser(1400, 700, BrowserType.CHROME)
                .addBrowser(1200, 900, BrowserType.FIREFOX)
        );
    }

    @BeforeMethod
    public void beforeEach() {
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterMethod
    public void afterEach() {
        context.close();
    }

    @AfterTest
    public void teardown() {
        playwright.close();

        TestResultsSummary testResultsSummary = runner.getAllTestResults(true);
        TestResultContainer[] results = testResultsSummary.getAllResults();
        System.out.println(Arrays.toString(results));
    }

    @Test
    public void test() {
        page.navigate("https://demo.applitools.com");

        eyes.open(page, "Playwright Java", "Test Playwright Java with the UFG"
                , new RectangleSize(1400, 700));
        eyes.check(Target.window().fully(true));
        eyes.closeAsync();
    }


}