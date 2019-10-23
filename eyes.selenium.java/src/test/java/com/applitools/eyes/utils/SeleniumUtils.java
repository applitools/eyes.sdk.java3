package com.applitools.eyes.utils;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

public class SeleniumUtils {
    public static ChromeDriver createChromeDriver() {
        ChromeOptions options = new ChromeOptions();
        return createChromeDriver(options);
    }

    public static ChromeDriver createChromeDriver(ChromeOptions options) {
        if (TestUtils.runHeadless) {
            options.setHeadless(true);
        }
        return new ChromeDriver(options);
    }

    public static WebDriver createWebDriver(Capabilities options) {
        WebDriver webDriver;
        switch (options.getBrowserName()) {
            case "firefox":
                webDriver = new FirefoxDriver((FirefoxOptions) options);
                break;
            case "safari":
                webDriver = new SafariDriver((SafariOptions) options);
                break;
            case "internet explorer":
                webDriver = new InternetExplorerDriver((InternetExplorerOptions) options);
                break;
            case "edge":
                webDriver = new EdgeDriver((EdgeOptions) options);
                break;
            default:
                webDriver = new ChromeDriver((ChromeOptions) options);
                break;
        }

        return webDriver;
    }
}
