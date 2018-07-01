package com.applitools.eyes.selenium;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.selenium.fluent.Target;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.Locale;

public class TestNicorette {
    @Test
    public void TestNicoretteProductsPage() {
        Eyes eyes = new Eyes(URI.create("https://eyes.applitools.com"));
        ChromeOptions options = new ChromeOptions();
        if (System.getenv("APPLITOOLS_RUN_HEADLESS").equalsIgnoreCase("TRUE"))
        {
            options.addArguments("headless");
        }
        WebDriver webDriver = new ChromeDriver();
        try {
            WebDriver driver = eyes.open(webDriver,
                    this.getClass().getSimpleName(),
                    this.getClass().getSimpleName(), new RectangleSize(1000, 800));

            eyes.setStitchMode(StitchMode.CSS);
            driver.get("https://www.nicorette.es/productos");

            eyes.check("Products", Target.window().fully());

            eyes.close();
        }
        finally {
            webDriver.quit();
            eyes.abortIfNotClosed();
        }
    }
}
