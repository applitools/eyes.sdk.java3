package com.applitools.eyes.driver;

import com.applitools.eyes.EyesException;
import com.applitools.eyes.selenium.Eyes;
//import com.applitools.eyes.selenium.universal.dto.DriverTargetDto;
//import com.applitools.eyes.selenium.universal.mapper.DriverMapper;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class TestCustomDriver {

    private final String USERNAME = GeneralUtils.getEnvString("SAUCE_USERNAME");
    private final String ACCESS_KEY = GeneralUtils.getEnvString("SAUCE_ACCESS_KEY");
    private final String SL_URL = "https://"+USERNAME+":" + ACCESS_KEY + "@ondemand.us-west-1.saucelabs.com:443/wd/hub";

    private MutableCapabilities capabilities;

    @BeforeTest
    public void before() {
        capabilities = new MutableCapabilities();
        capabilities.setCapability("browserName", "Chrome");
        capabilities.setCapability("browserVersion", "latest");

        MutableCapabilities sauceOptions = new MutableCapabilities();
        sauceOptions.setCapability("idleTimeout", 300);
        capabilities.setCapability("sauce:options", sauceOptions);
    }

    @Test
    public void testEyesOpenWithCustomDriver() throws MalformedURLException {
        WebDriver driver = new CustomDriver(new URL(SL_URL), capabilities);
        Eyes eyes = new Eyes();

        try {
            eyes.open(driver, "TestEyesOpenWithCustomDriver", "TestEyesOpenWithCustomDriver");
            Assert.assertTrue(eyes.getIsOpen());
        } finally {
            driver.quit();
            eyes.abort();
        }
    }

    @Test
    public void shouldFailEyesOpenWithWrongCustomDriver() throws MalformedURLException {

        WebDriver driver = new WrongCustomDriver(new URL(SL_URL), capabilities);
        Eyes eyes = new Eyes();

        try {
            eyes.open(driver, "ShouldFailEyesOpenWithWrongCustomDriver", "ShouldFailEyesOpenWithWrongCustomDriver");
        } catch (EyesException e) {
            Assert.assertEquals(e.getMessage(), "Unsupported webDriver implementation");
        } finally {
            driver.quit();
            eyes.abort();
        }
    }

}

class CustomDriver extends RemoteWebDriver {

    private RemoteWebDriver internalDriver;

    public CustomDriver(URL url, MutableCapabilities caps) {
        internalDriver = new RemoteWebDriver(url, caps);
    }

    @Override
    public SessionId getSessionId() {
        return internalDriver.getSessionId();
    }

    @Override
    public CommandExecutor getCommandExecutor() {
        return internalDriver.getCommandExecutor();
    }

    @Override
    public Capabilities getCapabilities() {
        return internalDriver.getCapabilities();
    }
}

class WrongCustomDriver extends RemoteWebDriver {

    private RemoteWebDriver internalDriver;

    public WrongCustomDriver(URL url, MutableCapabilities caps) {
        internalDriver = new RemoteWebDriver(url, caps);
    }

    @Override
    public SessionId getSessionId() {
        return internalDriver.getSessionId();
    }
}
