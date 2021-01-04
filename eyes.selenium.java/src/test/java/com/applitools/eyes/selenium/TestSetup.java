package com.applitools.eyes.selenium;

import com.applitools.eyes.*;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumTestUtils;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITest;
import org.testng.annotations.BeforeClass;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class TestSetup extends ReportingTestSuite implements ITest {

    private static String testNameSuffix = System.getenv("TEST_NAME_SUFFIX");
    public final String mode;

    private boolean useVisualGrid = false;
    StitchMode stitchMode = StitchMode.SCROLL;

    private EyesRunner runner = new AsyncClassicRunner();
    private Capabilities options;
    private String testName;

    public TestSetup(String testSuitName, Capabilities options, String mode) {
        if (testNameSuffix == null) testNameSuffix = "";
        this.testSuitName = testSuitName + testNameSuffix;
        this.options = options;
        this.mode = mode;
        super.setGroupName("selenium");
        switch (mode) {
            case "VG":
                this.useVisualGrid = true;
                super.addSuiteArg("mode", "VisualGrid");
                break;
            case "CSS":
                this.stitchMode = StitchMode.CSS;
                super.addSuiteArg("mode", "CSS");
                break;
            case "SCROLL":
                this.stitchMode = StitchMode.SCROLL;
                super.addSuiteArg("mode", "Scroll");
                break;
        }
    }

    class SpecificTestContextRequirements {

        private Eyes eyes;
        private WebDriver eyesDriver;
        private WebDriver webDriver;

        public HashSet<FloatingMatchSettings> expectedFloatingRegions = new HashSet<>();
        public HashSet<Region> expectedIgnoreRegions = new HashSet<>();
        public HashSet<Region> expectedLayoutRegions = new HashSet<>();
        public HashSet<Region> expectedStrictRegions = new HashSet<>();
        public HashSet<Region> expectedContentRegions = new HashSet<>();
        public Map<String, Object> expectedProperties = new HashMap<>();
        public HashSet<AccessibilityRegionByRectangle> expectedAccessibilityRegions = new HashSet<AccessibilityRegionByRectangle>();

        public SpecificTestContextRequirements(Eyes eyes) {
            this.eyes = eyes;
        }

        public Eyes getEyes() {
            return this.eyes;
        }

        public WebDriver getEyesDriver() {
            return this.eyesDriver;
        }

        public void setEyesDriver(WebDriver driver) {
            this.eyesDriver = driver;
        }

        public WebDriver getWebDriver() {
            return this.webDriver;
        }

        public void setWebDriver(WebDriver driver) {
            this.webDriver = driver;
        }
    }

    private Map<Object, SpecificTestContextRequirements> testDataByTestId = new ConcurrentHashMap<>();

    protected String testSuitName;

    protected String testedPageUrl = "https://applitools.github.io/demo/TestPages/FramesTestPage/";
    //protected RectangleSize testedPageSize = new RectangleSize(1200, 800);
    protected RectangleSize testedPageSize = new RectangleSize(700, 460);

    protected boolean compareExpectedRegions = true;

    protected String platform;
    protected boolean forceFPS;

    EyesRunner getRunner() {
        return this.runner;
    }

    @BeforeClass(alwaysRun = true)
    public void OneTimeSetUp() {
        if (TestUtils.runOnCI && System.getenv("TRAVIS") != null) {
            System.setProperty("webdriver.chrome.driver", "/home/travis/build/chromedriver"); // for travis build.
        }

        String batchId = System.getenv("APPLITOOLS_BATCH_ID");
        if (batchId != null) {
            TestDataProvider.batchInfo.setId(batchId);
        }

        this.runner = this.useVisualGrid ?
                new VisualGridRunner(10, testSuitName) :
                new AsyncClassicRunner(new RunnerOptions().testConcurrency(50));
    }

    public SpecificTestContextRequirements getTestData() {
        return this.testDataByTestId.get(Thread.currentThread().getId());
    }

    public WebDriver getDriver() {
        return getTestData().getEyesDriver();
    }

    protected WebDriver getWebDriver() {
        return getTestData().getWebDriver();
    }

    public Eyes getEyes() {
        return getTestData().getEyes();
    }

    protected void setExpectedIgnoreRegions(Region... expectedIgnoreRegions) {
        getTestData().expectedIgnoreRegions = new HashSet<>(Arrays.asList(expectedIgnoreRegions));
    }

    protected void setExpectedLayoutRegions(Region... expectedLayoutRegions) {
        getTestData().expectedLayoutRegions = new HashSet<>(Arrays.asList(expectedLayoutRegions));
    }

    protected void setExpectedStrictRegions(Region... expectedStrictRegions) {
        getTestData().expectedStrictRegions = new HashSet<>(Arrays.asList(expectedStrictRegions));
    }

    protected void setExpectedContentRegions(Region... expectedContentRegions) {
        getTestData().expectedContentRegions = new HashSet<>(Arrays.asList(expectedContentRegions));
    }

    protected void setExpectedFloatingRegions(FloatingMatchSettings... expectedFloatingsRegions) {
        getTestData().expectedFloatingRegions = new HashSet<>(Arrays.asList(expectedFloatingsRegions));
    }

    public void addExpectedProperty(String propertyName, Object expectedValue) {
        Map<String, Object> expectedProps = getTestData().expectedProperties;
        expectedProps.put(propertyName, expectedValue);
    }

    void beforeMethod(String testName) {
        // Initialize the eyes SDK and set your private API key.
        this.testName = testName + " " + options.getBrowserName() + " (" + this.mode + ")";
        Eyes eyes = initEyes();
        SpecificTestContextRequirements testData = new SpecificTestContextRequirements(eyes);
        testDataByTestId.put(Thread.currentThread().getId(), testData);

        if (this.runner instanceof VisualGridRunner) {
            testName += "_VG";
        } else if (this.stitchMode == StitchMode.SCROLL) {
            testName += "_Scroll";
        }

        RemoteWebDriver webDriver = null;
        String seleniumServerUrl = System.getenv("SELENIUM_SERVER_URL");
        try {
            if (seleniumServerUrl != null) {
                webDriver = new RemoteWebDriver(new URL(seleniumServerUrl), this.options);
            }
        } catch (MalformedURLException ignored) {
        }

        if (webDriver == null) {
            webDriver = (RemoteWebDriver) SeleniumUtils.createWebDriver(this.options);
        }

        eyes.addProperty("Selenium Session ID", webDriver.getSessionId().toString());

        eyes.addProperty("ForceFPS", eyes.getForceFullPageScreenshot() ? "true" : "false");
        eyes.addProperty("Agent ID", eyes.getFullAgentId());

        //IWebDriver webDriver = new RemoteWebDriver(new Uri("http://localhost:4444/wd/hub"), capabilities_);

        SeleniumTestUtils.setupLogging(eyes, testName + "_" + options.getPlatform());

        beforeOpen(eyes);

        WebDriver driver;
        try {
            driver = eyes.open(webDriver, this.testSuitName, testName, testedPageSize);
        } catch (Throwable e) {
            webDriver.quit();
            throw e;
        }

        driver.get(testedPageUrl);
        eyes.getLogger().log(new HashSet<String>(), Stage.GENERAL,
                Pair.of("testName", testName),
                Pair.of("batchName", TestDataProvider.batchInfo.getName()));

        if (useVisualGrid) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        testData.setEyesDriver(driver);
        testData.setWebDriver(webDriver);
    }

    private Eyes initEyes() {
        Eyes eyes = new Eyes(this.runner);
//        eyes.setLogHandler(TestUtils.initLogger());
        String serverUrl = System.getenv("APPLITOOLS_SERVER_URL");
        if (serverUrl != null && serverUrl.length() > 0) {
            eyes.setServerUrl(serverUrl);
        }

        eyes.setHideScrollbars(true);
        eyes.setStitchMode(this.stitchMode);
        eyes.setSaveNewTests(false);
        eyes.setBatch(TestDataProvider.batchInfo);
        if (System.getenv("APPLITOOLS_USE_PROXY") != null) {
            eyes.setProxy(new ProxySettings("http://127.0.0.1", 8888));
        }
        return eyes;
    }

    protected void beforeOpen(Eyes eyes) {
    }

    ;

    @Override
    public String getTestName() {
        return testName;
    }

    protected void setExpectedAccessibilityRegions(AccessibilityRegionByRectangle[] accessibilityRegions) {
        this.getTestData().expectedAccessibilityRegions = new HashSet<>(Arrays.asList(accessibilityRegions));
    }

    @Override
    public String toString() {
        return String.format("%s (%s, %s, force FPS: %s)",
                this.getClass().getSimpleName(),
                this.options.getBrowserName(),
                this.platform,
                this.forceFPS);
    }
}
