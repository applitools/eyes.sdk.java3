package com.applitools.eyes.selenium;

import com.applitools.eyes.IEyesBase;
import com.applitools.eyes.Logger;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.metadata.ActualAppOutput;
import com.applitools.eyes.metadata.SessionResults;
import com.applitools.eyes.selenium.capture.DomCapture;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import com.applitools.eyes.utils.CommUtils;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;

@Listeners(TestListener.class)
public final class TestSendDom {

    private interface WebDriverInitializer {
        void initWebDriver(WebDriver webDriver);
    }

    private static void captureDom(String url, String testName) {captureDom(url, null, testName);}

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

//    @Test
//    public void TestSendDOM_FullWindow() {
//        WebDriver webDriver = SeleniumUtils.createChromeDriver();
//        webDriver.get("https://applitools.github.io/demo/TestPages/FramesTestPage/");
//        DomInterceptingEyes eyes = new DomInterceptingEyes();
//        eyes.setBatch(TestDataProvider.batchInfo);
//        config.setAppName("Test Send DOM").setTestName("Full Window").setViewportSize(new RectangleSize(1024, 768));
//        EyesWebDriver eyesWebDriver = (EyesWebDriver) eyes.open(webDriver);
//        try {
//            eyes.check("Window", Target.window().fully());
//            String actualDomJsonString = eyes.getDomJson();
//
//            TestResults results = eyes.close(false);
//            boolean hasDom = getHasDom(eyes, results);
//            Assert.assertTrue(hasDom);
//            ObjectMapper mapper = new ObjectMapper();
//            try {
//                String expectedDomJson = GeneralUtils.readToEnd(TestSendDom.class.getResourceAsStream("/expected_dom1.json"));
//                JsonNode actual = mapper.readTree(actualDomJsonString);
//                JsonNode expected = mapper.readTree(expectedDomJson);
//                Assert.assertTrue(actual.equals(expected));
//            } catch (IOException e) {
//                GeneralUtils.logExceptionStackTrace(eyes.getLogger(), e);
//            }
//        } finally {
//            eyes.abort();
//            webDriver.quit();
//        }
//    }

//    //@Test
//    public void TestSendDOM_Simple_HTML() {
//        WebDriver webDriver = SeleniumUtils.createChromeDriver();
//        webDriver.get("https://applitools-dom-capture-origin-1.surge.sh/test.html");
//        Eyes eyes = new Eyes();
//        try {
//            EyesWebDriver eyesWebDriver = (EyesWebDriver) eyes.open(webDriver, "Test Send DOM", "Test DomCapture method", new RectangleSize(1200, 1000));
//            Logger logger = new Logger();
//            logger.setLogHandler(TestUtils.initLogger());
//            DomCapture domCapture = new DomCapture(logger, eyesWebDriver);
//            String actualDomJsonString = domCapture.getFullWindowDom();
//            String expectedDomJson = getExpectedDomFromUrl("https://applitools-dom-capture-origin-1.surge.sh/test.dom.json");
//
//            eyes.close(false);
//
//            Assert.assertEquals(actualDomJsonString, expectedDomJson);
//        } finally {
//            eyes.abort();
//            webDriver.quit();
//        }
//    }

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

    private String getExpectedDomFromUrl(String domUrl) {
        String expectedDomJsonString = CommUtils.getString(domUrl);
        return expectedDomJsonString;
    }
}
