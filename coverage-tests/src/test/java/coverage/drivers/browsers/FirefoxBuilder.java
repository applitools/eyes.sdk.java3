package coverage.drivers.browsers;

import coverage.GlobalSetup;
import coverage.drivers.SELENIUM;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

public class FirefoxBuilder implements Builder {

    public WebDriver build(boolean headless, boolean legacy, boolean executionGrid) throws MalformedURLException {
        FirefoxOptions options = new FirefoxOptions().setHeadless(headless);
        if (GlobalSetup.CI) {
            options.addArguments("--no-sandbox");
        }
        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        capabilities.setCapability("marionette", true);
        capabilities.merge(options);
        if (GlobalSetup.useDocker) return new RemoteWebDriver(new URL(SELENIUM.FIREFOX_LOCAL.url), capabilities);
        return new FirefoxDriver(capabilities);
    }
}
