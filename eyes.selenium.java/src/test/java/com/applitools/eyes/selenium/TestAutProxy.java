package com.applitools.eyes.selenium;

import com.applitools.eyes.AutProxyMode;
import com.applitools.eyes.AutProxySettings;
import com.applitools.eyes.EyesException;
import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.utils.SeleniumUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestAutProxy {

    private WebDriver driver;

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
        Eyes eyes = new Eyes();

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
        Eyes eyes = new Eyes();
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
        Eyes eyes = new Eyes();
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
        Eyes eyes = new Eyes();
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
}
