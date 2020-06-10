package com.applitools.eyes.selenium;

import com.applitools.eyes.IEyesBase;
import com.applitools.eyes.Logger;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.metadata.ActualAppOutput;
import com.applitools.eyes.metadata.SessionResults;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import com.applitools.eyes.utils.CommunicationUtils;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Comparator;

@Listeners(TestListener.class)
public final class TestSendDom {

    @BeforeClass(alwaysRun = true)
    public void OneTimeSetUp() {
        if (TestUtils.runOnCI && System.getenv("TRAVIS") != null) {
            System.setProperty("webdriver.chrome.driver", "/home/travis/build/chromedriver"); // for travis build.
        }
    }

    private interface WebDriverInitializer {
        void initWebDriver(WebDriver webDriver);
    }

    private static void captureDom(String url, String testName) {
        captureDom(url, null, testName);
    }

    private static void captureDom(String url, WebDriverInitializer initCode, String testName) {
        WebDriver webDriver = SeleniumUtils.createChromeDriver();
        webDriver.get(url);
        Logger logger = new Logger();

        logger.setLogHandler(TestUtils.initLogger(testName));
        if (initCode != null) {
            initCode.initWebDriver(webDriver);
        }
        Eyes eyes = new Eyes();
        try {
            eyes.setBatch(TestDataProvider.batchInfo);
            EyesWebDriver eyesWebDriver = (EyesWebDriver) eyes.open(webDriver, "Test Send DOM", testName);
            //DomCapture domCapture = new DomCapture(logger, eyesWebDriver);
            eyes.checkWindow();
            TestResults results = eyes.close(false);
            boolean hasDom = getHasDom(eyes, results);
            Assert.assertTrue(hasDom);
            //string actualDomJsonString = domCapture.GetFullWindowDom();
            //WriteDomJson(logger, actualDomJsonString);

        } catch (Exception ex) {
            GeneralUtils.logExceptionStackTrace(eyes.getLogger(), ex);
            throw ex;
        } finally {
            eyes.abort();
            webDriver.quit();
        }
    }


    class DomInterceptingEyes extends SeleniumEyes {
        private String domJson;

        public DomInterceptingEyes() {
            super(new ISeleniumConfigurationProvider() {
                private Configuration configuration = new Configuration();

                @Override
                public IConfigurationGetter get() {
                    return configuration;
                }

                @Override
                public IConfigurationSetter set() {
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
        private Logger logger;
        public DiffPrintingNotARealComparator(Logger logger) {
            this.logger = logger;
        }

        @Override
        public int compare(JsonNode o1, JsonNode o2) {
            if (o1 == null) {
                logger.log(String.format("O1 IS NULL! o2: %s, parent: %s", o2, lastObject));
            }

            if (o2 == null) {
                logger.log(String.format("O2 IS NULL! o1: %s, parent: %s", o1, lastObject));
            }

            if (!o1.equals(o2)) {
                logger.log(String.format("JSON diff found! Parent: %s, o1: %s , o2: %s", lastObject, o1, o2));
                return 1;
            }
            lastObject = o1;
            return 0;
        }
    }

    @Test
    public void TestSendDOM_FullWindow() {
        WebDriver webDriver = SeleniumUtils.createChromeDriver();
        webDriver.get("https://applitools.github.io/demo/TestPages/FramesTestPage/");
        DomInterceptingEyes eyes = new DomInterceptingEyes();
        eyes.setBatch(TestDataProvider.batchInfo);
        eyes.getConfigSetter().setAppName("Test Send DOM").setTestName("Full Window").setViewportSize(new RectangleSize(1024, 768));
        EyesWebDriver eyesWebDriver = (EyesWebDriver) eyes.open(webDriver);
        try {
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
                //noinspection SimplifiedTestNGAssertion
                if (actual == null) {
                    eyes.getLogger().log("ACTUAL DOM IS NULL!");
                }
                if (expected == null) {
                    eyes.getLogger().log("EXPECTED DOM IS NULL!");
                }
                Assert.assertTrue(actual.equals(new DiffPrintingNotARealComparator(eyes.getLogger()), expected));

                SessionResults sessionResults = TestUtils.getSessionResults(eyes.getApiKey(), results);
                ActualAppOutput[] actualAppOutput = sessionResults.getActualAppOutput();
                String downloadedDomJsonString = TestUtils.getStepDom(eyes, actualAppOutput[0]);
                JsonNode downloaded = mapper.readTree(downloadedDomJsonString);
                //noinspection SimplifiedTestNGAssertion
                if (downloaded == null) {
                    eyes.getLogger().log("Downloaded DOM IS NULL!");
                }
                Assert.assertTrue(downloaded.equals(expected));

            } catch (IOException e) {
                GeneralUtils.logExceptionStackTrace(eyes.getLogger(), e);
            }
        } finally {
            eyes.abort();
            webDriver.quit();
        }
    }

    @Test
    public void TestSendDOM_Selector() {
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
    public void TestNotSendDOM() {
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
    public void TestSendDOM_1() {
        captureDom("https://applitools.github.io/demo/TestPages/DomTest/dom_capture.html", "TestSendDOM_1");
    }

    @Test
    public void TestSendDOM_2() {
        captureDom("https://applitools.github.io/demo/TestPages/DomTest/dom_capture_2.html", "TestSendDOM_2");
    }

    private static boolean getHasDom(IEyesBase eyes, TestResults results) {
        SessionResults sessionResults = null;
        try {
            sessionResults = TestUtils.getSessionResults(eyes.getApiKey(), results);
        } catch (IOException e) {
            GeneralUtils.logExceptionStackTrace(eyes.getLogger(), e);
        }
        ActualAppOutput[] actualAppOutputs = sessionResults.getActualAppOutput();
        Assert.assertEquals(actualAppOutputs.length, 1);
        boolean hasDom = actualAppOutputs[0].getImage().getHasDom();
        return hasDom;
    }
}
