package com.applitools.eyes.driver;

import com.applitools.eyes.EyesException;
import com.applitools.eyes.selenium.Eyes;
//import com.applitools.eyes.selenium.universal.dto.DriverTargetDto;
//import com.applitools.eyes.selenium.universal.mapper.DriverMapper;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class TestCustomDriver {

    @Test
    public void testEyesOpenWithCustomDriver() throws MalformedURLException {

        WebDriver driver = new CustomDriver(new URL("https://applitools:zBo67o7BsoKhdkf8Va4u@hub-cloud.browserstack.com/wd/hub"),
                new DesiredCapabilities("chrome", "", Platform.ANY));
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

        WebDriver driver = new WrongCustomDriver(new URL("https://applitools:zBo67o7BsoKhdkf8Va4u@hub-cloud.browserstack.com/wd/hub"),
                new DesiredCapabilities("chrome", "", Platform.ANY));
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

    public CustomDriver(URL url, DesiredCapabilities caps) {
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
}

class WrongCustomDriver extends RemoteWebDriver {

    private RemoteWebDriver internalDriver;

    public WrongCustomDriver(URL url, DesiredCapabilities caps) {
        internalDriver = new RemoteWebDriver(url, caps);
    }

    @Override
    public SessionId getSessionId() {
        return internalDriver.getSessionId();
    }
}
