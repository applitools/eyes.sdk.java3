package com.applitools.eyes.selenium;

import com.applitools.eyes.AutProxyMode;
import com.applitools.eyes.AutProxySettings;
import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.utils.SeleniumUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestAutProxy {

    @Test
    public void testAutProxySettingsWithProxySettings() {
        ProxySettings proxySettings = new ProxySettings("http://127.0.0.1", 8080);
        AutProxySettings autProxySettings = new AutProxySettings(proxySettings);

        Assert.assertEquals(autProxySettings.getProxy(), proxySettings);
        Assert.assertNull(autProxySettings.getDomains());
        Assert.assertNull(autProxySettings.getAutProxyMode());
    }

    @Test
    public void testAutProxySettingsWithProxySettingsAndDomains() {
        ProxySettings proxySettings = new ProxySettings("http://127.0.0.1", 8080);
        String[] domains = new String[]{"a.com", "b.com"};
        AutProxySettings autProxySettings = new AutProxySettings(proxySettings, domains);

        Assert.assertEquals(autProxySettings.getProxy(), proxySettings);
        Assert.assertEquals(autProxySettings.getDomains(), domains);
        Assert.assertEquals(autProxySettings.getAutProxyMode(), AutProxyMode.ALLOW);
    }

    @Test
    public void testAutProxySettingsWithProxySettingsWithDomainsAndAutModeAllow() {
        ProxySettings proxySettings = new ProxySettings("http://127.0.0.1", 8080);
        String[] domains = new String[]{"a.com", "b.com"};
        AutProxyMode proxyMode = AutProxyMode.ALLOW;
        AutProxySettings autProxySettings = new AutProxySettings(proxySettings, domains, proxyMode);

        Assert.assertEquals(autProxySettings.getProxy(), proxySettings);
        Assert.assertEquals(autProxySettings.getDomains(), domains);
        Assert.assertEquals(autProxySettings.getAutProxyMode(), proxyMode);
    }

    @Test
    public void testAutProxySettingsWithProxySettingsWithDomainsAndAutModeBlock() {
        ProxySettings proxySettings = new ProxySettings("http://127.0.0.1", 8080);
        String[] domains = new String[]{"a.com", "b.com"};
        AutProxyMode proxyMode = AutProxyMode.BLOCK;
        AutProxySettings autProxySettings = new AutProxySettings(proxySettings, domains, proxyMode);

        Assert.assertEquals(autProxySettings.getProxy(), proxySettings);
        Assert.assertEquals(autProxySettings.getDomains(), domains);
        Assert.assertEquals(autProxySettings.getAutProxyMode(), proxyMode);
    }

    @Test
    public void testAutProxyFromConfiguration() {
        Configuration configuration = new Configuration();
        AutProxySettings autProxySettings = new AutProxySettings(new ProxySettings("http://127.0.0.1", 8080));
        configuration.setAutProxy(autProxySettings);

        Assert.assertEquals(configuration.getAutProxy(), autProxySettings);
    }

    @Test
    private void shouldSucceedWithAutProxy() {
        WebDriver driver = SeleniumUtils.createChromeDriver();
        try {
            Eyes eyes = new Eyes();

            Configuration configuration = new Configuration();
            AutProxySettings autProxySettings = new AutProxySettings(new ProxySettings("http://127.0.0.1", 8080));
            configuration.setAutProxy(autProxySettings);
            eyes.setConfiguration(configuration);

            eyes.open(driver, "AutProxyTest", "autProxy test");
            Assert.assertTrue(eyes.getIsOpen());
            eyes.close();
        } finally {
            driver.quit();
        }
    }

    @Test
    private void shouldSucceedWithAutProxyWithDomains() {
        WebDriver driver = SeleniumUtils.createChromeDriver();
        try {
            Eyes eyes = new Eyes();

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
            driver.quit();
        }
    }

    @Test
    private void shouldSucceedWithAutProxyWithDomainsAndAutModeAllow() {
        WebDriver driver = SeleniumUtils.createChromeDriver();
        try {
            Eyes eyes = new Eyes();

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
            driver.quit();
        }
    }

    @Test
    private void shouldSucceedWithAutProxyWithDomainsAndAutModeBlock() {
        WebDriver driver = SeleniumUtils.createChromeDriver();
        try {
            Eyes eyes = new Eyes();

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
            driver.quit();
        }
    }
}
