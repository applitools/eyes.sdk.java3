package com.applitools.eyes.examples;

import com.applitools.eyes.*;
import com.applitools.eyes.visualgrid.BrowserType;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.playwright.ClassicRunner;
import com.applitools.eyes.playwright.Eyes;
import com.applitools.eyes.playwright.fluent.Target;
import com.applitools.eyes.playwright.visualgrid.VisualGridRunner;
import com.applitools.eyes.selenium.StitchMode;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.microsoft.playwright.*;

import java.util.Arrays;
import java.util.stream.Collectors;

class DocsExamples {

    private Eyes eyes;
    private Page page;

    public void testUsage() {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch();
        BrowserContext context = browser.newContext();
        Page page = context.newPage();
        page.navigate("https://applitools.com");

        Eyes eyes = new Eyes();
        eyes.open(page, "applitools.com website", "My first Playwright test!");
        eyes.check("home page", Target.window().fully());

        eyes.close();
    }

    public void etc() {
        // region by selector
        eyes.check(Target.region(".banner"));

        // region by locator
        Locator locator = page.locator(".banner");
        eyes.check(Target.region(locator));

        // region by element
        ElementHandle el = page.locator(".banner").elementHandle();
        eyes.check(Target.region(el));

        // region by coordinates
        eyes.check(
                Target.region(new Region(10, 20, 200, 80))
        );

        // element inside frame
        eyes.check(Target.frame("frame-1").region(".element-inside frame"));

        // entire frame
        eyes.check(Target.frame("frame-1").fully());

        // element inside nested frames
        eyes.check(
                Target.frame("frame-1").frame("frame-1-1").region(".nested-element")
        );

        // single region
        eyes.check("viewport screenshot with ignore region",
                Target.window().ignore(".dynamic-content-here")
        );

        // multiple regions
        eyes.check("viewport screenshot with ignore region",
                Target.window()
                        .ignore(".dynamic-content-here")
                        .ignore(page.locator("selector")) // some Locator
                        .ignore(page.locator("selector").elementHandle()) // some ElementHandle
                        .ignore(new Region(10, 20, 30, 40))
        );

        // viewport screenshot with floating region
        eyes.check(
                Target
                        .window()
                        .floating(".floating-are", 10, 10, 10, 10) // up, down, left, right

        );

        // multiple regions
        eyes.check(
                Target
                        .window()
                        .floating(page.locator(".floating-area"), 10, 10, 10, 10) // Playwright Locator
                        .floating(10, ".floating-area")
                        .floating(10, page.locator(".floating-area")) // up, down, left, right all equal to 10
                        .floating(10, page.locator(".floating-area").elementHandle()) // up, down, left, right all equal to 10
                        .floating(10, new Region(10, 20, 30, 40))
                        .floating(new Region(10, 20, 30, 40), 10, 10, 10, 10)
        );

        // viewport screenshot with content region
        eyes.check(Target.window().content(".some-element"));

        // viewport screenshot with strict region
        eyes.check(Target.window().strict(".some-element"));

        // viewport screenshot with layout region
        eyes.check(Target.window().layout(".some-element"));

        // multiple regions
//        Target.window().content(region1).content(region2).content(region3);
//        Target.window().strict(region1).strict(region2).strict(region3);
//        Target.window().layout(region1).layout(region2).layout(region3);

        // viewport screenshot with accessibility region
        eyes.check(
                Target.window().accessibility(".some-element", AccessibilityRegionType.LargeText)
        );

        // multiple regions is done by chaining the same method
        eyes.check(
                Target.window()
                        .accessibility(".element-1", AccessibilityRegionType.LargeText)
                        .accessibility(".element-2", AccessibilityRegionType.IgnoreContrast)
                        .accessibility(".element-3", AccessibilityRegionType.BoldText)
        );

        // full page with custom scroll root element
        eyes.check(
                Target.window().fully().scrollRootElement(".main-content")
        );

        // Tag
        eyes.check(Target.window().withName("Main page"));

        // lazy loads with sensible defaults
        eyes.check(Target.window().lazyLoad());

        // lazy loads with options specified
        eyes.check(Target.window().lazyLoad(new LazyLoadOptions()
                .maxAmountToScroll(1000)   // total pixels of the page to be scrolled
                .scrollLength(250)  // amount of pixels to use for each scroll attempt
                .waitingTime(500)   // milliseconds to wait in-between each scroll attempt
        ));

        // close
        boolean throwEx = true;
        TestResults testResults = eyes.close(throwEx);

        // Runners
        ClassicRunner classicRunner = new ClassicRunner();
        VisualGridRunner visualGridRunner = new VisualGridRunner(new RunnerOptions().testConcurrency(5));

        // Use of Ultrafast grid
        Eyes eyes = new Eyes(new VisualGridRunner());
        Configuration configuration = eyes.getConfiguration();

        configuration
                .addBrowser(1400, 700, BrowserType.CHROME)
                .addBrowser(1200, 900, BrowserType.FIREFOX)
                .addBrowser(1200, 900, BrowserType.SAFARI)
                .addBrowser(1200, 900, BrowserType.EDGE_CHROMIUM)
                .addBrowser(1200, 900, BrowserType.IE_11)
        ;

        eyes.setConfiguration(configuration);
    }

