package com.applitools.eyes.demo;

import com.applitools.eyes.*;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class BasicDemo extends ReportingTestSuite {
    private static BatchInfo batch;
    private WebDriver driver;
    private final LogHandler logger = new StdoutLogHandler();

    public BasicDemo(){
        super.setGroupName("selenium");
    }

    @DataProvider(name = "booleanDP")
    public Object[] dp() {
        return new Object[]{Boolean.TRUE, Boolean.FALSE};
    }

    @BeforeClass
    public static void beforeAll() {
        if (TestUtils.runOnCI && System.getenv("TRAVIS") != null) {
            System.setProperty("webdriver.chrome.driver", "/home/travis/build/chromedriver"); // for travis build.
        }

        batch = new BatchInfo("Basic Sanity");
    }

    @BeforeMethod
    public void beforeEach() {
    }

    @Test
    public void basicDemo() throws MalformedURLException, InterruptedException {
        DesiredCapabilities caps = DesiredCapabilities.firefox();
        caps.setCapability("platform", "Mac OS X 10.15");
        caps.setCapability("version", "78.0");
        caps.setCapability("screenResolution", "1400x1050");
        caps.setCapability("name", "Charter");
        String username = System.getenv("SAUCE_USERNAME");
        String accessKey = System.getenv("SAUCE_ACCESS_KEY");
        String domain = "@ondemand.saucelabs.com:443/wd/hub";
        String url = "https://" + username + ":" + accessKey + domain;
        driver = new ChromeDriver();
        driver.get("https://www.nintendo.com/switch/");
        Thread.sleep(1000);

        Eyes eyes = new Eyes();
        eyes.setLogHandler(new StdoutLogHandler(true));
        eyes.setProxy(new ProxySettings("http://localhost:8888"));
        try {
            eyes.open(driver, "test", "test", new RectangleSize(1200, 600));
            eyes.check(Target.window().fully());
            eyes.close();
        } finally {
            driver.quit();
            eyes.abortIfNotClosed();
        }
    }
}