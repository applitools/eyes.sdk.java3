package com.applitools.eyes.selenium;

import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;

public class TestDefaultRootElement extends ReportingTestSuite {

    public TestDefaultRootElement() {
        super.setGroupName("selenium");
    }

    @Test
    public void testBodyGreaterThanHtml() {
        EyesRunner runner =  new ClassicRunner();
        Eyes eyes = new Eyes(runner);
        eyes.setLogHandler(new StdoutLogHandler());
        ChromeDriver driver = SeleniumUtils.createChromeDriver();
        try {
            eyes.open(driver, "Applitools Eyes SDK", "Test Body Greater Than Html", new RectangleSize(1220, 800));
            driver.get("https://applitools.github.io/demo/TestPages/TestBodyGreaterThanHtml/");
            driver.executeScript("window.scrollTo(0, document.body.scrollHeight)");
            WebElement element = driver.findElement(By.cssSelector("html > body > div"));
            eyes.check(Target.region(element).fully());
            eyes.closeAsync();
        } finally {
            driver.quit();
            eyes.abortAsync();
            runner.getAllTestResults();
        }
    }
}