    public void testWithGetAllTestResults() {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch();
        BrowserContext context = browser.newContext();
        Page page = context.newPage();
        page.navigate("https://applitools.com");

        VisualGridRunner runner = new VisualGridRunner(10);
        Eyes eyes = new Eyes(runner);
        eyes.open(page, "applitools.com website", "My first Playwright test!");
        eyes.check("home page", Target.window().fully());

        eyes.closeAsync();

        TestResultsSummary testResultSummary = runner.getAllTestResults();
        for (TestResultContainer testResultContainer: testResultSummary.getAllResults()) {
            TestResults testResults = testResultContainer.getTestResults();
            System.out.println(formatTestResults(testResults));
        }
    }

    public void commonTasks() {
        // server url
        eyes.setServerUrl("asd");

        // proxy
        eyes.setProxy(new ProxySettings("uri"));
        eyes.setProxy(new ProxySettings(
                "uri",
                1234, // port
                "username",
                "password"
        ));

        // batch
        BatchInfo batchInfo = new BatchInfo("name");
        batchInfo.setId("id");
        eyes.setBatch(batchInfo);

        // stitch mode
        eyes.setStitchMode(StitchMode.CSS);
        eyes.setStitchMode(StitchMode.SCROLL);

        // stitch overlap
        eyes.setStitchOverlap(60);

        // match level
        eyes.setMatchLevel(MatchLevel.LAYOUT);

        eyes.check(Target.window().layout());
        eyes.check(Target.window().strict());
        eyes.check(Target.window().content());
        eyes.check(Target.window().exact());

        // ignore displacements
        eyes.setIgnoreDisplacements(true);

        eyes.check(Target.window().ignoreDisplacements());

        // test properties
        Configuration configuration = eyes.getConfiguration();
        configuration.addProperty("my custom property", "some value");
        eyes.setConfiguration(configuration);
    }

    public String formatTestResults(TestResults testResults) {
        return "\n" +
                "Test name: " + testResults.getName() + "\n" +
                "Test status: " + testResults.getStatus() + "\n" +
                "URL to results: " + testResults.getUrl() + "\n" +
                "Total number of steps: " + testResults.getSteps() + "\n" +
                "Number of matching steps: " + testResults.getMatches() + "\n" +
                "Number of visual diffs: " + testResults.getMismatches() + "\n" +
                "Number of missing steps: " + testResults.getMissing() + "\n" +
                "Display size: " + testResults.getHostDisplaySize().toString() + "\n" +
                "Steps: " + Arrays.stream(testResults.getStepsInfo()).map(
                stepInfo -> "" + stepInfo.getName() + " - " + getStepStatus(stepInfo)
        ).collect(Collectors.toList());
    }

    public String getStepStatus(StepInfo stepInfo) {
        if (stepInfo.getIsDifferent()) {
            return "Diff";
        } else if (!stepInfo.getHasBaselineImage()) {
            return "New";
        } else if (!stepInfo.getHasCurrentImage()) {
            return "Missing";
        } else {
            return "Passed";
        }
    }
}