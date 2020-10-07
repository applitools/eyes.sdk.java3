package com.applitools.eyes.selenium;

import com.applitools.eyes.*;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.config.ConfigurationProvider;
import com.applitools.eyes.metadata.ActualAppOutput;
import com.applitools.eyes.metadata.SessionResults;
import com.applitools.eyes.selenium.capture.DomCapture;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Comparator;

public final class TestSendDom extends ReportingTestSuite {

    public TestSendDom() {
        super.setGroupName("selenium");
    }

    @BeforeClass(alwaysRun = true)
    public void OneTimeSetUp() {
        if (TestUtils.runOnCI && System.getenv("TRAVIS") != null) {
            System.setProperty("webdriver.chrome.driver", "/home/travis/build/chromedriver"); // for travis build.
        }
    }

    private static void captureDom(String url, String testName) throws IOException {
        WebDriver webDriver = SeleniumUtils.createChromeDriver();
        webDriver.get(url);
        Logger logger = new Logger();

        logger.setLogHandler(TestUtils.initLogger(testName));
        Eyes eyes = new Eyes();
        try {
            eyes.setBatch(TestDataProvider.batchInfo);
            eyes.open(webDriver, "Test Send DOM", testName);
            eyes.checkWindow();
            TestResults results = eyes.close(false);
            boolean hasDom = getHasDom(eyes, results);
            Assert.assertTrue(hasDom);
        } finally {
            eyes.abort();
            webDriver.quit();
        }
    }


    static class DomInterceptingEyes extends SeleniumEyes {
        private String domJson;

        public DomInterceptingEyes() {
            super(new ConfigurationProvider() {
                final Configuration configuration = new Configuration();

                @Override
                public Configuration get() {
                    return configuration;
                }
            }, new ClassicRunner());
        }

        public String getDomJson() {
            return domJson;
        }

        @Override
        public String tryCaptureDom() {
            this.domJson = super.tryCaptureDom();
            return this.domJson;
        }
    }

    // This is used for rhe
    public static class DiffPrintingNotARealComparator implements Comparator<JsonNode> {

        private JsonNode lastObject;
        private final Logger logger;
        public DiffPrintingNotARealComparator(Logger logger) {
            this.logger = logger;
        }

        @Override
        public int compare(JsonNode o1, JsonNode o2) {
            if (!o1.equals(o2)) {
                logger.log(String.format("JSON diff found! Parent: %s, o1: %s , o2: %s", lastObject, o1, o2));
                return 1;
            }
            lastObject = o1;
            return 0;
        }
    }

    @Test
    public void TestSendDOM_FullWindow() throws IOException {
        WebDriver webDriver = SeleniumUtils.createChromeDriver();
        webDriver.get("https://applitools.github.io/demo/TestPages/FramesTestPage/");
        DomInterceptingEyes eyes = new DomInterceptingEyes();
        eyes.setBatch(TestDataProvider.batchInfo);
        eyes.getConfigurationInstance().setAppName("Test Send DOM").setTestName("Full Window").setViewportSize(new RectangleSize(1024, 768));
        eyes.setLogHandler(new StdoutLogHandler());
        try {
            eyes.open(webDriver);
            eyes.check("Window", Target.window().fully());
            String actualDomJsonString = eyes.getDomJson();

            TestResults results = eyes.close(false);
            boolean hasDom = getHasDom(eyes, results);
            Assert.assertTrue(hasDom);
            ObjectMapper mapper = new ObjectMapper();
            try {
                String expectedDomJson = GeneralUtils.readToEnd(TestSendDom.class.getResourceAsStream("/expected_dom1.json"));
                JsonNode actual = mapper.readTree(actualDomJsonString);
                JsonNode expected = mapper.readTree(expectedDomJson);
                if (actual == null) {
                    Assert.fail("ACTUAL DOM IS NULL!");
                }
                if (expected == null) {
                    Assert.fail("EXPECTED DOM IS NULL!");
                }
                Assert.assertTrue(actual.equals(new DiffPrintingNotARealComparator(eyes.getLogger()), expected));

                SessionResults sessionResults = TestUtils.getSessionResults(eyes.getApiKey(), results);
                ActualAppOutput[] actualAppOutput = sessionResults.getActualAppOutput();
                String downloadedDomJsonString = TestUtils.getStepDom(eyes, actualAppOutput[0]);
                JsonNode downloaded = mapper.readTree(downloadedDomJsonString);
                if (downloaded == null) {
                    Assert.fail("Downloaded DOM IS NULL!");
                }
                Assert.assertTrue(downloaded.equals(new DiffPrintingNotARealComparator(eyes.getLogger()), expected));

            } catch (IOException e) {
                GeneralUtils.logExceptionStackTrace(eyes.getLogger(), e);
            }
        } finally {
            eyes.abortIfNotClosed();
            webDriver.quit();
        }
    }

