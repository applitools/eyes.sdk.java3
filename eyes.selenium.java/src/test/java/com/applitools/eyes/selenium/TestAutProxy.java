package com.applitools.eyes.selenium;

import com.applitools.eyes.AutProxyMode;
import com.applitools.eyes.AutProxySettings;
import com.applitools.eyes.EyesException;
import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestAutProxy {

    private WebDriver driver;
    private VisualGridRunner runner;

    @BeforeMethod
    public void setup() {
        String chromeDriverPath = System.getenv("CHROME_DRIVER_PATH");
        if(chromeDriverPath == null) throw new EyesException("CHROME_DRIVER_PATH missing");
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        driver = SeleniumUtils.createChromeDriver();
    }

    @AfterMethod
    public void teardown() {
        if (driver != null)
            driver.quit();
    }

    @Test
    private void shouldSucceedWithAutProxy() {
        runner = new VisualGridRunner();
        Eyes eyes = new Eyes(runner);

        try {

            Configuration configuration = new Configuration();
            AutProxySettings autProxySettings = new AutProxySettings(new ProxySettings("http://127.0.0.1", 8080));
            configuration.setAutProxy(autProxySettings);
            eyes.setConfiguration(configuration);

            eyes.open(driver, "AutProxyTest", "autProxy test");
            Assert.assertTrue(eyes.getIsOpen());
            eyes.close();
        } finally {
            eyes.abortIfNotClosed();
        }
    }

    @Test
    private void shouldSucceedWithAutProxyWithDomains() {
        runner = new VisualGridRunner();
        Eyes eyes = new Eyes(runner);
        try {

            Configuration configuration = new Configuration();
            AutProxySettings autProxySettings = new AutProxySettings(new ProxySettings("http://127.0.0.1", 8080),
                    new String[]{"google.com"});
            configuration.setAutProxy(autProxySettings);
            eyes.setConfiguration(configuration);

            driver.get("https://www.google.com");
            eyes.open(driver, "AutProxyTest", "autProxy with domains test");
            Assert.assertTrue(eyes.getIsOpen());
            eyes.close();
        } finally {
            eyes.abortIfNotClosed();
        }
    }

    @Test
    private void shouldSucceedWithAutProxyWithDomainsAndAutModeAllow() {
        runner = new VisualGridRunner();
        Eyes eyes = new Eyes(runner);
        try {

            Configuration configuration = new Configuration();
            AutProxySettings autProxySettings = new AutProxySettings(new ProxySettings("http://127.0.0.1", 8080),
                    new String[]{"google.com"}, AutProxyMode.ALLOW);
            configuration.setAutProxy(autProxySettings);
            eyes.setConfiguration(configuration);

            driver.get("https://www.google.com");
            eyes.open(driver, "AutProxyTest", "autProxy with domains and allow mode test");
            Assert.assertTrue(eyes.getIsOpen());
            eyes.close();
        } finally {
            eyes.abortIfNotClosed();
        }
    }

    @Test
    private void shouldSucceedWithAutProxyWithDomainsAndAutModeBlock() {
        runner = new VisualGridRunner();
        Eyes eyes = new Eyes(runner);
        try {

            Configuration configuration = new Configuration();
            AutProxySettings autProxySettings = new AutProxySettings(new ProxySettings("http://127.0.0.1", 8080),
                    new String[]{"google.com"}, AutProxyMode.BLOCK);
            configuration.setAutProxy(autProxySettings);
            eyes.setConfiguration(configuration);

            driver.get("https://www.google.com");
            eyes.open(driver, "AutProxyTest", "autProxy with domains and block mode test");
            Assert.assertTrue(eyes.getIsOpen());
            eyes.close();
        } finally {
            eyes.abortIfNotClosed();
        }
    }

    @Test
    private void shouldSucceedWithRunnerAutProxy() {
        runner = new VisualGridRunner();
        runner.setProxy(new ProxySettings("http://127.0.0.1", 8080));
        Eyes eyes = new Eyes(runner);

        try {

            eyes.open(driver, "AutProxyTest", "autProxy test");
            Assert.assertTrue(eyes.getIsOpen());
            eyes.close();
        } finally {
            eyes.abortIfNotClosed();
        }
    }

    @Test
    private void shouldSucceedWithRunnerAutProxyWithDomains() {
        RunnerOptions options = new RunnerOptions().autProxy(new ProxySettings("http://127.0.0.1", 8080),
                new String[]{"google.com"});
        runner = new VisualGridRunner(options);
        Eyes eyes = new Eyes(runner);
        try {

            eyes.open(driver, "AutProxyTest", "autProxy test");
            Assert.assertTrue(eyes.getIsOpen());
            eyes.close();
        } finally {
            eyes.abortIfNotClosed();
        }
    }

    @Test
    private void shouldSucceedWithRunnerAutProxyWithDomainsAndAutModeAllow() {
        RunnerOptions options = new RunnerOptions().autProxy(new ProxySettings("http://127.0.0.1", 8080),
                new String[]{"google.com"}, AutProxyMode.ALLOW);
        runner = new VisualGridRunner(options);
        Eyes eyes = new Eyes(runner);
        try {

            eyes.open(driver, "AutProxyTest", "autProxy test");
            Assert.assertTrue(eyes.getIsOpen());
            eyes.close();
        } finally {
            eyes.abortIfNotClosed();
        }
    }

    @Test
    private void shouldSucceedWithRunnerAutProxyWithDomainsAndAutModeBlock() {
        RunnerOptions options = new RunnerOptions().autProxy(new ProxySettings("http://127.0.0.1", 8080),
                new String[]{"google.com"}, AutProxyMode.ALLOW);
        Eyes eyes = new Eyes(runner);
        try {

            eyes.open(driver, "AutProxyTest", "autProxy test");
            Assert.assertTrue(eyes.getIsOpen());
            eyes.close();
        } finally {
            eyes.abortIfNotClosed();
        }
    }
}
