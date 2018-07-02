package com.applitools.eyes.selenium;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.selenium.fluent.Target;
import org.testng.annotations.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.net.URI;

public class TestServerConnector {

    @Test
    public void TestDelete() {
        Eyes eyes = new Eyes(URI.create("https://localhost.applitools.com"));
        WebDriver webDriver = new ChromeDriver();
        try {
            WebDriver driver = eyes.open(webDriver,
                    TestServerConnector.class.getSimpleName(),
                    TestServerConnector.class.getSimpleName(), new RectangleSize(800, 599));

            driver.get("https://applitools.com/helloworld");

            eyes.check("Hello", Target.window());

            TestResults results = eyes.close();

            results.delete();
        }
        finally {
            webDriver.quit();
            eyes.abortIfNotClosed();
        }
    }

}