    @Test
    public void TestSendDOM_Selector() throws IOException {
        WebDriver webDriver = SeleniumUtils.createChromeDriver();
        webDriver.get("https://applitools.github.io/demo/TestPages/DomTest/dom_capture.html");
        Eyes eyes = new Eyes();
        eyes.setBatch(TestDataProvider.batchInfo);
        try {
            eyes.open(webDriver, "Test SendDom", "Test SendDom", new RectangleSize(1000, 700));
            eyes.check("region", Target.region(By.cssSelector("#scroll1")));
            TestResults results = eyes.close(false);
            boolean hasDom = getHasDom(eyes, results);
            Assert.assertTrue(hasDom);
        } finally {
            eyes.abort();
            webDriver.quit();
        }
    }

    @Test
    public void TestNotSendDOM() throws IOException {
        WebDriver webDriver = SeleniumUtils.createChromeDriver();
        webDriver.get("https://applitools.com/helloworld");
        Eyes eyes = new Eyes();
        eyes.setBatch(TestDataProvider.batchInfo);
        eyes.setLogHandler(TestUtils.initLogger());
        eyes.setSendDom(false);
        try {
            eyes.open(webDriver, "Test NOT SendDom", "Test NOT SendDom", new RectangleSize(1000, 700));
            eyes.check("window", Target.window().sendDom(false));
            TestResults results = eyes.close(false);
            boolean hasDom = getHasDom(eyes, results);
            Assert.assertFalse(hasDom);
        } finally {
            eyes.abort();
            webDriver.quit();
        }
    }

    @Test
    public void TestSendDOM_1() throws IOException {
        captureDom("https://applitools.github.io/demo/TestPages/DomTest/dom_capture.html", "TestSendDOM_1");
    }

    @Test
    public void TestSendDOM_2() throws IOException {
        captureDom("https://applitools.github.io/demo/TestPages/DomTest/dom_capture_2.html", "TestSendDOM_2");
    }

    @Test
    public void TestCssFetching() throws IOException {
        WebDriver webDriver = SeleniumUtils.createChromeDriver();
        webDriver.get("https://applitools.github.io/demo/TestPages/CorsCssTestPage/");
        DomInterceptingEyes eyes = new DomInterceptingEyes();
        eyes.setLogHandler(new StdoutLogHandler());
        try {
            eyes.open(webDriver, "Test Send DOM", "TestCssFetching", new RectangleSize(700, 460));
            DomCapture domCapture = new DomCapture(eyes);
            String dom = domCapture.getPageDom(new NullPositionProvider());
            eyes.close();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode actual = objectMapper.readTree(dom);
            String expectedDomJson = GeneralUtils.readToEnd(TestSendDom.class.getResourceAsStream("/dom_cors_css.json"));
            JsonNode expected = objectMapper.readTree(expectedDomJson);
            Assert.assertTrue(actual.equals(new DiffPrintingNotARealComparator(eyes.getLogger()), expected));
        } finally {
            webDriver.quit();
        }
    }

    private static boolean getHasDom(IEyesBase eyes, TestResults results) throws IOException {
        SessionResults sessionResults = TestUtils.getSessionResults(eyes.getApiKey(), results);
        ActualAppOutput[] actualAppOutputs = sessionResults.getActualAppOutput();
        Assert.assertEquals(actualAppOutputs.length, 1);
        return actualAppOutputs[0].getImage().getHasDom();
    }
}
